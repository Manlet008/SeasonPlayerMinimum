package manlet.seasonplayerminimum.mixin;

import manlet.seasonplayerminimum.SeasonPlayerMinimum;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sereneseasons.season.SeasonHandler;

import java.util.HashMap;

@Mixin(value = SeasonHandler.class, priority = SeasonPlayerMinimum.MIXIN_PRIORITY, remap = false)
public class MixinSeasonHandler
{
    @Unique
    private static final HashMap<Level, Long> seasonplayerminimum$dayTimeOffset = new HashMap<>();
    @Unique
    private static final HashMap<Level, Long> seasonplayerminimum$lastRealDayTime = new HashMap<>();

    // Feeds onLevelTick a day time with any player-minimum-paused duration subtracted out, so the
    // season clock stays frozen while paused and resumes exactly where it left off - instead of
    // computing a real difference that includes the paused duration and jumping forward to "catch
    // up" once ticking is no longer paused.
    @Redirect(
        method = "onLevelTick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getDayTime()J", remap = true),
        remap = false
    )
    private static long seasonplayerminimum$getEffectiveDayTime(Level level)
    {
        return seasonplayerminimum$computeEffectiveDayTime(level, level.getDayTime());
    }

    // Compatibility with "Serene Seasons Fix - Revived": when its enable_override config is on,
    // it cancels vanilla's onLevelTick body outright and reimplements season progression itself
    // inside its own @Inject callback, reading time via Level#getLevelData()#getDayTime() instead
    // of Level#getDayTime(). That callback is merged into this same class under the same method
    // name with an extra CallbackInfo parameter, so it's targeted here by its full descriptor.
    // require = 0: silently does nothing if that mod isn't installed, or if a future version
    // changes this call site, rather than crashing startup.
    @Redirect(
        method = "onLevelTick(Lglitchcore/event/TickEvent$Level;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelData;getDayTime()J", remap = true),
        remap = false,
        require = 0
    )
    private static long seasonplayerminimum$getEffectiveDayTimeFixCompat(LevelData levelData)
    {
        long real = levelData.getDayTime();

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            return real;

        for (ServerLevel level : server.getAllLevels())
        {
            if (level.getLevelData() == levelData)
                return seasonplayerminimum$computeEffectiveDayTime(level, real);
        }

        return real;
    }

    @Unique
    private static long seasonplayerminimum$computeEffectiveDayTime(Level level, long real)
    {
        MinecraftServer server = level.getServer();
        int minimumPlayers = level.getGameRules().getInt(SeasonPlayerMinimum.RULE_SEASON_CYCLE_PLAYER_MINIMUM);
        boolean paused = server != null && server.getPlayerList().getPlayerCount() < minimumPlayers;

        long offset = seasonplayerminimum$dayTimeOffset.getOrDefault(level, 0L);
        if (paused)
        {
            long lastReal = seasonplayerminimum$lastRealDayTime.getOrDefault(level, real);
            offset += real - lastReal;
            seasonplayerminimum$dayTimeOffset.put(level, offset);
        }
        seasonplayerminimum$lastRealDayTime.put(level, real);

        return real - offset;
    }
}

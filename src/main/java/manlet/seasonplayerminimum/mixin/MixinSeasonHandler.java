package manlet.seasonplayerminimum.mixin;

import glitchcore.event.TickEvent;
import manlet.seasonplayerminimum.SeasonPlayerMinimum;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sereneseasons.season.SeasonHandler;

@Mixin(value = SeasonHandler.class, priority = SeasonPlayerMinimum.MIXIN_PRIORITY, remap = false)
public class MixinSeasonHandler
{
    @Inject(method = "onLevelTick", at = @At("HEAD"), cancellable = true, remap = false)
    private static void seasonplayerminimum$onLevelTick(TickEvent.Level event, CallbackInfo ci)
    {
        // Deliberately not filtered to Phase.START: Serene Seasons Fix - Revived does its own
        // season progression on Phase.END (bypassing SeasonHandler's own logic entirely), so we
        // need to cancel on both phases to block it as well as vanilla Serene Seasons.
        Level level = event.getLevel();
        if (level.isClientSide())
            return;

        MinecraftServer server = level.getServer();
        if (server == null)
            return;

        int minimumPlayers = level.getGameRules().getInt(SeasonPlayerMinimum.RULE_SEASON_CYCLE_PLAYER_MINIMUM);
        if (server.getPlayerList().getPlayerCount() < minimumPlayers)
        {
            // Keep lastDayTimes in sync with the world's day time while paused, otherwise the
            // elapsed time gets banked and slams into seasonCycleTicks all at once on resume.
            SeasonHandler.lastDayTimes.put(level, level.getDayTime());
            ci.cancel();
        }
    }
}

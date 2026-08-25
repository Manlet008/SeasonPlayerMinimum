package manlet.seasonplayerminimum;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Tracks whether season progression should be frozen because too few players are online.
 *
 * The real doSeasonCycle gamerule is never touched - MixinGameRules makes Serene Seasons (and
 * Serene Seasons Fix - Revived) see it as false while paused, without changing what's actually
 * stored, so an admin's own gamerule setting and its visibility in /gamerule are unaffected.
 */
@Mod.EventBusSubscriber(modid = SeasonPlayerMinimum.MOD_ID)
public class SeasonCyclePauseHandler
{
    private static volatile boolean paused = false;

    public static boolean isPaused()
    {
        return paused;
    }

    public static void recheckAll(MinecraftServer server)
    {
        if (server == null)
            return;

        int minimumPlayers = server.overworld().getGameRules().getInt(SeasonPlayerMinimum.RULE_SEASON_CYCLE_PLAYER_MINIMUM);
        paused = server.getPlayerList().getPlayerCount() < minimumPlayers;
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event)
    {
        recheckAll(event.getServer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        recheckAll(event.getEntity().level().getServer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        recheckAll(event.getEntity().level().getServer());
    }
}

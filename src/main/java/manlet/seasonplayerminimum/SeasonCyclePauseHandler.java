package manlet.seasonplayerminimum;

import manlet.seasonplayerminimum.config.ServerConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sereneseasons.api.SSGameRules;

@Mod.EventBusSubscriber(modid = SeasonPlayerMinimum.MOD_ID)
public class SeasonCyclePauseHandler
{
    // Translation keys for the messages sent on each pause/resume transition - see
    // assets/seasonplayerminimum/lang for the actual wording. Each takes (current player count,
    // configured minimum) as its arguments.
    public static final String KEY_PAUSED = "seasonplayerminimum.message.paused";
    public static final String KEY_RESUMED = "seasonplayerminimum.message.resumed";
    public static final String KEY_RESUMED_BUT_DISABLED = "seasonplayerminimum.message.resumed_but_disabled";

    private static final Logger LOGGER = LogManager.getLogger(SeasonPlayerMinimum.MOD_ID);

    private static volatile boolean paused = false;
    // 0, not Long.MIN_VALUE - System.currentTimeMillis() minus MIN_VALUE overflows a long and
    // wraps negative, which would fail the cooldown check below and silently skip the very first
    // broadcast ever attempted.
    private static long lastBroadcastTimeMillis = 0L;

    public static boolean isPaused()
    {
        return paused;
    }

    public static void recheckAll(MinecraftServer server)
    {
        if (server == null)
            return;

        GameRules rules = server.overworld().getGameRules();
        int minimumPlayers = rules.getInt(SeasonPlayerMinimum.RULE_SEASON_CYCLE_PLAYER_MINIMUM);
        int playerCount = server.getPlayerList().getPlayerCount();
        boolean shouldPause = playerCount < minimumPlayers;

        if (shouldPause == paused)
            return;

        paused = shouldPause;
        // DEFAULT_MINIMUM_PLAYERS = 0
        if (minimumPlayers == SeasonPlayerMinimum.DEFAULT_MINIMUM_PLAYERS)
            return;


        boolean realDoSeasonCycle = rules.getRule(SSGameRules.RULE_DOSEASONCYCLE).get();
        if (!realDoSeasonCycle && !ServerConfig.printMessagesWhenSeasonCycleDisabled.get())
            return;

        String key = paused ? KEY_PAUSED : (realDoSeasonCycle ? KEY_RESUMED : KEY_RESUMED_BUT_DISABLED);
        Component message = Component.translatable(key, playerCount, minimumPlayers);

        if (ServerConfig.printMessagesToConsole.get())
            LOGGER.info(message.getString());

        if (ServerConfig.displayMessagesOnServer.get())
        {
            long now = System.currentTimeMillis();
            long cooldownMillis = ServerConfig.serverMessageCooldownSeconds.get() * 1000L;
            if (now - lastBroadcastTimeMillis >= cooldownMillis)
            {
                lastBroadcastTimeMillis = now;
                server.getPlayerList().broadcastSystemMessage(message, false);
            }
        }
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

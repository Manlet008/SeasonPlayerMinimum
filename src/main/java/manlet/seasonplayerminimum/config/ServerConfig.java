package manlet.seasonplayerminimum.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue printMessagesToConsole;
    public static ForgeConfigSpec.BooleanValue printMessagesWhenSeasonCycleDisabled;
    public static ForgeConfigSpec.BooleanValue displayMessagesOnServer;
    public static ForgeConfigSpec.IntValue serverMessageCooldownSeconds;

    static
    {
        BUILDER.push("general");
        printMessagesToConsole = BUILDER
            .comment("Message the console each time the season cycle pauses or resumes because of the seasonCyclePlayerMinimum gamerule. On by default.")
            .define("print_messages_to_console", true);
        displayMessagesOnServer = BUILDER
                .comment("Alert the server when the player count drops below or rises above the required count. Off by default.")
                .define("display_message_on_server", false);
        printMessagesWhenSeasonCycleDisabled = BUILDER
            .comment("Whether to still log those messages while doSeasonCycle is already set to false. Off by default")
            .define("print_messages_if_doseasoncycle_false", false);
        serverMessageCooldownSeconds = BUILDER
            .comment("Minimum seconds between chat messages to prevent spam. Only limits the chat, not the console log.")
            .defineInRange("server_message_cooldown_seconds", 60, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}

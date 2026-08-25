package manlet.seasonplayerminimum;

import manlet.seasonplayerminimum.config.ServerConfig;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static net.minecraft.world.level.GameRules.register;

@Mod(SeasonPlayerMinimum.MOD_ID)
public class SeasonPlayerMinimum
{
    public static final String MOD_ID = "seasonplayerminimum";

    /** Default value for the seasonCyclePlayerMinimum gamerule - Stops any of the logic from running*/
    public static final int DEFAULT_MINIMUM_PLAYERS = 0;

    public static GameRules.Key<GameRules.IntegerValue> RULE_SEASON_CYCLE_PLAYER_MINIMUM;

    public SeasonPlayerMinimum(FMLJavaModLoadingContext context)
    {
        RULE_SEASON_CYCLE_PLAYER_MINIMUM = register("seasonCyclePlayerMinimum", GameRules.Category.UPDATES, GameRules.IntegerValue.create(DEFAULT_MINIMUM_PLAYERS, (server, rule) -> SeasonCyclePauseHandler.recheckAll(server)));

        context.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC, "seasonplayerminimum-server.toml");
    }
}

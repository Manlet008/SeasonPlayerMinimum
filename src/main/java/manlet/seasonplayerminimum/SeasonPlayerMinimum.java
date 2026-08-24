package manlet.seasonplayerminimum;

import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.world.level.GameRules.register;

@Mod(SeasonPlayerMinimum.MOD_ID)
public class SeasonPlayerMinimum
{
    public static final String MOD_ID = "seasonplayerminimum";

    /** Default value for the seasonCyclePlayerMinimum gamerule - 0 disables the freeze entirely. */
    public static final int DEFAULT_MINIMUM_PLAYERS = 0;

    /**
     * Lower than Mixin's default priority (1000) so MixinSeasonHandler cancels the tick before
     * Serene Seasons and before any other mixins that have a default priority.
     */
    public static final int MIXIN_PRIORITY = 990;

    public static GameRules.Key<GameRules.IntegerValue> RULE_SEASON_CYCLE_PLAYER_MINIMUM;

    public SeasonPlayerMinimum()
    {
        // IntegerValue.create is package-private in vanilla, but public via Forge's own official
        // access-widening patches - no reflection needed on the Forge side.
        RULE_SEASON_CYCLE_PLAYER_MINIMUM = register("seasonCyclePlayerMinimum", GameRules.Category.UPDATES, GameRules.IntegerValue.create(DEFAULT_MINIMUM_PLAYERS, (server, rule) -> {}));
    }
}

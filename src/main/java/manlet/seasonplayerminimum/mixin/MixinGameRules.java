package manlet.seasonplayerminimum.mixin;

import manlet.seasonplayerminimum.SeasonCyclePauseHandler;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.api.SSGameRules;

// Targets the plain vanilla GameRules class, not another mod's mixin-contributed method, so this
// doesn't depend on Serene Seasons Fix - Revived's internals or any mixin ordering between mods.
@Mixin(GameRules.class)
public class MixinGameRules
{
    @Inject(method = "getBoolean", at = @At("RETURN"), cancellable = true)
    private void seasonplayerminimum$overrideDoSeasonCycle(GameRules.Key<GameRules.BooleanValue> key, CallbackInfoReturnable<Boolean> cir)
    {
        if (key == SSGameRules.RULE_DOSEASONCYCLE && SeasonCyclePauseHandler.isPaused())
            cir.setReturnValue(false);
    }
}

package de.rettichlp.ucutils.mixin;

import de.rettichlp.ucutils.common.models.Countdown;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.ucutils.UCUtils.storage;
import static java.time.Duration.ofMillis;
import static net.minecraft.entity.effect.StatusEffects.HERO_OF_THE_VILLAGE;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z", at = @At("RETURN"))
    private void ucutils$addStatusEffectReturn(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        if (cir.getReturnValue() && effect.getEffectType().equals(HERO_OF_THE_VILLAGE)) {
            int durationInTicks = effect.getDuration();
            int durationInMillis = durationInTicks * 50;
            storage.getCountdowns().add(new Countdown("Absorption", ofMillis(durationInMillis)));
        }
    }
}

package de.rettichlp.ucutils.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static net.minecraft.client.sounds.SoundEngine.PlayResult.NOT_STARTED;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

@Mixin(SoundManager.class)
public abstract class SoundManagerMixin {

    @Unique
    private static final Identifier MALLE_SOUND_IDENTIFIER = fromNamespaceAndPath("ucmusic", "music.malle_i_love_you");

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void ucutils$playHead(SoundInstance instance, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        if (configuration.getOptions().miscellaneous().blockMalleSound() && MALLE_SOUND_IDENTIFIER.equals(instance.getIdentifier())) {
            cir.setReturnValue(NOT_STARTED);
        }
    }
}

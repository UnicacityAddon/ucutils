package de.rettichlp.ucutils.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.ucutils.UCUtils.nameTagService;
import static de.rettichlp.ucutils.UCUtils.storage;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void ucutils$getDisplayNameReturn(@NotNull CallbackInfoReturnable<Text> cir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        GameProfile gameProfile = ((PlayerEntity) (Object) this).getGameProfile();
        cir.setReturnValue(nameTagService.getEnrichedDisplayName(gameProfile.name()));
    }
}

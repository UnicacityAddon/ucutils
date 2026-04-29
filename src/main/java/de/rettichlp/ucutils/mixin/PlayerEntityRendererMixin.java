package de.rettichlp.ucutils.mixin;

import lombok.NonNull;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.ucutils.UCUtils.nameTagService;
import static java.util.Optional.ofNullable;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.GOLD;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Inject(method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"))
    private void ucutils$renderLabelIfPresent(@NonNull PlayerEntityRenderState playerEntityRenderState,
                                              @NonNull MatrixStack matrixStack,
                                              @NonNull OrderedRenderCommandQueue orderedRenderCommandQueue,
                                              CameraRenderState cameraRenderState,
                                              CallbackInfo ci) {
        ofNullable(playerEntityRenderState.displayName) // something like "[HV] RettichLP ⌜✚⌟"
                .map(nameTagService::revertEnrichment) // something like "RettichLP"
                .filter(nameTagService::isAfk)
                .ifPresent(playerName -> {
                    matrixStack.translate(0.0F, 1.3, 0.0F);
                    matrixStack.scale(0.5F, 0.5F, 0.5F);
                    orderedRenderCommandQueue.submitLabel(matrixStack, playerEntityRenderState.nameLabelPos, 0, literal("ᴀꜰᴋ").formatted(GOLD), !playerEntityRenderState.sneaking, playerEntityRenderState.light, playerEntityRenderState.squaredDistanceToCamera, cameraRenderState);
                });
    }
}

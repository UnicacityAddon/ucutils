package de.rettichlp.ucutils.mixin;

import lombok.NonNull;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.nameTagService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.util.Optional.ofNullable;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.BLUE;
import static net.minecraft.util.Formatting.BOLD;
import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.RED;

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
        if (!storage.isUnicaCity()) {
            return;
        }

        ofNullable(playerEntityRenderState.displayName) // something like "[HV] RettichLP ⌜✚⌟"
                .map(nameTagService::revertEnrichment) // something like "RettichLP"
                .ifPresent(playerName -> {
                    matrixStack.scale(0.5F, 0.5F, 0.5F);

                    // handle medical information (bandages + pills)
                    MutableText medicInformation = nameTagService.getMedicInformation(playerName);
                    boolean medicInformationPresent = !medicInformation.getSiblings().isEmpty() && configuration.getOptions().nameTag().additionalMedicalInformation();
                    if (medicInformationPresent) {
                        matrixStack.translate(0.0F, 1.8F, 0.0F);
                        orderedRenderCommandQueue.submitLabel(matrixStack, playerEntityRenderState.nameLabelPos, 0, medicInformation, !playerEntityRenderState.sneaking, playerEntityRenderState.light, playerEntityRenderState.squaredDistanceToCamera, cameraRenderState);
                    }

                    if (nameTagService.isAfk(playerName)) {
                        matrixStack.translate(0.0F, medicInformationPresent ? 0.8F : 2.6, 0.0F);
                        orderedRenderCommandQueue.submitLabel(matrixStack, playerEntityRenderState.nameLabelPos, 0, literal("ᴀꜰᴋ").formatted(GOLD, BOLD), !playerEntityRenderState.sneaking, playerEntityRenderState.light, playerEntityRenderState.squaredDistanceToCamera, cameraRenderState);
                    } else if (nameTagService.isADuty(playerName)) {
                        MutableText text = empty()
                                .append(literal("ᴀ").formatted(BLUE, BOLD))
                                .append(literal("ᴅᴜᴛʏ").formatted(RED, BOLD));

                        matrixStack.translate(0.0F, medicInformationPresent ? 0.8F : 2.6, 0.0F);
                        orderedRenderCommandQueue.submitLabel(matrixStack, playerEntityRenderState.nameLabelPos, 0, text, !playerEntityRenderState.sneaking, playerEntityRenderState.light, playerEntityRenderState.squaredDistanceToCamera, cameraRenderState);
                    }
                });
    }
}

package de.rettichlp.ucutils.mixin;

import lombok.NonNull;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.nameTagService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.BLUE;
import static net.minecraft.util.Formatting.BOLD;
import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.RED;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Unique
    private static final MutableText A_DUTY_TEXT = empty()
            .append(literal("ᴀ").formatted(BLUE, BOLD))
            .append(literal("ᴅᴜᴛʏ").formatted(RED, BOLD));

    @Unique
    private static final MutableText AFK_TEXT = literal("ᴀꜰᴋ").formatted(GOLD, BOLD);

    @Unique
    private static final MutableText HOUSE_BAN_TEXT = literal("Hᴀᴜѕᴠᴇʀʙᴏᴛ").formatted(RED, BOLD);

    @Unique
    private static final MutableText OUTLAW_TEXT = literal("Vᴏɢᴇʟꜰʀᴇɪ").formatted(RED, BOLD);

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

        // extract player name from click event with tell command
        // empty[style={color=red,clickEvent=class_10610[command=/tell Maagma46 ],hoverEvent=class_10611[entity=net.minecraft.class_2568$class_5248@a9702189],insertion=Maagma46}, siblings=[literal{[}[style={color=dark_gray}, siblings=[literal{UC}[style={color=blue}], literal{]}[style={color=dark_gray}]]], literal{Maagma46}, empty]]
        Text displayName = playerEntityRenderState.displayName;

        if (displayName == null) {
            return;
        }

        if (!(displayName.getStyle().getClickEvent() instanceof ClickEvent.SuggestCommand(String command))) {
            return;
        }

        String playerName = command.replace("/tell ", "").trim();

        matrixStack.scale(0.5F, 0.5F, 0.5F);

        // handle medical information (bandages + pills)
        MutableText medicInformation = nameTagService.getMedicInformation(playerName);
        boolean medicInformationPresent = !medicInformation.getSiblings().isEmpty();
        if (configuration.getOptions().nameTag().medicalInformation() && medicInformationPresent) {
            matrixStack.translate(0.0F, 1.8F, 0.0F);
            orderedRenderCommandQueue.submitLabel(matrixStack, playerEntityRenderState.nameLabelPos, 0, medicInformation, !playerEntityRenderState.sneaking, playerEntityRenderState.light, playerEntityRenderState.squaredDistanceToCamera, cameraRenderState);
        }

        // handle admin duty tag
        if (configuration.getOptions().nameTag().aDuty() && nameTagService.isADuty(playerName)) {
            matrixStack.translate(0.0F, medicInformationPresent ? 0.8F : 2.6, 0.0F);
            orderedRenderCommandQueue.submitLabel(matrixStack, playerEntityRenderState.nameLabelPos, 0, A_DUTY_TEXT, !playerEntityRenderState.sneaking, playerEntityRenderState.light, playerEntityRenderState.squaredDistanceToCamera, cameraRenderState);
            return;
        }

        // handle afk tag
        if (configuration.getOptions().nameTag().afk() && nameTagService.isAfk(playerName)) {
            matrixStack.translate(0.0F, medicInformationPresent ? 0.8F : 2.6, 0.0F);
            orderedRenderCommandQueue.submitLabel(matrixStack, playerEntityRenderState.nameLabelPos, 0, AFK_TEXT, !playerEntityRenderState.sneaking, playerEntityRenderState.light, playerEntityRenderState.squaredDistanceToCamera, cameraRenderState);
            return;
        }

        // handle houseban tag
        boolean hasHouseBan = storage.getHousebanEntries().stream().anyMatch(housebanEntry -> housebanEntry.getPlayerName().equals(playerName));
        if (configuration.getOptions().nameTag().houseBan() && hasHouseBan) {
            matrixStack.translate(0.0F, medicInformationPresent ? 0.8F : 2.6, 0.0F);
            orderedRenderCommandQueue.submitLabel(matrixStack, playerEntityRenderState.nameLabelPos, 0, HOUSE_BAN_TEXT, !playerEntityRenderState.sneaking, playerEntityRenderState.light, playerEntityRenderState.squaredDistanceToCamera, cameraRenderState);
            return;
        }

        // handle outlaw tag
        boolean isOutlaw = storage.getBlacklistEntries().stream()
                .anyMatch(blacklistEntry -> blacklistEntry.getPlayerName().equals(playerName) && blacklistEntry.isOutlaw());
        if (configuration.getOptions().nameTag().outlaw() && isOutlaw) {
            matrixStack.translate(0.0F, medicInformationPresent ? 0.8F : 2.6, 0.0F);
            orderedRenderCommandQueue.submitLabel(matrixStack, playerEntityRenderState.nameLabelPos, 0, OUTLAW_TEXT, !playerEntityRenderState.sneaking, playerEntityRenderState.light, playerEntityRenderState.squaredDistanceToCamera, cameraRenderState);
            return;
        }
    }
}

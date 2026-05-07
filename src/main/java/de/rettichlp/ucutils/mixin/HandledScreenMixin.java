package de.rettichlp.ucutils.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.player;
import static net.minecraft.entity.EquipmentSlot.MAINHAND;
import static net.minecraft.item.Items.BONE_MEAL;
import static net.minecraft.item.Items.WATER_BUCKET;
import static net.minecraft.screen.slot.SlotActionType.PICKUP;

@Mixin(GameMenuScreen.class)
public abstract class HandledScreenMixin extends Screen {

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void ucutils$renderTail(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        HandledScreen<?> self = (HandledScreen<?>) (Object) this;

        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;

        if (interactionManager == null || !(self instanceof GenericContainerScreen genericContainerScreen)) {
            return;
        }

        String title = genericContainerScreen.getTitle().getString();
        switch (title) {
            case "CarControl" -> {
                if (configuration.getOptions().car().fastLock()) {
                    interactionManager.clickSlot(genericContainerScreen.getScreenHandler().syncId, 0, 0, PICKUP, player);
                }
            }
            case "Fahrzeuge" -> {
                if (configuration.getOptions().car().fastFind()) {
                    interactionManager.clickSlot(genericContainerScreen.getScreenHandler().syncId, 0, 0, PICKUP, player);
                }
            }
            case "Plantage" -> {
                ItemStack mainHandStack = player.getEquippedStack(MAINHAND);
                int syncId = genericContainerScreen.getScreenHandler().syncId;
                if (mainHandStack.isOf(WATER_BUCKET)) {
                    interactionManager.clickSlot(syncId, 1, 0, PICKUP, player);
                } else if (mainHandStack.isOf(BONE_MEAL)) {
                    interactionManager.clickSlot(syncId, 7, 0, PICKUP, player);
                }
            }
        }
    }
}

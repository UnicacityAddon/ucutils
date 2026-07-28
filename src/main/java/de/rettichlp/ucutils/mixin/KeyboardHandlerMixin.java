package de.rettichlp.ucutils.mixin;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.ucutils.UCUtils.REINFORCEMENT_ACCEPT_KEY;
import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "keyPress",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/KeyMapping;click(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V",
                     shift = AFTER))
    private void ucutils$keyPressInvoke(long handle, int action, KeyEvent event, CallbackInfo ci) {
        // only with closed chat
        if (!this.minecraft.gui.hud.getChat().isChatFocused()) {
            if (REINFORCEMENT_ACCEPT_KEY.matches(event)) {
                String lastRelevantReinforcementSenderName = storage.getLastRelevantReinforcementSenderName();

                if (lastRelevantReinforcementSenderName.isBlank()) {
                    return;
                }

                commandService.sendCommand("reinforcement omw " + lastRelevantReinforcementSenderName);
            }
        }
    }
}

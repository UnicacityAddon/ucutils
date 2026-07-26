package de.rettichlp.ucutils.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.MOD_ID;
import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {

    @Unique
    private static final KeyMapping.Category KEY_CATEGORY = new KeyMapping.Category(fromNamespaceAndPath(MOD_ID, "ucutils.key.category.name"));

    @Unique
    private static final KeyMapping REINFORCEMENT_ACCEPT_KEY = new KeyMapping("ucutils.key.reinforcement_accept", KEYSYM, GLFW_KEY_O, KEY_CATEGORY);

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "keyPress",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/KeyMapping;matches(Lnet/minecraft/client/input/KeyEvent;)Z",
                     ordinal = 0))
    private void ucutils$keyPressInvoke(long handle, int action, KeyEvent event, CallbackInfo ci) {
        // only with closed chat
        if (!this.minecraft.gui.getChat().isChatFocused()) {
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

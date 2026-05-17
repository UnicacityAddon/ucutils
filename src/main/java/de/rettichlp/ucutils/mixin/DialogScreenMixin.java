package de.rettichlp.ucutils.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.action.DynamicCustomDialogAction;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

import static de.rettichlp.ucutils.UCUtils.storage;

@Mixin(DialogScreen.class)
public abstract class DialogScreenMixin<T extends Dialog> {

    @Shadow
    @Final
    private T dialog;

    @Inject(method = "init", at = @At("TAIL"))
    public void ucutils$initTail(CallbackInfo ci) {
        String fBankDepositReason = storage.getFBankDepositReason();
        if (fBankDepositReason.isBlank()) {
            return;
        }

        DialogScreen<?> self = (DialogScreen<?>) (Object) this;

        if (!(this.dialog instanceof MultiActionDialog multiActionDialog) || !self.getTitle().getString().equals("F-Bank Einzahlung")) {
            return;
        }

        List<DialogActionButtonData> actions = multiActionDialog.actions();
        if (actions.isEmpty()) {
            return;
        }

        DialogActionButtonData dialogActionButtonData = actions.getFirst();
        if (dialogActionButtonData.action().isEmpty() || !(dialogActionButtonData.action().get() instanceof DynamicCustomDialogAction(Identifier id, Optional<NbtCompound> additions))) {
            return;
        }

        additions.ifPresent(nbtCompound -> nbtCompound.putString("grund", fBankDepositReason));
        CustomClickActionC2SPacket customClickActionC2SPacket = new CustomClickActionC2SPacket(id, additions.map(nbt -> (NbtElement) nbt));

        MinecraftClient client = MinecraftClient.getInstance();
        client.getNetworkHandler().sendPacket(customClickActionC2SPacket);
        client.setScreen(null);
    }
}

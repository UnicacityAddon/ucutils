package de.rettichlp.ucutils.mixin;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.ucutils.UCUtils.storage;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.BOLD;
import static net.minecraft.util.Formatting.RED;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void ucutils$getPlayerNameReturn(PlayerListEntry playerListEntry, @NotNull CallbackInfoReturnable<Text> cir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        String playerName = playerListEntry.getProfile().name();
        Text originText = cir.getReturnValue();

        MutableText text = null;

        boolean isWanted = storage.getWantedEntries().stream()
                .anyMatch(wantedEntry -> wantedEntry.getPlayerName().equals(playerName));

        if (isWanted) {
            text = literal(" 🔍").formatted(RED, BOLD);
        }

        if (text != null) {
            cir.setReturnValue(originText.copy().append(" ").append(text));
        }
    }
}

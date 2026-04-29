package de.rettichlp.ucutils.mixin;

import de.rettichlp.ucutils.common.models.BlacklistEntry;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static de.rettichlp.ucutils.UCUtils.networkHandler;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.util.Comparator.comparing;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.TextColor.fromFormatting;
import static net.minecraft.util.Formatting.BLUE;
import static net.minecraft.util.Formatting.BOLD;
import static net.minecraft.util.Formatting.DARK_BLUE;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.DARK_RED;
import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.WHITE;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    @Unique
    private static final Comparator<PlayerListEntry> UCUTILS_ENTRY_ORDERING = comparing((PlayerListEntry playerListEntry) -> {
        String playerName = playerListEntry.getProfile().name();
        Text displayName = playerListEntry.getDisplayName();

        if (displayName == null) {
            return 6; // OTHER
        }

        List<Text> siblings = displayName.getSiblings();

        if (siblings.isEmpty()) {
            return 6; // OTHER
        }

        TextColor firstSiblingStyleColor = siblings.getFirst().getStyle().getColor();

        if (firstSiblingStyleColor == null) {
            return 6; // OTHER
        }

        if (firstSiblingStyleColor.equals(fromFormatting(DARK_GRAY))) {
            TextColor secondSiblingStyleColor = siblings.get(1).getStyle().getColor();
            if (secondSiblingStyleColor != null && secondSiblingStyleColor.equals(fromFormatting(BLUE))) {
                return 0; // ADMIN
            }

            if (secondSiblingStyleColor != null && secondSiblingStyleColor.equals(fromFormatting(GOLD))) {
                return 5; // REPORT
            }

            return 9;
        } else if (firstSiblingStyleColor.equals(fromFormatting(DARK_BLUE))) {
            return 1; // FBI
        } else if (firstSiblingStyleColor.equals(fromFormatting(BLUE))) {
            return 2; // POLICE
        } else if (firstSiblingStyleColor.equals(fromFormatting(DARK_RED))) {
            return 3; // MEDIC
        } else if (firstSiblingStyleColor.equals(fromFormatting(GOLD))) {
            return 4; // NEWS
        } else if (storage.getWantedEntries().stream().anyMatch(wantedEntry -> wantedEntry.getPlayerName().equals(playerName))) {
            return 6; // WANTED
        } else if (storage.getBlacklistEntries().stream().anyMatch(blacklistEntry -> blacklistEntry.getPlayerName().equals(playerName))) {
            return 7; // BLACKLIST
        } else if (storage.getContractEntries().stream().anyMatch(contractEntry -> contractEntry.getPlayerName().equals(playerName))) {
            return 8; // CONTRACT
        } else {
            return 9; // OTHER
        }
    }).thenComparing(playerListEntry -> playerListEntry.getProfile().name());

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

        Optional<BlacklistEntry> optionalBlacklistEntry = storage.getBlacklistEntries().stream()
                .filter(be -> be.getPlayerName().equals(playerName))
                .findFirst();

        if (isWanted) {
            text = literal(" 🔍").formatted(RED, BOLD);
        } else if (optionalBlacklistEntry.isPresent()) {
            text = literal(" 💀").formatted(optionalBlacklistEntry.get().isOutlaw() ? RED : WHITE, BOLD);
        }

        if (text != null) {
            cir.setReturnValue(originText.copy().append(" ").append(text));
        }
    }

    @Inject(method = "collectPlayerEntries", at = @At("RETURN"), cancellable = true)
    private void ucutils$collectPlayerEntriesReturn(@NotNull CallbackInfoReturnable<List<PlayerListEntry>> cir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        // get current player list entries
        Collection<PlayerListEntry> playerListEntries = networkHandler.getListedPlayerListEntries();

        // order player list entries
        List<PlayerListEntry> orderedPlayerListEntries = playerListEntries
                .stream()
                .sorted(UCUTILS_ENTRY_ORDERING)
                .limit(80L)
                .toList();

        // set ordered player list entries before the original finally returns them
        cir.setReturnValue(orderedPlayerListEntries);
    }
}

package de.rettichlp.ucutils.common.services;

import de.rettichlp.ucutils.common.configuration.options.NameTagOptions;
import de.rettichlp.ucutils.common.models.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static de.rettichlp.ucutils.UCUtils.*;
import static de.rettichlp.ucutils.common.models.Color.WHITE;
import static java.time.LocalDateTime.now;
import static net.minecraft.text.Text.*;
import static net.minecraft.util.Formatting.*;

public class FactionService {

    public MutableText getEnrichedDisplayName(String targetName) {
        NameTagOptions nameTagOptions = configuration.getOptions().nameTag();
        Faction targetFaction = storage.getCachedFaction(targetName);

        Text newTargetDisplayNamePrefix = empty();
        Text newTargetDisplayName = literal(targetName);
        Text newTargetDisplayNameSuffix = nameTagOptions.factionInformation() ? targetFaction.getNameTagSuffix() : empty();
        Formatting newTargetDisplayNameColor;

        // highlight factions
        newTargetDisplayNameColor = nameTagOptions.highlightFactions().getOrDefault(targetFaction, WHITE).getFormatting();

        // blacklist
        Optional<BlacklistEntry> optionalTargetBlacklistEntry = storage.getBlacklistEntries().stream()
                .filter(blacklistEntry -> blacklistEntry.getPlayerName().equals(targetName))
                .findAny();

        if (optionalTargetBlacklistEntry.isPresent() && nameTagOptions.additionalBlacklist()) {
            newTargetDisplayNameColor = RED;
            newTargetDisplayNamePrefix = optionalTargetBlacklistEntry.get().isOutlaw()
                    ? empty()
                      .append(of("[").copy().formatted(DARK_GRAY))
                      .append(of("V").copy().formatted(DARK_RED))
                      .append(of("]").copy().formatted(DARK_GRAY))
                    : empty();
        }

        // contract
        Optional<ContractEntry> optionalTargetContractEntry = storage.getContractEntries().stream()
                .filter(contractEntry -> contractEntry.getPlayerName().equals(targetName))
                .findAny();

        if (optionalTargetContractEntry.isPresent() && nameTagOptions.additionalContract()) {
            newTargetDisplayNameColor = RED;
        }

        // houseban
        Optional<HousebanEntry> optionalTargetHousebanEntry = storage.getHousebanEntries().stream()
                .filter(housebanEntry -> housebanEntry.getPlayerName().equals(targetName))
                .filter(housebanEntry -> housebanEntry.getUnbanDateTime().isAfter(now()))
                .findAny();

        if (optionalTargetHousebanEntry.isPresent() && nameTagOptions.additionalHouseban()) {
            newTargetDisplayNamePrefix = empty()
                    .append(of("[").copy().formatted(DARK_GRAY))
                    .append(of("HV").copy().formatted(DARK_RED))
                    .append(of("]").copy().formatted(DARK_GRAY));
        }

        // wanted
        Optional<WantedEntry> optionalTargetWantedEntry = storage.getWantedEntries().stream()
                .filter(wantedEntry -> wantedEntry.getPlayerName().equals(targetName))
                .findAny();

        if (optionalTargetWantedEntry.isPresent() && nameTagOptions.additionalWanted()) {
            newTargetDisplayNameColor = factionService.getWantedPointColor(optionalTargetWantedEntry.get().getWantedPointAmount());
        }

        return empty()
                .append(newTargetDisplayNamePrefix)
                .append(" ")
                .append(newTargetDisplayName.copy().formatted(newTargetDisplayNameColor))
                .append(" ")
                .append(newTargetDisplayNameSuffix);
    }

    public @NotNull Formatting getWantedPointColor(int wantedPointAmount) {
        Formatting color;

        if (wantedPointAmount >= 60) {
            color = DARK_RED;
        } else if (wantedPointAmount >= 50) {
            color = RED;
        } else if (wantedPointAmount >= 25) {
            color = GOLD;
        } else if (wantedPointAmount >= 15) {
            color = YELLOW;
        } else if (wantedPointAmount >= 2) {
            color = GREEN;
        } else {
            color = DARK_GREEN;
        }
        return color;
    }
}

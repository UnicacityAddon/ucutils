package de.rettichlp.ucutils.common.services;

import de.rettichlp.ucutils.common.configuration.options.NameTagOptions;
import de.rettichlp.ucutils.common.models.BlacklistEntry;
import de.rettichlp.ucutils.common.models.ContractEntry;
import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.models.HousebanEntry;
import de.rettichlp.ucutils.common.models.WantedEntry;
import lombok.NonNull;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.networkHandler;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.models.Color.WHITE;
import static java.time.LocalDateTime.now;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.of;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.DARK_GREEN;
import static net.minecraft.util.Formatting.DARK_RED;
import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.YELLOW;

public class NameTagService {

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
            newTargetDisplayNameColor = getWantedPointColor(optionalTargetWantedEntry.get().getWantedPointAmount());
        }

        return empty()
                .append(newTargetDisplayNamePrefix)
                .append(" ")
                .append(newTargetDisplayName.copy().formatted(newTargetDisplayNameColor))
                .append(" ")
                .append(newTargetDisplayNameSuffix);
    }

    public String revertEnrichment(@NonNull Text text) {
        String string = text.getString();
        String[] strings = string.split(" ");

        // if faction information enabled, the last index is faction information
        return configuration.getOptions().nameTag().factionInformation()
                ? strings[strings.length - 2]
                : strings[strings.length - 1];
    }

    public boolean isAfk(String targetName) {
        return networkHandler.getPlayerList().stream()
                .filter(entry -> entry.getProfile().name().equals(targetName))
                .anyMatch(entry -> {
                    Team team = entry.getScoreboardTeam();
                    return team != null && team.getName().endsWith("_afk");
                });
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

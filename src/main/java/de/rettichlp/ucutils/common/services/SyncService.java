package de.rettichlp.ucutils.common.services;

import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.models.FactionEntry;
import lombok.Getter;

import java.util.Map;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static de.rettichlp.ucutils.UCUtils.api;
import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.notificationService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.models.Faction.NULL;
import static de.rettichlp.ucutils.common.services.CommandService.COMMAND_COOLDOWN_MILLIS;
import static java.awt.Color.MAGENTA;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MINUTES;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.of;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;

public class SyncService {

    @Getter
    private boolean gameSyncProcessActive = false;

    public void syncFactionMembers() {
        for (Faction faction : Faction.values()) {
            if (faction == NULL) {
                continue;
            }

            storage.getFactionEntries().clear();

            api.getFactionMembers(faction, factionMembers -> {
                // to faction entry
                FactionEntry factionEntry = new FactionEntry(faction, factionMembers);

                storage.getFactionEntries().add(factionEntry);
                LOGGER.info("Faction members for faction {} synced ({} members)", faction, factionMembers.size());
            });
        }
    }

    public void syncFactionSpecificData() {
        this.gameSyncProcessActive = true;

        // parse from faction-related init commands after all faction members are synced
        utilService.delayedAction(() -> {
            Faction faction = storage.getFaction(requireNonNull(player.getDisplayName()).getString());
            switch (faction) {
                case FBI, POLIZEI -> commandService.sendCommand("wanteds");
                case HITMAN -> commandService.sendCommand("contractlist");
                case RETTUNGSDIENST -> commandService.sendCommand("hausverbot");
                default -> {
                    if (faction.isBadFaction()) {
                        commandService.sendCommand("blacklist");
                    }
                }
            }
        }, COMMAND_COOLDOWN_MILLIS);

        utilService.delayedAction(() -> {
            this.gameSyncProcessActive = false;
            notificationService.sendSuccessNotification("Fraktionsdaten synchronisiert");
        }, COMMAND_COOLDOWN_MILLIS * 2);
    }

    public void checkForUpdates() {
        api.getModrinthVersions(maps -> {
            if (maps.isEmpty()) {
                return;
            }

            Map<String, Object> latestRelease = maps.getFirst();
            String latestVersion = (String) latestRelease.get("version_number");

            String currentVersion = utilService.getVersion();
            if (nonNull(latestVersion) && !currentVersion.equals(latestVersion)) {
                notificationService.sendNotification(() -> empty()
                        .append(of("Neue UCUtils Version verfügbar").copy().formatted(GRAY))
                        .append(of(":").copy().formatted(DARK_GRAY)).append(" ")
                        .append(of(currentVersion).copy().formatted(RED)).append(" ")
                        .append(of("→").copy().formatted(GRAY)).append(" ")
                        .append(of(latestVersion).copy().formatted(GREEN)), MAGENTA, MINUTES.toMillis(5));
            }
        });
    }
}

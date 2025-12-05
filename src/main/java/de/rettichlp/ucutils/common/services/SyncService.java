package de.rettichlp.ucutils.common.services;

import de.rettichlp.ucutils.common.models.Faction;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static de.rettichlp.ucutils.UCUtils.api;
import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.notificationService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.models.Faction.NULL;
import static de.rettichlp.ucutils.common.services.CommandService.COMMAND_COOLDOWN_MILLIS;
import static java.awt.Color.MAGENTA;
import static java.time.LocalDateTime.MIN;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.stream;
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
    private LocalDateTime lastSyncTimestamp = MIN;
    @Getter
    private boolean gameSyncProcessActive = false;

    public void syncFactionMembersWithCommand(Runnable runAfter) {
        List<String> factionMemberInfoCommands = stream(Faction.values())
                .filter(faction -> faction != NULL)
                .map(faction -> "memberinfoall " + faction.getDisplayName())
                .toList();

        commandService.sendCommands(factionMemberInfoCommands, 1000);

        utilService.delayedAction(runAfter, Faction.values().length * 1000L + 1000);
    }

    public void syncFactionSpecificData() {
        this.gameSyncProcessActive = true;
        this.lastSyncTimestamp = now();

        // parse from faction-related init commands after all faction members are synced
        utilService.delayedAction(() -> {
            Faction faction = storage.getFaction(requireNonNull(player.getDisplayName()).getString());
            switch (faction) {
                case FBI, POLIZEI -> commandService.sendCommand("wanteds");
                case HITMAN -> commandService.sendCommand("contractlist");
                case RETTUNGSDIENST -> commandService.sendCommand("hausverbot list");
                default -> {
                    if (faction.isBadFaction()) {
                        commandService.sendCommand("blacklist");
                    }
                }
            }
        }, COMMAND_COOLDOWN_MILLIS);

        utilService.delayedAction(() -> this.gameSyncProcessActive = false, COMMAND_COOLDOWN_MILLIS * 2);
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

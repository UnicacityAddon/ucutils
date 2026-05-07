package de.rettichlp.ucutils.common.services;

import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.models.FactionEntry;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

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
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;

public class SyncService {

    public ScheduledFuture<?> startRepeatingSync() {
        return newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (!storage.isUnicaCity()) {
                return;
            }

            // show health for hydration bar sync
            utilService.delayedAction(() -> commandService.sendCommandWithHiddenOutput("health"), COMMAND_COOLDOWN_MILLIS);
        }, 20, 180, SECONDS);
    }

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
        // parse from faction-related init commands after all faction members are synced
        utilService.delayedAction(() -> {
            Faction faction = storage.getFaction(player.getStringifiedName());
            switch (faction) {
                case FBI, POLIZEI -> commandService.sendCommandWithHiddenOutput("wanteds");
                case MERCENARY -> commandService.sendCommandWithHiddenOutput("contractlist");
                case RETTUNGSDIENST -> commandService.sendCommandWithHiddenOutput("hausverbot");
                default -> {
                    if (faction.isBadFaction()) {
                        commandService.sendCommandWithHiddenOutput("blacklist");
                    }
                }
            }
        }, COMMAND_COOLDOWN_MILLIS);
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
                notificationService.sendNotification(empty()
                        .append(translatable("ucutils.notification.info.new_version").copy().formatted(GRAY))
                        .append(literal(":").copy().formatted(DARK_GRAY)).append(" ")
                        .append(literal(currentVersion).copy().formatted(RED)).append(" ")
                        .append(literal("→").copy().formatted(GRAY)).append(" ")
                        .append(literal(latestVersion).copy().formatted(GREEN)), MAGENTA, MINUTES.toMillis(5));
            }
        });
    }
}

package de.rettichlp.ucutils.common.services;

import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.models.FactionEntry;

import java.util.Map;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static de.rettichlp.ucutils.UCUtils.api;
import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.notificationService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.models.Faction.NULL;
import static java.awt.Color.MAGENTA;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MINUTES;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

public class SyncService {

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

    public void syncTeamMembers() {
        api.getTeamMembers(teamResponse -> {
            storage.setTeam(teamResponse);
            LOGGER.info("Team members synced ({})", teamResponse.ucTeam().size());
        });
    }

    public void syncFactionSpecificData() {
        // parse from faction-related init commands after all faction members are synced
        Faction faction = storage.getFaction(player.getPlainTextName());
        switch (faction) {
            case FBI, POLIZEI -> commandService.sendCommandWithHiddenOutput("wanteds");
            default -> {
            }
        }
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
                        .append(translatable("ucutils.notification.info.new_version").withStyle(GRAY))
                        .append(literal(":").withStyle(DARK_GRAY)).append(" ")
                        .append(literal(currentVersion).withStyle(RED)).append(" ")
                        .append(literal("→").withStyle(GRAY)).append(" ")
                        .append(literal(latestVersion).withStyle(GREEN)), MAGENTA, MINUTES.toMillis(5));
            }
        });
    }
}

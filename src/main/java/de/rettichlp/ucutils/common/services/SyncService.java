package de.rettichlp.ucutils.common.services;

import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.models.FactionEntry;
import lombok.Getter;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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
import static java.util.concurrent.TimeUnit.MINUTES;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;

public class SyncService {

    @Getter
    private boolean gameSyncProcessActive = false;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;

    private boolean executedByTask = false;
    private int healthMessageLineCount = 0;

    private static final Pattern HEALTH_STATUS_PATTERN = Pattern.compile(
            "=== Zustand von .+ ===|Gesundheit|Blut|Hunger|Durst|Fett|Muskeln"
    );

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
            Faction faction = storage.getFaction(player.getStringifiedName());
            switch (faction) {
                case FBI, POLIZEI -> commandService.sendCommand("wanteds");
                case MERCENARY -> commandService.sendCommand("contractlist");
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
            notificationService.sendSuccessNotification(translatable("ucutils.notification.info.faction_data_synchronized"));
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
                notificationService.sendNotification(empty()
                        .append(translatable("ucutils.notification.info.new_version").copy().formatted(GRAY))
                        .append(literal(":").copy().formatted(DARK_GRAY)).append(" ")
                        .append(literal(currentVersion).copy().formatted(RED)).append(" ")
                        .append(literal("→").copy().formatted(GRAY)).append(" ")
                        .append(literal(latestVersion).copy().formatted(GREEN)), MAGENTA, MINUTES.toMillis(5));
            }
        });
    }

    public void checkThirst() {

        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {

            String plainText = message.getString();

            if (this.executedByTask && HEALTH_STATUS_PATTERN.matcher(plainText).find()) {
                this.healthMessageLineCount++;
                if (this.healthMessageLineCount >= 7) {
                    this.executedByTask = false;
                    this.healthMessageLineCount = 0;
                }
            } else {
                return true;
            }

            if (plainText.contains("Durst")) {
                for (Text child : message.getSiblings()) {
                    HoverEvent hoverEvent = child.getStyle().getHoverEvent();
                    if (hoverEvent != null && hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT) {
                        if (hoverEvent instanceof HoverEvent.ShowText(Text value)) {
                            String hoverText = value.getString();
                            double thirst = this.parseFirstValue(hoverText);
                            storage.setThirst(thirst);
                            LOGGER.debug("Set Thirst: {}", thirst);
                        }
                    }
                }
            }

            return false;
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!storage.isUnicaCity()) {
                return;
            }

            LOGGER.info("Starting thirst monitoring scheduler");

            this.scheduler = Executors.newSingleThreadScheduledExecutor();
            this.scheduledTask = this.scheduler.scheduleAtFixedRate(() -> {
                MinecraftClient instance = MinecraftClient.getInstance();
                ClientPlayNetworkHandler networkHandler = instance.getNetworkHandler();
                if (networkHandler == null) {
                    return;
                }
                instance.execute(() -> {
                    this.executedByTask = true;
                    this.healthMessageLineCount = 0;
                    networkHandler.sendChatCommand("health");
                });
            }, 0, 10, TimeUnit.SECONDS);

            LOGGER.debug("Thirst monitoring scheduler started");
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {

            storage.setThirst(-1.0);

            this.executedByTask = false;
            this.healthMessageLineCount = 0;
            if (this.scheduledTask != null && !this.scheduledTask.isCancelled()) {
                this.scheduledTask.cancel(false);
            }
            if (this.scheduler != null && !this.scheduler.isShutdown()) {
                this.scheduler.shutdown();
            }
        });
    }

    private double parseFirstValue(String text) {
        String cleaned = text.replaceAll("§.", "");
        String[] parts = cleaned.split("/");
        if (parts.length > 0) {
            return Double.parseDouble(parts[0].trim());
        }
        return 0.0;
    }
}

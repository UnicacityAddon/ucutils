package de.rettichlp.ucutils.common.services;

import de.rettichlp.ucutils.common.models.CommandResponseRetriever;
import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.models.FactionEntry;
import de.rettichlp.ucutils.common.models.FactionMember;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import static java.lang.Integer.parseInt;
import static java.time.LocalDateTime.MIN;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.regex.Pattern.compile;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.of;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;

public class SyncService {

    private static final Pattern FACTION_MEMBER_ALL_ENTRY = compile("^\\s*-\\s*(?<rank>\\d)\\s*\\|\\s*(?<playerNames>.+)$");

    @Getter
    private LocalDateTime lastSyncTimestamp = MIN;
    @Getter
    private boolean gameSyncProcessActive = false;

    public void syncFactionMembersWithCommandResponse(Runnable runAfter) {
        List<CommandResponseRetriever> commandResponseRetrievers = stream(Faction.values())
                .filter(faction -> faction != NULL)
                .map(this::syncFactionMembersWithCommandResponse)
                .toList();

        for (int i = 0; i < commandResponseRetrievers.size(); i++) {
            CommandResponseRetriever commandResponseRetriever = commandResponseRetrievers.get(i);
            utilService.delayedAction(commandResponseRetriever::execute, i * 100L);
        }

        utilService.delayedAction(() -> {
            storage.getPlayerFactionCache().clear();
            runAfter.run();
        }, commandResponseRetrievers.size() * 100L + 100);
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

    @Contract("_ -> new")
    private @NotNull CommandResponseRetriever syncFactionMembersWithCommandResponse(@NotNull Faction faction) {
        String commandToExecute = "memberinfoall " + faction.getDisplayName();
        return new CommandResponseRetriever(commandToExecute, FACTION_MEMBER_ALL_ENTRY, matchers -> {
            Set<FactionMember> factionMembers = new HashSet<>();

            matchers.forEach(matcher -> {
                int rank = parseInt(matcher.group("rank"));
                String[] playerNames = matcher.group("playerNames").split(", ");

                for (String playerName : playerNames) {
                    FactionMember factionMember = new FactionMember(playerName, rank);
                    factionMembers.add(factionMember);
                }
            });

            FactionEntry factionEntry = new FactionEntry(faction, factionMembers);
            storage.getFactionEntries().removeIf(fe -> fe.faction() == faction);
            storage.getFactionEntries().add(factionEntry);
            LOGGER.info("Retrieved {} members for faction {} from command", factionMembers.size(), faction.name());
        }, true);
    }
}

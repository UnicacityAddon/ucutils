package de.rettichlp.ucutils.common.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static de.rettichlp.ucutils.UCUtils.messageService;
import static de.rettichlp.ucutils.UCUtils.networkHandler;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static java.lang.Boolean.getBoolean;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

public class CommandService {

    public static long COMMAND_COOLDOWN_MILLIS = 500;

    private static final String UUID_RETTICHLP = "25855f4d-3874-4a7f-a6ad-e9e4f3042e19";

    public void sendCommand(String command) {
        LOGGER.info("UCUtils executing command: {}", command);
        networkHandler.sendChatCommand(command);
    }

    public boolean sendCommandWithAfkCheck(String command) {
        boolean isAfk = storage.isAfk();
        LOGGER.info("UCUtils executing command with AFK check (is AFK: {}): {}", isAfk, command);

        if (!isAfk) {
            networkHandler.sendChatCommand(command);
        }

        return !isAfk;
    }

    public void sendCommands(List<String> commandStrings) {
        // to modifiable list
        List<String> commands = new ArrayList<>(commandStrings);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (commands.isEmpty()) {
                    this.cancel();
                    return;
                }

                sendCommand(commands.removeFirst());
            }
        }, 0, COMMAND_COOLDOWN_MILLIS);
    }

    public boolean isSuperUser() {
        return nonNull(player) && (UUID_RETTICHLP.equals(player.getUuidAsString()) || getBoolean("fabric.development"));
    }

    public void retrieveNumberAndRun(String playerName, Consumer<Integer> runWithNumber) {
        sendCommand("nummer " + playerName);

        utilService.delayedAction(() -> ofNullable(storage.getRetrievedNumbers().get(playerName))
                .ifPresentOrElse(runWithNumber, () -> messageService.sendModMessage("Die Nummer von " + playerName + " konnte nicht abgerufen werden.", false)), COMMAND_COOLDOWN_MILLIS);
    }
}

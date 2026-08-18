package de.rettichlp.ucutils.common.services;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static de.rettichlp.ucutils.UCUtils.nameTagService;
import static de.rettichlp.ucutils.UCUtils.networkHandler;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.lang.Boolean.getBoolean;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class CommandService {

    public static final long COMMAND_COOLDOWN_MILLIS = 500;

    private static final String UUID_RETTICHLP = "25855f4d-3874-4a7f-a6ad-e9e4f3042e19";

    @Getter
    private final Map<String, Long> hideCommandOutputCommands = new HashMap<>();

    @Getter
    private final List<String> requestCommands = new ArrayList<>();

    private final ScheduledExecutorService commandScheduler = newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "ucutils-command-scheduler");
        thread.setDaemon(true);
        return thread;
    });

    public void sendCommand(String command) {
        LOGGER.info("UCUtils executing command: {}", command);
        networkHandler.sendCommand(command);
    }

    public void sendCommandWithAfkCheck(String command) {
        boolean isAfk = nameTagService.isAfk(player.getPlainTextName());
        boolean isDead = storage.isDead();
        LOGGER.info("UCUtils executing command with AFK check (is AFK: {}, is dead: {}): {}", isAfk, isDead, command);

        if (!isAfk && !isDead) {
            networkHandler.sendCommand(command);
        }
    }

    public void sendCommands(List<String> commandStrings) {
        sendCommands(commandStrings, COMMAND_COOLDOWN_MILLIS);
    }

    public void sendCommands(List<String> commandStrings, long cooldownMillis) {
        // to modifiable list
        List<String> commands = new ArrayList<>(commandStrings);
        ScheduledFuture<?>[] taskHolder = new ScheduledFuture<?>[1];

        taskHolder[0] = this.commandScheduler.scheduleAtFixedRate(() -> {
            if (commands.isEmpty()) {
                taskHolder[0].cancel(false);
                return;
            }

            sendCommand(commands.removeFirst());
        }, 0, cooldownMillis, MILLISECONDS);
    }

    public void sendCommandsWithAwaitingResponse(@NotNull List<String> commands) {
        String firstCommand = commands.removeFirst();
        sendCommand(firstCommand);
        this.requestCommands.addAll(commands);
    }

    public void sendCommandWithHiddenOutput(String command) {
        this.hideCommandOutputCommands.put(command, currentTimeMillis());
        sendCommandWithAfkCheck(command);
    }

    public boolean showCommandOutputMessage(String command) {
        long executionTime = this.hideCommandOutputCommands.getOrDefault(command, 0L);
        return currentTimeMillis() - executionTime > 1000;
    }

    public boolean isSuperUser() {
        return nonNull(player) && (UUID_RETTICHLP.equals(player.getStringUUID()) || getBoolean("fabric.development"));
    }
}

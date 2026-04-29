package de.rettichlp.ucutils.listener.impl;

import de.rettichlp.ucutils.common.models.Countdown;
import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IAbsorptionGetListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import de.rettichlp.ucutils.listener.ITickListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.nameTagService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.models.ShutdownReason.CEMETERY;
import static de.rettichlp.ucutils.common.models.ShutdownReason.JAIL;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.getProperty;
import static java.time.Duration.ofMinutes;
import static java.util.Objects.nonNull;
import static java.util.regex.Pattern.compile;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.of;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.RED;

@UCUtilsListener
public class PlayerListener implements IAbsorptionGetListener, IMessageReceiveListener, ITickListener {

    private static final String SHUTDOWN_TIMEOUT = "5";
    private static final int PRAY_DELAY_IN_SECONDS = 30;

    // dead
    private static final Pattern DEAD_PATTERN = compile("^Du bist nun für (?<minutes>\\d+) Minuten auf dem Friedhof$");
    private static final Pattern DEAD_DESPAWN_PATTERN = compile("^Verdammt\\.{3} mein Kopf dröhnt so\\.{3}$");
    private static final Pattern DEAD_AREVIVE_PATTERN = compile("^Du wurdest von (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) wiederbelebt\\.$");

    // health
    private static final Pattern HEALTH_HEADER_PATTERN = compile("^=== Zustand von (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) ===$");
    private static final Pattern HEALTH_ENTRY_PATTERN = compile("^§.» (?<type>Gesundheit|Blut §.\\[§..+§.]|Hunger|Durst|Fett|Muskeln)§.: §.((§.)?#)+$");
    private static final Pattern HEALTH_ENTRY_HOVER_PATTERN = compile("^§.(?<value>\\d+\\.\\d+)§./§.20\\.0$");

    // jail
    private static final Pattern JAIL_PATTERN = compile("^\\[Gefängnis] Du bist nun für (?<minutes>\\d+) Minuten im Gefängnis\\.$");
    private static final Pattern JAIL_UNJAIL_PATTERN = compile("^\\[Gefängnis] Du bist nun wieder frei!$");

    // pray
    private static final Pattern PRAY_START_PATTERN = compile("^\\[Kirche] Du fängst an für (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) zu beten\\.$");

    @Override
    public void onAbsorptionGet() {
        storage.getCountdowns().add(new Countdown("Absorption", ofMinutes(3)));
    }

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Matcher deadAReviveMatcher = DEAD_AREVIVE_PATTERN.matcher(message);
        if (deadAReviveMatcher.find()) {
            storage.getActiveShutdowns().removeIf(shutdownReason -> shutdownReason == CEMETERY);
            return true;
        }

        Matcher deadMatcher = DEAD_PATTERN.matcher(message);
        if (deadMatcher.find()) {
            int minutes = parseInt(deadMatcher.group("minutes"));
            storage.getCountdowns().add(new Countdown("Friedhof", ofMinutes(minutes)));
            return true;
        }

        Matcher healthHeaderMatcher = HEALTH_HEADER_PATTERN.matcher(message);
        if (healthHeaderMatcher.find()) {
            return commandService.showCommandOutputMessage("health");
        }

        Matcher healthEntryMatcher = HEALTH_ENTRY_PATTERN.matcher(message);
        if (healthEntryMatcher.find()) {
            if (!healthEntryMatcher.group("type").contains("Durst")) {
                return commandService.showCommandOutputMessage("health");
            }

            text.getSiblings().stream()
                    .map(sibling -> sibling.getStyle().getHoverEvent())
                    .filter(hoverEvent -> hoverEvent instanceof HoverEvent.ShowText)
                    .map(hoverEvent -> ((HoverEvent.ShowText) hoverEvent).value().getString())
                    .findFirst()
                    .ifPresent(hoverString -> {
                        Matcher healthEntryHoverMatcher = HEALTH_ENTRY_HOVER_PATTERN.matcher(hoverString);
                        if (healthEntryHoverMatcher.find()) {
                            storage.setThirst(parseDouble(healthEntryHoverMatcher.group("value")));
                        }
                    });

            return commandService.showCommandOutputMessage("health");
        }

        Matcher jailMatcher = JAIL_PATTERN.matcher(message);
        if (jailMatcher.find()) {
            int minutes = parseInt(jailMatcher.group("minutes"));
            storage.getCountdowns().add(new Countdown("Gefängnis", ofMinutes(minutes)));
            return true;
        }

        Matcher deadDespawnMatcher = DEAD_DESPAWN_PATTERN.matcher(message);
        if (deadDespawnMatcher.find()) {
            boolean shutdown = storage.getActiveShutdowns().removeIf(shutdownReason -> shutdownReason == CEMETERY);

            if (shutdown) {
                shutdownPC();
            }

            return true;
        }

        Matcher jailUnjailMatcher = JAIL_UNJAIL_PATTERN.matcher(message);
        if (jailUnjailMatcher.find()) {
            boolean shutdown = storage.getActiveShutdowns().removeIf(shutdownReason -> shutdownReason == JAIL);

            if (shutdown) {
                shutdownPC();
            }

            return true;
        }

        Matcher prayStartMatcher = PRAY_START_PATTERN.matcher(message);
        if (prayStartMatcher.find()) {
            utilService.delayedAction(() -> commandService.sendCommand("beten"), PRAY_DELAY_IN_SECONDS * 1000L);
            return true;
        }

        return true;
    }

    @Override
    public void onTick() {
        if (player.age % 1200 == 0 && !nameTagService.isAfk(player.getStringifiedName())) {
            configuration.addMinutesSinceLastPayDay(1);
        }
    }

    private void shutdownPC() {
        String os = getProperty("os.name").toLowerCase();
        String[] command = new String[0];

        if (os.contains("windows")) {
            command = new String[]{ "shutdown", "/s", "/t", SHUTDOWN_TIMEOUT };
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            command = new String[]{ "shutdown", "-h", "+" + SHUTDOWN_TIMEOUT }; // will fail potentially without sudo
        }

        ClientConnection connection = player.networkHandler.getConnection();
        if (nonNull(connection)) {
            connection.disconnect(empty()
                    .append(of("Der PC wird in").copy().formatted(GRAY)).append(" ")
                    .append(of(SHUTDOWN_TIMEOUT + " Sekunden").copy().formatted(RED)).append(" ")
                    .append(of("durch UCUtils heruntergefahren...").copy().formatted(GRAY)));
        }

        if (command.length == 0) {
            LOGGER.warn("Unknown operating system {} - shutdown aborted", os);
            return;
        }

        try {
            getRuntime().exec(command);
        } catch (IOException e) {
            LOGGER.error("Error while executing shutdown command: {}", command, e);
        }
    }
}

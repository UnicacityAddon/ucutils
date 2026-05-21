package de.rettichlp.ucutils.listener.impl.faction;

import de.rettichlp.ucutils.common.models.Countdown;
import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.text.Text;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.services.CommandService.COMMAND_COOLDOWN_MILLIS;
import static java.time.Duration.ofMinutes;
import static java.time.LocalDateTime.now;
import static java.util.regex.Pattern.compile;

@UCUtilsListener
public class MedicListener implements IMessageReceiveListener {

    public static final Duration MEDIC_BANDAGE_DURATION = ofMinutes(4);
    public static final Duration MEDIC_PILL_DURATION = ofMinutes(4);

    private static final Pattern MEDIC_BANDAGE_PATTERN = compile("^(?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat dich bandagiert\\.$");
    private static final Pattern MEDIC_BANDAGE_GIVE_PATTERN = compile("^Du hast (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) bandagiert\\.$");
    private static final Pattern MEDIC_PILL_PATTERN = compile("^\\[Medic] Doktor (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat dir Schmerzpillen verabreicht\\.$");
    private static final Pattern MEDIC_PILL_GIVE_PATTERN = compile("^\\[Medic] Du hast (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) Schmerzpillen verabreicht\\.$");
    private static final Pattern MEDIC_REVIVE_START_PATTERN = compile("^Du beginnst mit der Wiederbelebung von (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+)\\.\\.\\.$");
    private static final Pattern LABOR_TRANSPORT_STARTED_PATTERN = compile("^\\[ʟᴀʙᴏʀ] Transport gestartet: (?<chestAmount>\\d+) ᴋɪsᴛᴇɴ mit (?<ingredientAmount>\\d+) (?<ingredient>.+)$");

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Matcher medicBandageMatcher = MEDIC_BANDAGE_PATTERN.matcher(message);
        if (medicBandageMatcher.find()) {
            storage.getCountdowns().add(new Countdown("Bandage", MEDIC_BANDAGE_DURATION));
            return true;
        }

        Matcher medicBandageGiveMatcher = MEDIC_BANDAGE_GIVE_PATTERN.matcher(message);
        if (medicBandageGiveMatcher.find()) {
            String playerName = medicBandageGiveMatcher.group("playerName");
            storage.getMedicBandageCooldowns().put(playerName, now().plus(MEDIC_BANDAGE_DURATION));
            return true;
        }

        Matcher medicPillMatcher = MEDIC_PILL_PATTERN.matcher(message);
        if (medicPillMatcher.find()) {
            storage.getCountdowns().add(new Countdown("Schmerzpille", MEDIC_PILL_DURATION));
            return true;
        }

        Matcher medicPillGiveMatcher = MEDIC_PILL_GIVE_PATTERN.matcher(message);
        if (medicPillGiveMatcher.find()) {
            String playerName = medicPillGiveMatcher.group("playerName");
            storage.getMedicPillCooldowns().put(playerName, now().plus(MEDIC_PILL_DURATION));
            return true;
        }

        Matcher medicReviveStartMatcher = MEDIC_REVIVE_START_PATTERN.matcher(message);
        if (medicReviveStartMatcher.find()) {
            utilService.delayedAction(() -> commandService.sendCommand("dinfo"), COMMAND_COOLDOWN_MILLIS);
            return true;
        }

        Matcher laborTransportStartedMatcher = LABOR_TRANSPORT_STARTED_PATTERN.matcher(message);
        if (laborTransportStartedMatcher.find()) {
            Duration duration = ofMinutes(5).plusSeconds(56); // please don't ask why it is like this
            storage.getCountdowns().add(new Countdown("Labor Transport", duration, () -> {}));
            return true;
        }

        return true;
    }
}

package de.rettichlp.ucutils.listener.impl.faction;

import de.rettichlp.ucutils.common.models.Countdown;
import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.models.HousebanEntry;
import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.messageService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.models.Sound.FIRE;
import static de.rettichlp.ucutils.common.services.CommandService.COMMAND_COOLDOWN_MILLIS;
import static java.lang.Integer.parseInt;
import static java.time.Duration.ofMinutes;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;
import static net.minecraft.text.Text.of;
import static net.minecraft.util.Formatting.AQUA;

@UCUtilsListener
public class MedicListener implements IMessageReceiveListener {

    public static final Duration MEDIC_BANDAGE_DURATION = ofMinutes(4);
    public static final Duration MEDIC_PILL_DURATION = ofMinutes(4);

    private static final Pattern MEDIC_BANDAGE_PATTERN = compile("^(?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat dich bandagiert\\.$");
    private static final Pattern MEDIC_BANDAGE_GIVE_PATTERN = compile("^Du hast (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) bandagiert\\.$");
    private static final Pattern MEDIC_PILL_PATTERN = compile("^\\[Medic] Doktor (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat dir Schmerzpillen verabreicht\\.$");
    private static final Pattern MEDIC_PILL_GIVE_PATTERN = compile("^\\[Medic] Du hast (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) Schmerzpillen verabreicht\\.$");
    private static final Pattern MEDIC_REVIVE_START_PATTERN = compile("^Du beginnst mit der Wiederbelebung von (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+)\\.\\.\\.$");
    private static final Pattern HOUSEBAN_HEADER_PATTERN = compile("^=== Hausverbote \\(\\d+\\) ===$");
    private static final Pattern HOUSEBAN_ENTRY_PATTERN = compile("^» (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) ➲ (?<reasons>.+) ➲ \\d+d \\((?<expireDateDay>\\d+)\\.(?<expireDateMonth>\\d+)\\.(?<expireDateYear>\\d+) (?<expireTimeHour>\\d+):(?<expireTimeMinute>\\d+)\\)$");
    private static final Pattern HOUSEBAN_ADD_PATTERN = compile("^\\[HV] » (?:\\[UC])?(?<issuerPlayerName>[a-zA-Z0-9_]+) hat (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+)s Hausverbot gegeben\\. » (?<reason>.+) » \\d+d \\((?<expireDateDay>\\d+)\\.(?<expireDateMonth>\\d+)\\.(?<expireDateYear>\\d+) (?<expireTimeHour>\\d+):(?<expireTimeMinute>\\d+)\\)$");
    private static final Pattern HOUSEBAN_REMOVE_PATTERN = compile("^\\[HV] » (?:\\[UC])?(?<issuerPlayerName>[a-zA-Z0-9_]+) hat (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+)s Hausverbot aufgehoben\\.$");
    private static final Pattern FIRST_AID_PATTERN = compile("^\\[Erste-Hilfe] (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat dir ein Erste-Hilfe-Schein für 14 Tage ausgestellt\\.$");
    private static final Pattern FIRST_AID_LICENCES_PATTERN = compile("^- Erste-Hilfe-Schein: Vorhanden$");
    private static final Pattern LABOR_TRANSPORT_STARTED_PATTERN = compile("^\\[ʟᴀʙᴏʀ] Transport gestartet: (?<chestAmount>\\d+) ᴋɪsᴛᴇɴ mit (?<ingredientAmount>\\d+) (?<ingredient>.+)$");
    private static final Pattern FIRE_START_PATTERN = compile("^News: Es wurde ein Feuer bei .+ gemeldet!$");

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

        Matcher housebanHeaderMatcher = HOUSEBAN_HEADER_PATTERN.matcher(message);
        if (housebanHeaderMatcher.find()) {
            storage.getHousebanEntries().clear();
            return commandService.showCommandOutputMessage("hausverbot");
        }

        Matcher housebanEntryMatcher = HOUSEBAN_ENTRY_PATTERN.matcher(message);
        if (housebanEntryMatcher.find()) {
            HousebanEntry housebanEntry = getHousebanEntry(housebanEntryMatcher);
            storage.getHousebanEntries().add(housebanEntry);
            return commandService.showCommandOutputMessage("hausverbot");
        }

        Matcher housebanAddMatcher = HOUSEBAN_ADD_PATTERN.matcher(message);
        if (housebanAddMatcher.find()) {
            String playerName = housebanAddMatcher.group("playerName");
            String reason = housebanAddMatcher.group("reason");
            int expireDateDay = parseInt(housebanAddMatcher.group("expireDateDay"));
            int expireDateMonth = parseInt(housebanAddMatcher.group("expireDateMonth"));
            int expireDateYear = parseInt(housebanAddMatcher.group("expireDateYear"));
            int expireTimeHour = parseInt(housebanAddMatcher.group("expireTimeHour"));
            int expireTimeMinute = parseInt(housebanAddMatcher.group("expireTimeMinute"));

            storage.getHousebanEntries().removeIf(housebanEntry -> housebanEntry.getPlayerName().equals(playerName));
            LocalDateTime unbanDateTime = LocalDateTime.of(expireDateYear, expireDateMonth, expireDateDay, expireTimeHour, expireTimeMinute);
            HousebanEntry housebanEntry = new HousebanEntry(playerName, singletonList(reason), unbanDateTime);
            storage.getHousebanEntries().add(housebanEntry);
            return true;
        }

        Matcher housebanRemoveMatcher = HOUSEBAN_REMOVE_PATTERN.matcher(message);
        if (housebanRemoveMatcher.find()) {
            String playerName = housebanRemoveMatcher.group("playerName");
            storage.getHousebanEntries().removeIf(housebanEntry -> housebanEntry.getPlayerName().equals(playerName));
            return true;
        }

        Matcher firstAidMatcher = FIRST_AID_PATTERN.matcher(message);
        if (firstAidMatcher.find()) {
            configuration.setFirstAidLicenseExpireDateTime(now().plusDays(14));
            return true;
        }

        Matcher firstAidLicencesMatcher = FIRST_AID_LICENCES_PATTERN.matcher(message);
        if (firstAidLicencesMatcher.find()) {
            MutableText overwriteText = text.copy().append(" ")
                    .append(of("bis " + ofNullable(configuration.getFirstAidLicenseExpireDateTime())
                            .map(messageService::dateTimeToFriendlyString)
                            .orElse("Unbekannt")).copy().formatted(AQUA));

            player.sendMessage(overwriteText, false);
            return false; // hide message
        }

        Matcher laborTransportStartedMatcher = LABOR_TRANSPORT_STARTED_PATTERN.matcher(message);
        if (laborTransportStartedMatcher.find()) {
            Duration duration = ofMinutes(5).plusSeconds(56); // please don't ask why it is like this
            storage.getCountdowns().add(new Countdown("Labor Transport", duration, () -> {}));
            return true;
        }

        Faction playerFaction = storage.getFaction(player.getGameProfile().name());
        Matcher fireStartMatcher = FIRE_START_PATTERN.matcher(message);
        if (fireStartMatcher.find() && configuration.getOptions().sound().fire().verify(playerFaction)) {
            FIRE.play();
            return true;
        }

        return true;
    }

    private @NotNull HousebanEntry getHousebanEntry(@NotNull MatchResult housebanEntryMatcher) {
        String playerName = housebanEntryMatcher.group("playerName");
        String reasonsRaw = housebanEntryMatcher.group("reasons");
        int expireDateDay = parseInt(housebanEntryMatcher.group("expireDateDay"));
        int expireDateMonth = parseInt(housebanEntryMatcher.group("expireDateMonth"));
        int expireDateYear = parseInt(housebanEntryMatcher.group("expireDateYear"));
        int expireTimeHour = parseInt(housebanEntryMatcher.group("expireTimeHour"));
        int expireTimeMinute = parseInt(housebanEntryMatcher.group("expireTimeMinute"));

        String[] reasons = reasonsRaw.split(" \\+ ");

        LocalDateTime unbanDateTime = LocalDateTime.of(expireDateYear, expireDateMonth, expireDateDay, expireTimeHour, expireTimeMinute);
        return new HousebanEntry(playerName, asList(reasons), unbanDateTime);
    }
}

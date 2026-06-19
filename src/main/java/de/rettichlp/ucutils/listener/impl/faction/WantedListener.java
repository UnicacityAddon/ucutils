package de.rettichlp.ucutils.listener.impl.faction;

import de.rettichlp.ucutils.common.models.WantedEntry;
import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.nameTagService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.services.CommandService.COMMAND_COOLDOWN_MILLIS;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.util.regex.Pattern.compile;
import static net.minecraft.ChatFormatting.BLUE;
import static net.minecraft.ChatFormatting.DARK_AQUA;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

@UCUtilsListener
public class WantedListener implements IMessageReceiveListener {

    private static final Pattern WANTED_GIVEN_POINTS_PATTERN = compile("^HQ: (?:\\[UC])?([a-zA-Z0-9_]+)'s momentanes WantedLevel: (\\d+)$");
    private static final Pattern WANTED_GIVEN_REASON_PATTERN = compile("^HQ: Gesuchter: (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+)\\. Grund: (?<reason>.+)$");
    private static final Pattern WANTED_REASON_PATTERN = compile("^HQ: Fahndungsgrund: (?<reason>.+) \\| Fahndungszeit: (?<time>.+)$");
    private static final Pattern WANTED_DELETE_PATTERN = compile("^HQ: (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+)(?:'s)* Akten gelöscht, over\\.$");
    private static final Pattern WANTED_KILL_PATTERN = compile("^HQ: (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+) wurde von (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) getötet\\.$");
    private static final Pattern WANTED_ARREST_PATTERN = compile("^HQ: (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+) wurde von (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) eingesperrt\\.$");
    private static final Pattern WANTED_UNARREST_PATTERN = compile("^HQ: (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+) aus dem Gefängnis entlassen\\.$");
    private static final Pattern WANTED_LIST_HEADER_PATTERN = compile("Online Spieler mit WantedPunkten:");
    private static final Pattern WANTED_LIST_ENTRY_PATTERN = compile("- (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) \\| (?<wantedPointAmount>\\d+) WPS \\((?<reason>.+)\\)(?<afk> \\| AFK|)");
    private static final Pattern LICENSE_DRIVING_GIVE_PATTERN = compile("^(Agent|Agentin|Beamter|Beamtin) (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+)(?:'s)* Führerschein zurückgegeben\\.$");
    private static final Pattern LICENSE_DRIVING_TAKE_PATTERN = compile("^(Agent|Agentin|Beamter|Beamtin) (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+)(?:'s)* Führerschein abgenommen\\.$");
    private static final Pattern LICENSE_GUN_GIVE_PATTERN = compile("^(Agent|Agentin|Beamter|Beamtin) (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+)(?:'s)* Waffenschein zurückgegeben\\.$");
    private static final Pattern LICENSE_GUN_TAKE_PATTERN = compile("^(Agent|Agentin|Beamter|Beamtin) (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+)(?:'s)* Waffenschein abgenommen\\.$");
    private static final Pattern TAKE_GUNS_PATTERN = compile("^(Beamter|Beamtin) (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+) (seine|ihre) Waffen abgenommen\\.$");
    private static final Pattern TAKE_DRUGS_PATTERN = compile("^(Beamter|Beamtin) (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+) (seine|ihre) Drogen abgenommen.$");
    private static final Pattern CAR_CHECK_PATTERN = compile("^Das Fahrzeug mit dem Kennzeichen [A-Z0-9-]+ gehört (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+)\\.$");
    private static final Pattern CAR_PARKTICKET_PATTERN = compile("^HQ: (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat ein Strafzettel an das Fahrzeug \\[(?<plate>[A-Z0-9-]+)] vergeben\\.$");
    private static final Pattern CAR_PARKTICKET_REMOVE_PATTERN = compile("^HQ: (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat ein Strafzettel von dem Fahrzeug \\[(?<plate>[A-Z0-9-]+)] entfernt\\.$");
    private static final Pattern SEARCH_TRUNK_PATTERN = compile("^HQ: (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat den Kofferraum vom Fahrzeug (?<plate>.+) durchsucht, over\\.$");
    private static final Pattern TRACKER_AGENT_PATTERN = compile("^HQ: (Agent|Agentin) (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat ein Peilsender an (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+) befestigt, over\\.$");

    @Override
    public boolean onMessageReceive(Component text, String message) {
        Matcher wantedGivenPointsMatcher = WANTED_GIVEN_POINTS_PATTERN.matcher(message);
        if (wantedGivenPointsMatcher.find()) {
            String playerName = wantedGivenPointsMatcher.group(1);
            int wantedPoints = parseInt(wantedGivenPointsMatcher.group(2));

            storage.getWantedEntries().stream()
                    .filter(wantedEntry -> wantedEntry.getPlayerName().equals(playerName))
                    .findFirst()
                    .ifPresentOrElse(wantedEntry -> wantedEntry.setWantedPointAmount(wantedPoints), () -> {
                        WantedEntry wantedEntry = new WantedEntry(playerName, wantedPoints, "");
                        storage.getWantedEntries().add(wantedEntry);
                    });

            Component modifiedMessage = empty()
                    .append(literal("➥").withStyle(GRAY)).append(" ")
                    .append(literal(wantedGivenPointsMatcher.group(2)).withStyle(BLUE)).append(" ")
                    .append(literal("Wanteds").withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);

            return false;
        }

        Matcher wantedGivenReasonMatcher = WANTED_GIVEN_REASON_PATTERN.matcher(message);
        if (wantedGivenReasonMatcher.find()) {
            String playerName = wantedGivenReasonMatcher.group("playerName");
            String reason = wantedGivenReasonMatcher.group("reason");

            storage.getWantedEntries().stream()
                    .filter(wantedEntry -> wantedEntry.getPlayerName().equals(playerName))
                    .findFirst()
                    .ifPresent(wantedEntry -> wantedEntry.setReason(reason));

            Component modifiedMessage = empty()
                    .append(literal("Gesucht").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(wantedGivenReasonMatcher.group(1)).withStyle(BLUE)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(wantedGivenReasonMatcher.group(2)).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);

            return false;
        }

        Matcher wantedReasonMatcher = WANTED_REASON_PATTERN.matcher(message);
        if (wantedReasonMatcher.find()) {
            String reason = wantedReasonMatcher.group("reason");
            String time = wantedReasonMatcher.group("time");

            Component modifiedMessage = empty()
                    .append(literal("➥").withStyle(GRAY)).append(" ")
                    .append(literal(reason).withStyle(BLUE)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(time).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);

            return false;
        }

        Matcher wantedDeleteMatcher = WANTED_DELETE_PATTERN.matcher(message);
        if (wantedDeleteMatcher.find()) {
            String playerName = wantedDeleteMatcher.group("playerName");
            String targetName = wantedDeleteMatcher.group("targetName");

            int wpAmount = getWpAmountAndDelete(targetName);

            Component modifiedMessage = empty()
                    .append(literal("Gelöscht").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(targetName).withStyle(BLUE)).append(" ")
                    .append(literal("(").withStyle(GRAY))
                    .append(literal(valueOf(wpAmount)).withStyle(RED))
                    .append(literal(")").withStyle(GRAY)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(playerName).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);

            return false;
        }

        Matcher wantedKillMatcher = WANTED_KILL_PATTERN.matcher(message);
        if (wantedKillMatcher.find()) {
            String targetName = wantedKillMatcher.group("targetName");
            String playerName = wantedKillMatcher.group("playerName");
            int wpAmount = getWpAmountAndDelete(targetName);

            Component modifiedMessage = empty()
                    .append(literal("Getötet").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(targetName).withStyle(BLUE)).append(" ")
                    .append(literal("(").withStyle(GRAY))
                    .append(literal(valueOf(wpAmount)).withStyle(RED))
                    .append(literal(")").withStyle(GRAY)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(playerName).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);
            return false;
        }

        Matcher wantedJailMatcher = WANTED_ARREST_PATTERN.matcher(message);
        if (wantedJailMatcher.find()) {
            String targetName = wantedJailMatcher.group("targetName");
            String playerName = wantedJailMatcher.group("playerName");
            int wpAmount = getWpAmountAndDelete(targetName);

            Component modifiedMessage = empty()
                    .append(literal("Eingesperrt").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(targetName).withStyle(BLUE)).append(" ")
                    .append(literal("(").withStyle(GRAY))
                    .append(literal(valueOf(wpAmount)).withStyle(RED))
                    .append(literal(")").withStyle(GRAY)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(playerName).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);
            return false;
        }

        Matcher carCheckMatcher = CAR_CHECK_PATTERN.matcher(message);
        if (carCheckMatcher.find()) {
            String playerName = carCheckMatcher.group("playerName");
            utilService.delayedAction(() -> commandService.sendCommand("memberinfo " + playerName), COMMAND_COOLDOWN_MILLIS);
        }

        Matcher carParkticketMatcher = CAR_PARKTICKET_PATTERN.matcher(message);
        if (carParkticketMatcher.find()) {
            String officerName = carParkticketMatcher.group("playerName");
            String plate = carParkticketMatcher.group("plate");

            Component modifiedMessage = empty()
                    .append(literal("Strafzettel").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(plate).withStyle(BLUE)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(officerName).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);
            return false;
        }

        Matcher carParkticketRemoveMatcher = CAR_PARKTICKET_REMOVE_PATTERN.matcher(message);
        if (carParkticketRemoveMatcher.find()) {
            String officerName = carParkticketRemoveMatcher.group("playerName");
            String plate = carParkticketRemoveMatcher.group("plate");

            Component modifiedMessage = empty()
                    .append(literal("Strafzettel entfernt").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(plate).withStyle(BLUE)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(officerName).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);

            return false;
        }

        Matcher searchTrunkMatcher = SEARCH_TRUNK_PATTERN.matcher(message);
        if (searchTrunkMatcher.find()) {
            String officerName = searchTrunkMatcher.group("playerName");
            String plate = searchTrunkMatcher.group("plate");

            Component modifiedMessage = empty()
                    .append(literal("Fahrzeugkontrolle").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(plate).withStyle(BLUE)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(officerName).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);

            return false;
        }

        Matcher wantedUnarrestMatcher = WANTED_UNARREST_PATTERN.matcher(message);
        if (wantedUnarrestMatcher.find()) {
            String playerName = wantedUnarrestMatcher.group("playerName");
            String targetName = wantedUnarrestMatcher.group("targetName");

            Component modifiedMessage = empty()
                    .append(literal("Entlassen").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(targetName).withStyle(BLUE)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(playerName).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);

            return false;
        }

        Matcher wantedListHeaderMatcher = WANTED_LIST_HEADER_PATTERN.matcher(message);
        if (wantedListHeaderMatcher.find()) {
            storage.getWantedEntries().clear();
            return commandService.showCommandOutputMessage("wanteds");
        }

        Matcher wantedListEntryMatcher = WANTED_LIST_ENTRY_PATTERN.matcher(message);
        if (wantedListEntryMatcher.find()) {
            String playerName = wantedListEntryMatcher.group("playerName");
            int wantedPointAmount = parseInt(wantedListEntryMatcher.group("wantedPointAmount"));
            String reason = wantedListEntryMatcher.group("reason");
            boolean isAfk = wantedListEntryMatcher.group("afk").contains("AFK");

            WantedEntry wantedEntry = new WantedEntry(playerName, wantedPointAmount, reason);
            storage.getWantedEntries().add(wantedEntry);

            ChatFormatting color = nameTagService.getWantedPointColor(wantedPointAmount);

            if (commandService.showCommandOutputMessage("wanteds")) {
                Component modifiedMessage = empty()
                        .append(literal("➥").withStyle(GRAY)).append(" ")
                        .append(literal(playerName).withStyle(color)).append(" ")
                        .append(literal("-").withStyle(GRAY)).append(" ")
                        .append(literal(reason).withStyle(color)).append(" ")
                        .append(literal("(").withStyle(GRAY))
                        .append(literal(valueOf(wantedPointAmount)).withStyle(BLUE))
                        .append(literal(")").withStyle(GRAY)).append(" ")
                        .append(literal(isAfk ? "|" : "").withStyle(DARK_GRAY)).append(" ")
                        .append(literal(isAfk ? "AFK" : "").withStyle(GRAY));

                player.sendSystemMessage(modifiedMessage);
            }

            return false;
        }

        Matcher licenseDrivingGiveMatcher = LICENSE_DRIVING_GIVE_PATTERN.matcher(message);
        if (licenseDrivingGiveMatcher.find()) {
            String playerName = licenseDrivingGiveMatcher.group("playerName");
            String targetName = licenseDrivingGiveMatcher.group("targetName");

            Component modifiedMessage = empty()
                    .append(literal("Führerscheinrückgabe").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(targetName).withStyle(BLUE)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(playerName).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);

            return false;
        }

        Matcher licenseDrivingTakeMatcher = LICENSE_DRIVING_TAKE_PATTERN.matcher(message);
        if (licenseDrivingTakeMatcher.find()) {
            String playerName = licenseDrivingTakeMatcher.group("playerName");
            String targetName = licenseDrivingTakeMatcher.group("targetName");

            Component modifiedMessage = empty()
                    .append(literal("Führerscheinabnahme").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(targetName).withStyle(BLUE)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(playerName).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);

            return false;
        }

        Matcher licenseGunGiveMatcher = LICENSE_GUN_GIVE_PATTERN.matcher(message);
        if (licenseGunGiveMatcher.find()) {
            String playerName = licenseGunGiveMatcher.group("playerName");
            String targetName = licenseGunGiveMatcher.group("targetName");

            Component modifiedMessage = empty()
                    .append(literal("Waffenscheinrückgabe").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(targetName).withStyle(BLUE)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(playerName).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);

            return false;
        }

        Matcher licenseGunTakeMatcher = LICENSE_GUN_TAKE_PATTERN.matcher(message);
        if (licenseGunTakeMatcher.find()) {
            String playerName = licenseGunTakeMatcher.group("playerName");
            String targetName = licenseGunTakeMatcher.group("targetName");

            Component modifiedMessage = empty()
                    .append(literal("Waffenscheinabnahme").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(targetName).withStyle(BLUE)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(playerName).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);

            return false;
        }

        Matcher takeGunsMatcher = TAKE_GUNS_PATTERN.matcher(message);
        if (takeGunsMatcher.find()) {
            String playerName = takeGunsMatcher.group("playerName");
            String targetName = takeGunsMatcher.group("targetName");

            Component modifiedMessage = empty()
                    .append(literal("Waffenabnahme").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(targetName).withStyle(BLUE)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(playerName).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);

            return false;
        }

        Matcher takeDrugsMatcher = TAKE_DRUGS_PATTERN.matcher(message);
        if (takeDrugsMatcher.find()) {
            String playerName = takeDrugsMatcher.group("playerName");
            String targetName = takeDrugsMatcher.group("targetName");

            Component modifiedMessage = empty()
                    .append(literal("Drogenabnahme").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(targetName).withStyle(BLUE)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(playerName).withStyle(BLUE));

            player.sendSystemMessage(modifiedMessage);

            return false;
        }

        Matcher trackerMatcher = TRACKER_AGENT_PATTERN.matcher(message);
        if (trackerMatcher.find()) {
            String playerName = trackerMatcher.group("playerName");
            String targetName = trackerMatcher.group("targetName");

            Component modifiedMessage = empty()
                    .append(literal("Peilsender").withStyle(RED)).append(" ")
                    .append(literal("-").withStyle(GRAY)).append(" ")
                    .append(literal(playerName).withStyle(DARK_AQUA)).append(" ")
                    .append(literal("»").withStyle(GRAY)).append(" ")
                    .append(literal(targetName).withStyle(GOLD));

            player.sendSystemMessage(modifiedMessage);

            return false;
        }

        return true;
    }

    private int getWpAmountAndDelete(String targetName) {
        Predicate<WantedEntry> predicate = wantedEntry -> wantedEntry.getPlayerName().equals(targetName);
        int wantedPointAmount = storage.getWantedEntries().stream()
                .filter(predicate)
                .findAny()
                .map(WantedEntry::getWantedPointAmount)
                .orElse(0);

        storage.getWantedEntries().removeIf(predicate);
        return wantedPointAmount;
    }
}

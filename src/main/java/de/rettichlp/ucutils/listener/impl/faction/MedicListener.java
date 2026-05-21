package de.rettichlp.ucutils.listener.impl.faction;

import de.rettichlp.ucutils.common.models.Countdown;
import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.Color;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.services.CommandService.COMMAND_COOLDOWN_MILLIS;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.time.Duration.ofMinutes;
import static java.time.LocalDateTime.now;
import static java.util.regex.Pattern.compile;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.BOLD;
import static net.minecraft.util.Formatting.DARK_AQUA;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.DARK_GREEN;
import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.YELLOW;

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
    private static final Pattern STORAGE_INGREDIENT_SHARE_PATTERN = compile("^.+ (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+): (?<ingredient1>\\d+)x Wirkstoff \\| (?<ingredient2>\\d+)x Trägerstoff \\| (?<ingredient3>\\d+)x Zusatzstoff$");
    private static final Pattern STORAGE_INGREDIENT_ACCEPT_PATTERN = compile("^.+ (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+): Ich nehme (?<ingredient>Wirkstoff|Trägerstoff|Zusatzstoff)! \\(geschätzt: (?<amountBefore>\\d+) → (?<amountAfter>\\d+)\\)$");

    private static final MutableText STORAGE_TEXT = empty()
            .append(literal("ʟ").withColor(Color.decode("#AA0000").getRGB()))
            .append(literal("ᴀ").withColor(Color.decode("#BF1515").getRGB()))
            .append(literal("ʙ").withColor(Color.decode("#D52B2B").getRGB()))
            .append(literal("ᴏ").withColor(Color.decode("#EA4040").getRGB()))
            .append(literal("ʀ").withColor(Color.decode("#FF5555").getRGB()));

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

        Matcher storageIngredientShareMatcher = STORAGE_INGREDIENT_SHARE_PATTERN.matcher(message);
        if (storageIngredientShareMatcher.find()) {
            String playerName = storageIngredientShareMatcher.group("playerName");
            int ingredient1 = parseInt(storageIngredientShareMatcher.group("ingredient1"));
            int ingredient2 = parseInt(storageIngredientShareMatcher.group("ingredient2"));
            int ingredient3 = parseInt(storageIngredientShareMatcher.group("ingredient3"));

            player.sendMessage(empty(), false);

            MutableText storageText = empty()
                    .append(literal("[").formatted(DARK_GRAY))
                    .append(STORAGE_TEXT)
                    .append(literal("] ").formatted(DARK_GRAY))
                    .append(literal("Aktueller Bestand (geteilt von " + playerName + ")").formatted(GRAY))
                    .append(literal(":").formatted(DARK_GRAY));

            player.sendMessage(storageText, false);

            player.sendMessage(getIngredientText("Wirkstoff", ingredient1), false);
            player.sendMessage(getIngredientText("Trägerstoff", ingredient2), false);
            player.sendMessage(getIngredientText("Zusatzstoff", ingredient3), false);

            player.sendMessage(empty(), false);
            return false;
        }

        Matcher storageIngredientAcceptMatcher = STORAGE_INGREDIENT_ACCEPT_PATTERN.matcher(message);
        if (storageIngredientAcceptMatcher.find()) {
            String playerName = storageIngredientAcceptMatcher.group("playerName");
            String ingredient = storageIngredientAcceptMatcher.group("ingredient");
            int amountBefore = parseInt(storageIngredientAcceptMatcher.group("amountBefore"));
            int amountAfter = parseInt(storageIngredientAcceptMatcher.group("amountAfter"));

            player.sendMessage(empty()
                    .append(literal("[").formatted(DARK_GRAY))
                    .append(STORAGE_TEXT)
                    .append(literal("] ").formatted(DARK_GRAY))
                    .append(literal(playerName).formatted(RED))
                    .append(literal(" übernimmt ").formatted(GRAY))
                    .append(literal(ingredient).formatted(RED))
                    .append(literal(":").formatted(DARK_GRAY)), false);

            player.sendMessage(empty()
                    .append(literal("  > ").formatted(DARK_GRAY))
                    .append(literal("Geschätzte Menge nach Transport").formatted(GRAY))
                    .append(literal(": ").formatted(DARK_GRAY))
                    .append(literal(amountBefore + " → ").formatted(GRAY))
                    .append(literal(valueOf(amountAfter)).formatted(getColor(amountAfter), BOLD)), false);
            return false;
        }

        return true;
    }

    private MutableText getIngredientText(String ingredient, int amount) {
        return empty()
                .append(literal("  > ").formatted(DARK_GRAY))
                .append(literal("[").formatted(DARK_GRAY))
                .append(literal("Ich übernehme!").styled(style -> style
                        .withFormatting(DARK_AQUA)
                        .withHoverEvent(new HoverEvent.ShowText(literal("Klicke um bescheid zu sagen, dass Du den Transport übernimmst")))
                        .withClickEvent(new ClickEvent.RunCommand("/f Ich nehme " + ingredient + "! (geschätzt: " + amount + " → " + (amount + 20) + ")"))))
                .append(literal("] ").formatted(DARK_GRAY))
                .append(literal(ingredient).formatted(GRAY))
                .append(literal(": ").formatted(DARK_GRAY))
                .append(literal(valueOf(amount)).formatted(getColor(amount), BOLD));
    }

    private Formatting getColor(int amount) {
        if (amount >= 80) {
            return DARK_GREEN;
        } else if (amount >= 60) {
            return GREEN;
        } else if (amount >= 40) {
            return YELLOW;
        } else if (amount >= 20) {
            return GOLD;
        } else {
            return RED;
        }
    }
}

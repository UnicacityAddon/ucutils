package de.rettichlp.ucutils.listener.impl;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.common.services.MessageService.TIME_FORMAT;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.time.LocalTime.now;
import static java.util.regex.Pattern.compile;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.AQUA;
import static net.minecraft.util.Formatting.BLUE;
import static net.minecraft.util.Formatting.DARK_GRAY;

@UCUtilsListener
public class KarmaMessageListener implements IMessageReceiveListener {

    private static final Pattern KARMA_CHANGED_PATTERN = compile("^\\[Karma] (?<amount>[+-]\\d+) Karma\\.$");
    private static final Pattern KARMA_PATTERN = compile("^\\[Karma] Du hast ein Karma von (?<amount>[+-]\\d+)\\.$");

    private int lastKarmaChange = 0;

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Matcher karmaChangedMatcher = KARMA_CHANGED_PATTERN.matcher(message);
        if (karmaChangedMatcher.find()) {
            boolean showEnrichedKarma = configuration.getOptions().showEnrichedKarma();
            if (!showEnrichedKarma) {
                return true;
            }

            // show enriched karma message
            this.lastKarmaChange = parseInt(karmaChangedMatcher.group("amount"));
            commandService.sendCommandWithAfkCheck("karma");
            return false;
        }

        Matcher karmaMatcher = KARMA_PATTERN.matcher(message);
        if (karmaMatcher.find()) {
            // show default message if no karma was changed
            if (this.lastKarmaChange == 0) {
                return true;
            }

            int currentKarma = parseInt(karmaMatcher.group("amount"));

            MutableText enrichedKarmaMessage = empty()
                    .append(literal("[").formatted(DARK_GRAY))
                    .append(literal("Karma").formatted(BLUE))
                    .append(literal("] ").formatted(DARK_GRAY))
                    .append(literal((this.lastKarmaChange > 0 ? "+" : "") + this.lastKarmaChange + " ").formatted(AQUA))
                    .append(literal("Karma ").formatted(AQUA))
                    .append(literal("(").formatted(DARK_GRAY))
                    .append(literal(valueOf(currentKarma)).formatted(AQUA))
                    .append(literal("/").formatted(DARK_GRAY))
                    .append(literal("100").formatted(AQUA))
                    .append(literal(")").formatted(DARK_GRAY));

            // add despawn time if available
            if (this.lastKarmaChange < 0) {
                LocalTime despawnTime = now().plusMinutes(5);
                enrichedKarmaMessage
                        .append(literal(" (").formatted(DARK_GRAY))
                        .append(literal(TIME_FORMAT.format(despawnTime)).formatted(AQUA))
                        .append(literal(")").formatted(DARK_GRAY));
            }

            player.sendMessage(enrichedKarmaMessage, false);
            this.lastKarmaChange = 0;
            return false;
        }

        return true;
    }
}
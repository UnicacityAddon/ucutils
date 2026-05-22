package de.rettichlp.ucutils.listener.impl.faction;

import de.rettichlp.ucutils.common.Storage;
import de.rettichlp.ucutils.common.models.FactionMember;
import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import de.rettichlp.ucutils.listener.IMessageSendListener;
import lombok.NonNull;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.Storage.ToggledChat.NONE;
import static de.rettichlp.ucutils.common.configuration.options.Options.ReinforcementType.UNICACITYADDON;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.of;
import static net.minecraft.util.Formatting.AQUA;
import static net.minecraft.util.Formatting.BOLD;
import static net.minecraft.util.Formatting.DARK_AQUA;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.RED;

@UCUtilsListener
public class FactionListener implements IMessageReceiveListener, IMessageSendListener {

    private static final Pattern REINFORCEMENT_PATTERN = compile("^(?:(?<type>.+)! )?(?<senderRank>.+) (?:\\[UC])?(?<senderPlayerName>[a-zA-Z0-9_]+) benötigt Unterstützung in der Nähe von (?<naviPoint>.+)! \\((?<distance>\\d+) Meter entfernt\\)$");
    private static final Pattern REINFORCEMENT_BUTTON_PATTERN = compile("^ §7» §cRoute anzeigen §7\\| §cUnterwegs$");
    private static final Pattern REINFORCMENT_ON_THE_WAY_PATTERN = compile("^(?<senderRank>.+) (?:\\[UC])?(?<senderPlayerName>[a-zA-Z0-9_]+) kommt zum Verstärkungsruf von (?:\\[UC])?(?<target>[a-zA-Z0-9_]+)! \\((?<distance>\\d+) Meter entfernt\\)$");

    private static final Pattern FACTION_CHAT_PATTERN = compile("^(?<playerPrefix>[\\p{L} ]+) (?:\\[UC])?(?<senderPlayerName>[a-zA-Z0-9_]+): (?<message>.+)$");

    private static final ReinforcementConsumer<String, String, String, String> REINFORCEMENT = (type, sender, naviPoint, distance) -> empty()
            .append(of(type).copy().formatted(RED, BOLD)).append(" ")
            .append(of(sender).copy().formatted(AQUA)).append(" ")
            .append(of("-").copy().formatted(GRAY)).append(" ")
            .append(of(naviPoint).copy().formatted(AQUA)).append(" ")
            .append(of("-").copy().formatted(GRAY)).append(" ")
            .append(of(distance + "m").copy().formatted(DARK_AQUA));

    private static final ReinforcementOnTheWayConsumer<String, String, String> REINFORCEMENT_ON_THE_WAY = (sender, target, distance) -> empty()
            .append(of("➥").copy().formatted(GRAY)).append(" ")
            .append(of(sender).copy().formatted(AQUA)).append(" ")
            .append(of("➡").copy().formatted(GRAY)).append(" ")
            .append(of(target).copy().formatted(DARK_AQUA)).append(" ")
            .append(of("- (").copy().formatted(GRAY))
            .append(of(distance + "m").copy().formatted(DARK_AQUA))
            .append(of(")").copy().formatted(GRAY));

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Matcher reinforcementMatcher = REINFORCEMENT_PATTERN.matcher(message);
        if (reinforcementMatcher.find()) {
            String type = ofNullable(reinforcementMatcher.group("type")).orElse("Reinforcement");
            String senderRank = reinforcementMatcher.group("senderRank");
            String senderPlayerName = reinforcementMatcher.group("senderPlayerName");
            String naviPoint = reinforcementMatcher.group("naviPoint");
            String distance = reinforcementMatcher.group("distance");

            boolean modernReinforcementStyle = configuration.getOptions().reinforcementType() == UNICACITYADDON;
            if (modernReinforcementStyle) {
                Text reinforcementText = REINFORCEMENT.create(type, senderRank + " " + senderPlayerName, naviPoint, distance);
                player.sendMessage(empty(), false);
                player.sendMessage(reinforcementText, false);
            }

            return !modernReinforcementStyle;
        }

        Matcher reinforcementButtonMatcher = REINFORCEMENT_BUTTON_PATTERN.matcher(message);
        if (reinforcementButtonMatcher.find()) {
            boolean modernReinforcementStyle = configuration.getOptions().reinforcementType() == UNICACITYADDON;
            if (modernReinforcementStyle) {
                // send empty line after buttons
                MinecraftClient.getInstance().execute(() -> player.sendMessage(empty(), false));
            }

            return true;
        }

        Matcher reinforcementOnTheWayMatcher = REINFORCMENT_ON_THE_WAY_PATTERN.matcher(message);
        if (reinforcementOnTheWayMatcher.find()) {
            String senderRank = reinforcementOnTheWayMatcher.group("senderRank");
            String senderPlayerName = reinforcementOnTheWayMatcher.group("senderPlayerName");
            String target = reinforcementOnTheWayMatcher.group("target");
            String distance = reinforcementOnTheWayMatcher.group("distance");

            boolean modernReinforcementStyle = configuration.getOptions().reinforcementType() == UNICACITYADDON;
            if (modernReinforcementStyle) {
                Text reinforcementAnswer = REINFORCEMENT_ON_THE_WAY.create(senderRank + " " + senderPlayerName, target, distance);
                player.sendMessage(reinforcementAnswer, false);
            }

            return !modernReinforcementStyle;
        }

        Matcher factionChatMatcher = FACTION_CHAT_PATTERN.matcher(message);
        if (factionChatMatcher.find()) {
            if (!configuration.getOptions().changeFactionChatColor()) {
                return true;
            }

            Formatting primaryFormatting = configuration.getOptions().factionChatColorPrimary().getFormatting();
            Formatting secondaryFormatting = configuration.getOptions().factionChatColorSecondary().getFormatting();

            // check if color already matches formatting
            List<Text> siblings = text.getSiblings();
            if (siblings.size() != 3 || messageMatchesColor(siblings, primaryFormatting, secondaryFormatting)) {
                return true;
            }

            String playerPrefix = factionChatMatcher.group("playerPrefix");
            String senderPlayerName = factionChatMatcher.group("senderPlayerName");
            String factionMessage = factionChatMatcher.group("message");

            Optional<FactionMember> optionalFactionMember = storage.getFactionMember(senderPlayerName);
            if (optionalFactionMember.isEmpty()) {
                return true;
            }

            String rankName = optionalFactionMember.get().rankName();
            if (!playerPrefix.equals(rankName)) {
                return true;
            }

            player.sendMessage(empty()
                    .append(literal(playerPrefix).formatted(primaryFormatting))
                    .append(literal(" "))
                    .append(literal(senderPlayerName).formatted(primaryFormatting))
                    .append(literal(": ").formatted(DARK_GRAY))
                    .append(literal(factionMessage).formatted(secondaryFormatting)), false);

            return false;
        }

        return true;
    }

    @Override
    public boolean onMessageSend(String message) {
        Storage.ToggledChat toggledChat = storage.getToggledChat();
        if (toggledChat != NONE) {
            commandService.sendCommand(toggledChat.getCommand() + " " + message);
            return false;
        }

        return true;
    }

    private boolean messageMatchesColor(@NonNull List<Text> siblings, Formatting primaryFormatting, Formatting secondaryFormatting) {
        TextColor primaryFormattingCurrent = siblings.get(0).getStyle().getColor();
        TextColor secondaryFormattingCurrent = siblings.get(2).getStyle().getColor();
        return primaryFormattingCurrent == null || secondaryFormattingCurrent == null || primaryFormattingCurrent.getRgb() == primaryFormatting.getColorValue() || secondaryFormattingCurrent.getRgb() == secondaryFormatting.getColorValue();
    }

    @FunctionalInterface
    public interface ReinforcementConsumer<Type, Sender, NaviPoint, Distance> {

        Text create(String type, String sender, String naviPoint, String distance);
    }

    @FunctionalInterface
    public interface ReinforcementOnTheWayConsumer<Sender, Target, Distance> {

        Text create(String sender, String target, String distance);
    }
}

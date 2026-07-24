package de.rettichlp.ucutils.listener.impl.faction;

import de.rettichlp.ucutils.common.Storage;
import de.rettichlp.ucutils.common.models.FactionMember;
import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import de.rettichlp.ucutils.listener.IMessageSendListener;
import lombok.NonNull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

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
import static net.minecraft.ChatFormatting.AQUA;
import static net.minecraft.ChatFormatting.BOLD;
import static net.minecraft.ChatFormatting.DARK_AQUA;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

@UCUtilsListener
public class FactionListener implements IMessageReceiveListener, IMessageSendListener {

    private static final Pattern REINFORCEMENT_PATTERN = compile("^(?:(?<type>.+)! )?(?<senderRank>.+) (?:\\[UC])?(?<senderPlayerName>[a-zA-Z0-9_]+) benötigt Unterstützung in der Nähe von (?<naviPoint>.+)! \\((?<distance>\\d+) Meter entfernt\\)$");
    private static final Pattern REINFORCEMENT_BUTTON_PATTERN = compile("^ §7» §cRoute anzeigen §7\\| §cUnterwegs$");
    private static final Pattern REINFORCMENT_ON_THE_WAY_PATTERN = compile("^(?<senderRank>.+) (?:\\[UC])?(?<senderPlayerName>[a-zA-Z0-9_]+) kommt zum Verstärkungsruf von (?:\\[UC])?(?<target>[a-zA-Z0-9_]+)! \\((?<distance>\\d+) Meter entfernt\\)$");

    private static final Pattern FACTION_CHAT_PATTERN = compile("^(?<playerPrefix>[\\p{L} ]+) (?:\\[UC])?(?<senderPlayerName>[a-zA-Z0-9_]+): (?<message>.+)$");

    private static final ReinforcementConsumer<String, String, String, String> REINFORCEMENT = (type, sender, naviPoint, distance) -> empty()
            .append(literal(type).withStyle(RED, BOLD)).append(" ")
            .append(literal(sender).withStyle(AQUA)).append(" ")
            .append(literal("-").withStyle(GRAY)).append(" ")
            .append(literal(naviPoint).withStyle(AQUA)).append(" ")
            .append(literal("-").withStyle(GRAY)).append(" ")
            .append(literal(distance + "m").withStyle(DARK_AQUA));

    private static final ReinforcementOnTheWayConsumer<String, String, String> REINFORCEMENT_ON_THE_WAY = (sender, target, distance) -> empty()
            .append(literal("➥").withStyle(GRAY)).append(" ")
            .append(literal(sender).withStyle(AQUA)).append(" ")
            .append(literal("➡").withStyle(GRAY)).append(" ")
            .append(literal(target).withStyle(DARK_AQUA)).append(" ")
            .append(literal("- (").withStyle(GRAY))
            .append(literal(distance + "m").withStyle(DARK_AQUA))
            .append(literal(")").withStyle(GRAY));

    @Override
    public boolean onMessageReceive(Component text, String message) {
        Matcher reinforcementMatcher = REINFORCEMENT_PATTERN.matcher(message);
        if (reinforcementMatcher.find()) {
            String type = ofNullable(reinforcementMatcher.group("type")).orElse("Reinforcement");
            String senderRank = reinforcementMatcher.group("senderRank");
            String senderPlayerName = reinforcementMatcher.group("senderPlayerName");
            String naviPoint = reinforcementMatcher.group("naviPoint");
            String distance = reinforcementMatcher.group("distance");

            boolean modernReinforcementStyle = configuration.getOptions().reinforcementType() == UNICACITYADDON;
            if (modernReinforcementStyle) {
                Component reinforcementText = REINFORCEMENT.create(type, senderRank + " " + senderPlayerName, naviPoint, distance);
                player.sendSystemMessage(empty());
                player.sendSystemMessage(reinforcementText);
            }

            return !modernReinforcementStyle;
        }

        Matcher reinforcementButtonMatcher = REINFORCEMENT_BUTTON_PATTERN.matcher(message);
        if (reinforcementButtonMatcher.find()) {
            boolean modernReinforcementStyle = configuration.getOptions().reinforcementType() == UNICACITYADDON;
            if (modernReinforcementStyle) {
                // send empty line after buttons
                Minecraft.getInstance().execute(() -> player.sendSystemMessage(empty()));
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
                Component reinforcementAnswer = REINFORCEMENT_ON_THE_WAY.create(senderRank + " " + senderPlayerName, target, distance);
                player.sendSystemMessage(reinforcementAnswer);
            }

            return !modernReinforcementStyle;
        }

        Matcher factionChatMatcher = FACTION_CHAT_PATTERN.matcher(message);
        if (factionChatMatcher.find()) {
            if (!configuration.getOptions().chatOptions().changeFactionChatColor()) {
                return true;
            }

            ChatFormatting primaryFormatting = configuration.getOptions().chatOptions().factionChatColorPrimary().getFormatting();
            ChatFormatting secondaryFormatting = configuration.getOptions().chatOptions().factionChatColorSecondary().getFormatting();

            // check if color already matches formatting
            List<Component> siblings = text.getSiblings();
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

            player.sendSystemMessage(empty()
                    .append(literal(playerPrefix).withStyle(primaryFormatting))
                    .append(literal(" "))
                    .append(literal(senderPlayerName).withStyle(primaryFormatting))
                    .append(literal(": ").withStyle(DARK_GRAY))
                    .append(literal(factionMessage).withStyle(secondaryFormatting)));

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

    private boolean messageMatchesColor(@NonNull List<Component> siblings,
                                        ChatFormatting primaryFormatting,
                                        ChatFormatting secondaryFormatting) {
        TextColor primaryFormattingCurrent = siblings.get(0).getStyle().getColor();
        TextColor secondaryFormattingCurrent = siblings.get(2).getStyle().getColor();
        return primaryFormattingCurrent == null || secondaryFormattingCurrent == null || primaryFormattingCurrent.getValue() == primaryFormatting.getColor() || secondaryFormattingCurrent.getValue() == secondaryFormatting.getColor();
    }

    @FunctionalInterface
    public interface ReinforcementConsumer<Type, Sender, NaviPoint, Distance> {

        Component create(String type, String sender, String naviPoint, String distance);
    }

    @FunctionalInterface
    public interface ReinforcementOnTheWayConsumer<Sender, Target, Distance> {

        Component create(String sender, String target, String distance);
    }
}

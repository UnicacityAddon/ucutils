package de.rettichlp.ucutils.listener.impl.faction;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.BOLD;
import static net.minecraft.util.Formatting.DARK_RED;

@UCUtilsListener
public class BadFactionListener implements IMessageReceiveListener {

    private static final Pattern BLACK_MARKET_DEALER_ENTRY_PATTERN = compile("^» (?<location>.+) – (gerade eben|vor \\d+ min|vor \\d+ std) {2}\\[Navi]$");

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Matcher blackMarketDealerEntryMatcher = BLACK_MARKET_DEALER_ENTRY_PATTERN.matcher(message);
        if (blackMarketDealerEntryMatcher.find()) {
            Vec3d blackMarketPosition = storage.getBlackMarketPosition();
            Vec3d dealerPosition = storage.getDealerPosition();

            // extract location from message
            Text last = text.getSiblings().getLast();
            ClickEvent clickEvent = last.getStyle().getClickEvent();
            if (!(clickEvent instanceof ClickEvent.RunCommand(String command))) {
                return true;
            }

            String[] locationPieces = command.replace("/navi ", "").split("/");

            if (locationPieces.length != 3) {
                return true;
            }

            Vec3d extractedPosition = new Vec3d(parseInt(locationPieces[0]), parseInt(locationPieces[1]), parseInt(locationPieces[2]));

            if ((blackMarketPosition != null && extractedPosition.distanceTo(blackMarketPosition) < 5) || (dealerPosition != null && extractedPosition.distanceTo(dealerPosition) < 5)) {
                MutableText text1 = text.copy().append(literal(" ⯐").formatted(DARK_RED, BOLD));
                player.sendMessage(text1, false);
                return false;
            }

            return true;
        }

        return true;
    }
}

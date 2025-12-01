package de.rettichlp.ucutils.listener.impl;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.storage;
import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;

@UCUtilsListener
public class MobileListener implements IMessageReceiveListener {

    private static final Pattern MOBILE_NUMBER_PATTERN = compile("^(?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) gehört die Nummer (?<number>\\d+)\\.$");
    private static final Pattern MOBILE_SMS_RECEIVE_PATTERN = compile("^Dein Handy klingelt! Eine Nachricht von (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) \\((?<number>\\d+)\\)\\.$");

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Matcher mobileNumberMatcher = MOBILE_NUMBER_PATTERN.matcher(message);
        if (mobileNumberMatcher.find()) {
            String playerName = mobileNumberMatcher.group("playerName");
            int number = parseInt(mobileNumberMatcher.group("number"));
            storage.getRetrievedNumbers().put(playerName, number);
            return true;
        }

        Matcher mobileSmsReceiveMatcher = MOBILE_SMS_RECEIVE_PATTERN.matcher(message);
        if (mobileSmsReceiveMatcher.find()) {
            String playerName = mobileSmsReceiveMatcher.group("playerName");
            int number = parseInt(mobileSmsReceiveMatcher.group("number"));

            storage.getRetrievedNumbers().put(playerName, number);
            storage.setLastReceivedSmsNumber(number);

            return true;
        }

        return true;
    }
}

package de.rettichlp.ucutils.listener.impl;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static java.util.regex.Pattern.compile;

@UCUtilsListener
public class PrayListener implements IMessageReceiveListener {

    private static final Pattern PRAYING_START_PATTERN = compile("^\\[Kirche] Du hast begonnen für (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) zu beten\\.$");

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Matcher prayingStartMatcher = PRAYING_START_PATTERN.matcher(message);
        if (prayingStartMatcher.find()) {
            String playerName = prayingStartMatcher.group("playerName");
            utilService.delayedAction(() -> commandService.sendCommandWithAfkCheck("beten " + playerName), 15000);
            return true;
        }

        return true;
    }
}

package de.rettichlp.ucutils.listener.impl;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.network.chat.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.services.CommandService.COMMAND_COOLDOWN_MILLIS;
import static java.util.regex.Pattern.compile;

@UCUtilsListener
public class MobileListener implements IMessageReceiveListener {

    private static final Pattern MOBILE_OFF_PATTERN = compile("^Dein Handy ist ausgeschaltet\\.$");

    @Override
    public boolean onMessageReceive(Component text, String message) {
        Matcher mobileOffMatcher = MOBILE_OFF_PATTERN.matcher(message);
        if (mobileOffMatcher.find()) {
            utilService.delayedAction(() -> commandService.sendCommand("togglephone"), COMMAND_COOLDOWN_MILLIS);
            return true;
        }

        return true;
    }
}

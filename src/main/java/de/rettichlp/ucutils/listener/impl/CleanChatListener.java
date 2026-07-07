package de.rettichlp.ucutils.listener.impl;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.network.chat.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

@UCUtilsListener
public class CleanChatListener implements IMessageReceiveListener {

    private static final Pattern TRASH_CAN_PATTERN = compile("^Du durchwühlst den Mülleimer\\.$");

    @Override
    public boolean onMessageReceive(Component text, String message) {
        Matcher trashCanMatcher = TRASH_CAN_PATTERN.matcher(message);
        if (trashCanMatcher.find()) {
            return false;
        }

        return true;
    }
}

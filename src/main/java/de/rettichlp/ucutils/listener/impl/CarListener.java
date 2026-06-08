package de.rettichlp.ucutils.listener.impl;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;

@UCUtilsListener
public class CarListener implements IMessageReceiveListener {

    private static final Pattern CAR_UNLOCK_PATTERN = compile("^\\[Car] Du hast deinen .+ aufgeschlossen\\.$");
    private static final Pattern CAR_LOCK_PATTERN = compile("^\\[Car] Du hast deinen .+ abgeschlossen\\.$");
    private static final Pattern CAR_LOCKED_OWN_PATTERN = compile("^\\[Car] Dein Fahrzeug ist abgeschlossen\\.$");
    private static final Pattern CAR_FIND_PATTERN = compile("^\\[Car] Das Fahrzeug befindet sich bei » X: (?<x>-?\\d+) \\| Y: (?<y>-?\\d+) \\| Z: (?<z>-?\\d+)$");

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Matcher carUnlockMatcher = CAR_UNLOCK_PATTERN.matcher(message);
        if (carUnlockMatcher.find()) {
            storage.setCarLocked(false);
            return true;
        }

        Matcher carLockMatcher = CAR_LOCK_PATTERN.matcher(message);
        if (carLockMatcher.find()) {
            storage.setCarLocked(true);
            return true;
        }

        Matcher carLockedOwnMatcher = CAR_LOCKED_OWN_PATTERN.matcher(message);
        if (carLockedOwnMatcher.find()) {
            commandService.sendCommand("car lock");
            return true;
        }

        Matcher carFindMatcher = CAR_FIND_PATTERN.matcher(message);
        if (carFindMatcher.find() && configuration.getOptions().car().fastFind()) {
            int x = parseInt(carFindMatcher.group("x"));
            int y = parseInt(carFindMatcher.group("y"));
            int z = parseInt(carFindMatcher.group("z"));
            commandService.sendCommand("navi " + x + "/" + y + "/" + z);
            return true;
        }

        return true;
    }
}

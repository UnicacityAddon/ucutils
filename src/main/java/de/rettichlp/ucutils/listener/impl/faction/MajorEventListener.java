package de.rettichlp.ucutils.listener.impl.faction;

import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.models.Sound.BANK_ROBBERY;
import static de.rettichlp.ucutils.common.models.Sound.BOMB_SOUND;
import static java.util.regex.Pattern.compile;

@UCUtilsListener
public class MajorEventListener implements IMessageReceiveListener {

    private static final Pattern BANK_ROBBERY_PATTERN = compile("^News: Es wurde ein Raub in der Staatsbank gemeldet!$");
    private static final Pattern BOMB_FOUND_PATTERN = compile("^News: ACHTUNG! Es wurde eine Bombe in der Nähe von (?<location>.+) gefunden!$");

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Faction playerFaction = storage.getFaction(player.getGameProfile().name());

        Matcher bankRobberyMatcher = BANK_ROBBERY_PATTERN.matcher(message);
        if (bankRobberyMatcher.find() && configuration.getOptions().sound().bankRobbery().verify(playerFaction)) {
            BANK_ROBBERY.play();
            return true;
        }

        Matcher bombFoundMatcher = BOMB_FOUND_PATTERN.matcher(message);
        if (bombFoundMatcher.find() && configuration.getOptions().sound().bomb().verify(playerFaction)) {
            BOMB_SOUND.play();
            return true;
        }

        return true;
    }
}

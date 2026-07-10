package de.rettichlp.ucutils.listener.impl;

import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.network.chat.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.models.Sound.BANK_ROBBERY;
import static de.rettichlp.ucutils.common.models.Sound.BOMB_SOUND;
import static de.rettichlp.ucutils.common.models.Sound.CONTRACT_FULFILLED;
import static de.rettichlp.ucutils.common.models.Sound.CONTRACT_SET;
import static de.rettichlp.ucutils.common.models.Sound.FIRE;
import static java.util.regex.Pattern.compile;

@UCUtilsListener
public class SoundTriggerListener implements IMessageReceiveListener {

    private static final Pattern BANK_ROBBERY_PATTERN = compile("^News: Es wurde ein Raub in der Staatsbank gemeldet!$");
    private static final Pattern BOMB_FOUND_PATTERN = compile("^News: ACHTUNG! Es wurde eine Bombe in der Nähe von (?<location>.+) gefunden!$");
    private static final Pattern CONTRACT_ADD_PATTERN = compile("^\\[Contract] Es wurde ein Kopfgeld auf (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+) \\((?<price>\\d+)\\$\\) ausgesetzt\\.$");
    private static final Pattern CONTRACT_KILL_PATTERN = compile("^\\[Contract] (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+) getötet\\. Kopfgeld: (?<price>\\d+)\\$$");
    private static final Pattern FIRE_START_PATTERN = compile("^News: Es wurde ein Feuer bei .+ gemeldet!$");

    @Override
    public boolean onMessageReceive(Component text, String message) {
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

        Matcher contractAddMatcher = CONTRACT_ADD_PATTERN.matcher(message);
        if (contractAddMatcher.find() && configuration.getOptions().sound().contractSet()) {
            CONTRACT_SET.play();
            return true;
        }

        Matcher contractKillMatcher = CONTRACT_KILL_PATTERN.matcher(message);
        if (contractKillMatcher.find() && configuration.getOptions().sound().contractFulfilled()) {
            CONTRACT_FULFILLED.play();
            return true;
        }

        Matcher fireStartMatcher = FIRE_START_PATTERN.matcher(message);
        if (fireStartMatcher.find() && configuration.getOptions().sound().fire().verify(playerFaction)) {
            FIRE.play();
            return true;
        }

        return true;
    }
}

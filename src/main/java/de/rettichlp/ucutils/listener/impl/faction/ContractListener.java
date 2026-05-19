package de.rettichlp.ucutils.listener.impl.faction;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.common.models.Sound.CONTRACT_FULFILLED;
import static de.rettichlp.ucutils.common.models.Sound.CONTRACT_SET;
import static java.util.regex.Pattern.compile;

@UCUtilsListener
public class ContractListener implements IMessageReceiveListener {

    private static final Pattern CONTRACT_ADD_PATTERN = compile("^\\[Contract] Es wurde ein Kopfgeld auf (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+) \\((?<price>\\d+)\\$\\) ausgesetzt\\.$");
    private static final Pattern CONTRACT_KILL_PATTERN = compile("^\\[Contract] (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat (?:\\[UC])?(?<targetName>[a-zA-Z0-9_]+) getötet\\. Kopfgeld: (?<price>\\d+)\\$$");

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Matcher contractAddMatcher = CONTRACT_ADD_PATTERN.matcher(message);
        if (contractAddMatcher.find()) {
            if (configuration.getOptions().sound().contractSet()) {
                CONTRACT_SET.play();
            }

            return true;
        }

        Matcher contractKillMatcher = CONTRACT_KILL_PATTERN.matcher(message);
        if (contractKillMatcher.find()) {
            if (configuration.getOptions().sound().contractFulfilled()) {
                CONTRACT_FULFILLED.play();
            }

            return true;
        }

        return true;
    }
}

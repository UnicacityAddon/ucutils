package de.rettichlp.ucutils.command.money;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.messageService;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.services.CommandService.COMMAND_COOLDOWN_MILLIS;
import static java.lang.Integer.parseInt;
import static java.lang.Math.min;
import static java.util.regex.Pattern.compile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@UCUtilsCommand(label = "einzahlen")
@UCUtilsListener
public class DepositCommand extends CommandBase implements IMessageReceiveListener {

    private static final Pattern PLAYER_MONEY_AMOUNT_PATTERN = compile("- Geld: (?<moneyAmount>\\d+)\\$");
    private static final Pattern ATM_MONEY_AMOUNT_PATTERN = compile("^ATM (?<number>\\d+): (?<amountCurrent>\\d+)\\$/(?<amountMax>\\d+)\\$$");

    private static int money = 0;
    private static int atmMoneyAvailable = -1;

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .then(literal("force")
                        .executes(context -> {
                            commandService.sendCommand("stats");

                            utilService.delayedAction(() -> {
                                if (money <= 0) {
                                    messageService.sendModMessage("Du hast kein Geld zum Einzahlen.", false);
                                    return;
                                }

                                commandService.sendCommand("bank einzahlen " + money);
                                messageService.sendModMessage("Nutze \"force\" bitte nur in Notfällen um zu vermeiden dass AMTs überfüllt werden und Geld dadurch \"verloren\" geht.", false);
                            }, COMMAND_COOLDOWN_MILLIS);

                            return 1;
                        })
                )
                .executes(context -> {
                    commandService.sendCommand("stats");
                    utilService.delayedAction(() -> commandService.sendCommandWithHiddenOutput("atminfo"), COMMAND_COOLDOWN_MILLIS);

                    utilService.delayedAction(() -> {
                        if (money <= 0) {
                            messageService.sendModMessage("Du hast kein Geld zum Einzahlen.", false);
                            return;
                        }

                        if (atmMoneyAvailable == 0) {
                            messageService.sendModMessage("Der Bankautomat ist voll.", false);
                            messageService.sendModMessage("Nutze einen anderen ATM oder '/einzahlen force' um dennoch Geld in diesen ATM zu legen.", false);
                        } else if (atmMoneyAvailable == -1) {
                            // in case atm info is not available
                            commandService.sendCommand("bank einzahlen " + money);
                        } else {
                            commandService.sendCommand("bank einzahlen " + min(atmMoneyAvailable, money));
                        }
                    }, COMMAND_COOLDOWN_MILLIS * 2);

                    return 1;
                });
    }

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Matcher playerMoneyAmountMatcher = PLAYER_MONEY_AMOUNT_PATTERN.matcher(message);
        if (playerMoneyAmountMatcher.find()) {
            money = parseInt(playerMoneyAmountMatcher.group("moneyAmount"));
            return true;
        }

        Matcher atmMoneyAmountMatcher = ATM_MONEY_AMOUNT_PATTERN.matcher(message);
        if (atmMoneyAmountMatcher.find()) {
            int amountCurrent = parseInt(atmMoneyAmountMatcher.group("amountCurrent"));
            int amountMax = parseInt(atmMoneyAmountMatcher.group("amountMax"));
            atmMoneyAvailable = amountMax - amountCurrent;
            return commandService.showCommandOutputMessage("atminfo");
        }

        return true;
    }
}

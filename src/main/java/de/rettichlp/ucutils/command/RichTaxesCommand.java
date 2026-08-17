package de.rettichlp.ucutils.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.messageService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.services.CommandService.COMMAND_COOLDOWN_MILLIS;

@UCUtilsCommand(label = "reichensteuer")
public class RichTaxesCommand extends CommandBase {

    private static final int RICH_TAXES_THRESHOLD = 100000;

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .executes(context -> {
                    if (storage.isPremium()) {
                        commandService.sendCommand("reichensteuern");
                        return 1;
                    }

                    // execute command to check money on the bank of player
                    commandService.sendCommand("atminfo");

                    // handle money withdraw
                    utilService.delayedAction(() -> {
                        int moneyAtmAmount = storage.getMoneyAtmAmount();
                        int moneyBankAmount = configuration.getMoneyBankAmount();

                        // check atm has money
                        if (moneyAtmAmount <= 0) {
                            messageService.sendModMessage("Der ATM hat kein Geld.", false);
                            return;
                        }

                        // check player has rich taxes
                        if (moneyBankAmount <= RICH_TAXES_THRESHOLD) {
                            messageService.sendModMessage("Du hast nicht ausreichend Geld auf der Bank.", false);
                            return;
                        }

                        int moneyThatNeedsToBeWithdrawn = moneyBankAmount - RICH_TAXES_THRESHOLD;

                        if (moneyAtmAmount >= moneyThatNeedsToBeWithdrawn) {
                            commandService.sendCommand("bank abbuchen " + moneyThatNeedsToBeWithdrawn);
                        } else {
                            commandService.sendCommand("bank abbuchen " + moneyAtmAmount);
                            messageService.sendModMessage("Du musst noch " + (moneyThatNeedsToBeWithdrawn - moneyAtmAmount) + "$ abbuchen.", false);
                        }
                    }, COMMAND_COOLDOWN_MILLIS);

                    return 1;
                });
    }
}

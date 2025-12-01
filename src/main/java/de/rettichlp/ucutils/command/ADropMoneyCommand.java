package de.rettichlp.ucutils.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.messageService;
import static de.rettichlp.ucutils.UCUtils.player;
import static java.lang.Math.min;
import static java.util.Optional.ofNullable;
import static net.minecraft.scoreboard.ScoreHolder.fromName;
import static net.minecraft.scoreboard.ScoreboardDisplaySlot.SIDEBAR;

@UCUtilsCommand(label = "adropmoney")
public class ADropMoneyCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .executes(context -> {
                    Scoreboard scoreboard = player.getScoreboard();

                    Optional<ReadableScoreboardScore> optionalReadableScoreboardScore = ofNullable(scoreboard.getObjectiveForSlot(SIDEBAR))
                            .map(scoreboardObjective -> scoreboard.getScore(fromName("§9Geld§8:"), scoreboardObjective));

                    if (optionalReadableScoreboardScore.isEmpty()) {
                        messageService.sendModMessage("Der Geldtransport-Job wird nicht ausgeführt.", false);
                        return 1;
                    }

                    List<String> scheduledCommands = getScheduledCommands(optionalReadableScoreboardScore);

                    commandService.sendCommands(scheduledCommands);

                    return 1;
                });
    }

    private @NotNull List<String> getScheduledCommands(@NotNull Optional<ReadableScoreboardScore> optionalReadableScoreboardScore) {
        ReadableScoreboardScore readableScoreboardScore = optionalReadableScoreboardScore.get();
        int moneyToDrop = readableScoreboardScore.getScore();

        List<String> scheduledCommands = new ArrayList<>();

        while (moneyToDrop > 0) {
            int moneyCanDrop = min(moneyToDrop, configuration.getMoneyBankAmount());
            scheduledCommands.add("bank abbuchen " + moneyCanDrop);
            scheduledCommands.add("dropmoney");
            scheduledCommands.add("bank einzahlen " + moneyCanDrop);
            moneyToDrop -= moneyCanDrop;
        }
        return scheduledCommands;
    }
}

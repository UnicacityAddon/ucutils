package de.rettichlp.ucutils.command.atm;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static de.rettichlp.ucutils.UCUtils.commandService;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;

@UCUtilsCommand(label = "überweisen")
public class AtmTransferCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .then(argument("player", word())
                        .suggests(PLAYER_NAMES_SUGGESTION_PROVIDER)
                        .then(argument("amount", integer(1))
                                .then(argument("reason", greedyString())
                                        .executes(context -> {
                                            String player = context.getArgument("player", String.class);
                                            int amount = getInteger(context, "amount");
                                            String reason = context.getArgument("reason", String.class);
                                            commandService.sendCommand("bank überweisen " + player + " " + amount + " " + reason);
                                            return 1;
                                        }))
                                .executes(context -> {
                                    String player = context.getArgument("player", String.class);
                                    int amount = getInteger(context, "amount");
                                    commandService.sendCommand("bank überweisen " + player + " " + amount);
                                    return 1;
                                })));
    }
}

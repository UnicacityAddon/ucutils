package de.rettichlp.ucutils.command.atm;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static de.rettichlp.ucutils.UCUtils.commandService;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;

@UCUtilsCommand(label = "abbuchen")
public class AtmWithdrawCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .then(argument("amount", integer(1))
                        .executes(context -> {
                            int amount = getInteger(context, "amount");
                            commandService.sendCommand("bank abbuchen " + amount);
                            return 1;
                        }));
    }
}

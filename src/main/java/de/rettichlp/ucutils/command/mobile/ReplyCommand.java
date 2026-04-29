package de.rettichlp.ucutils.command.mobile;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.messageService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

@UCUtilsCommand(label = "reply", aliases = "r")
public class ReplyCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .then(argument("message", greedyString())
                        .executes(context -> {
                            String message = getString(context, "message");

                            int lastReceivedSmsNumber = storage.getLastReceivedSmsNumber();

                            if (lastReceivedSmsNumber < 0) {
                                messageService.sendModMessage("Kein SMS-Empfänger gefunden.", false);
                                return 1;
                            }

                            commandService.sendCommand("sms " + lastReceivedSmsNumber + " " + message);

                            return 1;
                        }));
    }
}

package de.rettichlp.ucutils.command.chat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static de.rettichlp.ucutils.UCUtils.commandService;
import static java.lang.String.join;
import static java.util.Arrays.stream;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;

@UCUtilsCommand(label = "vm")
public class AttemptedMurderCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .then(argument("players", greedyString())
                        .suggests(PLAYER_NAMES_SUGGESTION_PROVIDER)
                        .executes(context -> {
                            String playerString = getString(context, "players");

                            List<String> playerNames = stream(playerString.split(" "))
                                    .filter(name -> !name.isEmpty())
                                    .toList();

                            String command = "asu " + join(" ", playerNames) + " Versuchter Mord";
                            commandService.sendCommand(command);
                            return 1;
                        }));
    }
}

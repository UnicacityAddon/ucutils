package de.rettichlp.ucutils.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.ucutils.common.models.ShutdownReason.CEMETERY;
import static de.rettichlp.ucutils.common.models.ShutdownReason.JAIL;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

@UCUtilsCommand(label = "shutdown")
public class ShutdownCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .then(literal("friedhof")
                        .executes(_ -> {
                            CEMETERY.activate();
                            return 1;
                        }))
                .then(literal("gefängnis")
                        .executes(_ -> {
                            JAIL.activate();
                            return 1;
                        }));
    }
}

package de.rettichlp.ucutils.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.storage;

@UCUtilsCommand(label = "aktien")
public class StockMarketCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .executes(_ -> {
                    storage.setStockMarketCommandRunning(true);
                    commandService.sendCommand("mobile");
                    return 1;
                });
    }
}

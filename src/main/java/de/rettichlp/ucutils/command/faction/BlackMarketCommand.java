package de.rettichlp.ucutils.command.faction;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.ucutils.UCUtils.messageService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.util.Comparator.comparingDouble;
import static net.minecraft.text.Text.empty;

@UCUtilsCommand(label = "blackmarket", aliases = "schwarzmarkt")
public class BlackMarketCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .executes(context -> {
                    player.sendMessage(empty(), false);

                    messageService.sendModMessage("Schwarzmarkt Orte:", false);
                    storage.getBlackMarkets().stream()
                            .sorted(comparingDouble(value -> value.getType().getBlockPos().getSquaredDistance(player.getBlockPos())))
                            .forEach(blackMarket -> messageService.sendModMessage(blackMarket.getText(), false));

                    player.sendMessage(empty(), false);

                    return 1;
                });
    }
}

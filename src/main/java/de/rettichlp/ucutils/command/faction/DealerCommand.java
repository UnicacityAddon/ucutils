package de.rettichlp.ucutils.command.faction;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.messageService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.util.Comparator.comparingDouble;
import static net.minecraft.text.Text.empty;

@UCUtilsCommand(label = "dealer")
public class DealerCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .requires(fabricClientCommandSource -> storage.getFaction(player.getStringifiedName()).isBadFaction() || commandService.isSuperUser())
                .executes(context -> {
                    player.sendMessage(empty(), false);

                    messageService.sendModMessage("Dealer Orte:", false);
                    storage.getDealers().stream()
                            .sorted(comparingDouble(value -> value.getType().getBlockPos().getSquaredDistance(player.getBlockPos())))
                            .forEach(dealer -> messageService.sendModMessage(dealer.getText(), false));

                    player.sendMessage(empty(), false);

                    return 1;
                });
    }
}

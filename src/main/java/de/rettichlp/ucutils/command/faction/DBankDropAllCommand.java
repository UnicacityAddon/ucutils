package de.rettichlp.ucutils.command.faction;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.models.Ingredient;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import de.rettichlp.ucutils.listener.impl.InventoryListener;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.models.Ingredient.POWDER;

@UCUtilsCommand(label = "dbankdropall", aliases = "dda")
public class DBankDropAllCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .executes(context -> {
                    // sync inventory
                    InventoryListener.inventorySyncStep = POWDER;
                    commandService.sendCommand("inv");

                    utilService.delayedAction(() -> {
                        // handle
                        List<String> commandQueue = new ArrayList<>();

                        storage.getInventory().entrySet().stream()
                                .filter(ingredientMapEntry -> ingredientMapEntry.getKey().isDrugBankDropable())
                                .forEach(ingredientMapEntry -> {
                                    Ingredient ingredient = ingredientMapEntry.getKey();

                                    ingredientMapEntry.getValue().entrySet().stream()
                                            .filter(purityIntegerEntry -> purityIntegerEntry.getValue() > 0)
                                            .forEach(purityIntegerEntry -> {
                                                int purityNumber = purityIntegerEntry.getKey().ordinal();
                                                Integer amount = purityIntegerEntry.getValue();
                                                commandQueue.add("dbank drop " + ingredient.getDisplayName() + " " + amount + " " + purityNumber);
                                            });
                                });

                        commandService.sendCommands(commandQueue, 1000);
                    }, 2500);

                    return 1;
                });
    }
}

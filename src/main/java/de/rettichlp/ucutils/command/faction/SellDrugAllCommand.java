package de.rettichlp.ucutils.command.faction;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.models.InventoryItem;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import de.rettichlp.ucutils.listener.impl.InventoryListener;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.PlayerListEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.networkHandler;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.models.InventoryItem.POWDER;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.minecraft.command.CommandSource.suggestMatching;

@UCUtilsCommand(label = "selldrugall", aliases = "sda")
public class SellDrugAllCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .then(argument("player", word())
                        .suggests((context, builder) -> {
                            List<String> list = networkHandler.getPlayerList().stream()
                                    .map(PlayerListEntry::getProfile)
                                    .map(GameProfile::name)
                                    .toList();
                            return suggestMatching(list, builder);
                        })
                        .executes(context -> {
                            // sync inventory
                            InventoryListener.inventorySyncStep = POWDER;
                            commandService.sendCommand("inv");

                            String targetName = getString(context, "player");

                            utilService.delayedAction(() -> {
                                // handle
                                List<String> commandQueue = new ArrayList<>();

                                storage.getInventory().entrySet().stream()
                                        .filter(inventoryItemMapEntry -> inventoryItemMapEntry.getKey().isDrugBankItem())
                                        .forEach(inventoryItemMapEntry -> {
                                            InventoryItem inventoryItem = inventoryItemMapEntry.getKey();

                                            inventoryItemMapEntry.getValue().entrySet().stream()
                                                    .filter(purityIntegerEntry -> purityIntegerEntry.getValue() > 0)
                                                    .forEach(purityIntegerEntry -> {
                                                        int purityNumber = purityIntegerEntry.getKey().ordinal();
                                                        Integer amount = purityIntegerEntry.getValue();
                                                        commandQueue.add("selldrug " + targetName + " " + inventoryItem.getDisplayName() + " " + purityNumber + " " + amount + " " + 0);
                                                    });
                                        });

                                commandService.sendCommandsWithAwaitingResponse(commandQueue);
                            }, 2500);

                            return 1;
                        }));
    }
}

package de.rettichlp.ucutils.listener.impl;

import de.rettichlp.ucutils.common.models.InventoryItem;
import de.rettichlp.ucutils.common.models.Purity;
import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IScreenOpenListener;
import lombok.NonNull;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.models.InventoryItem.ANTIBIOTICS;
import static de.rettichlp.ucutils.common.models.InventoryItem.COUGH_SYRUP;
import static de.rettichlp.ucutils.common.models.InventoryItem.CRYSTALS;
import static de.rettichlp.ucutils.common.models.InventoryItem.GRAB_BAG;
import static de.rettichlp.ucutils.common.models.InventoryItem.GUN_POWDER;
import static de.rettichlp.ucutils.common.models.InventoryItem.HERBS;
import static de.rettichlp.ucutils.common.models.InventoryItem.IRON;
import static de.rettichlp.ucutils.common.models.InventoryItem.KEVLAR_FIBERS;
import static de.rettichlp.ucutils.common.models.InventoryItem.MASK;
import static de.rettichlp.ucutils.common.models.InventoryItem.MEDICINAL_HERBS;
import static de.rettichlp.ucutils.common.models.InventoryItem.PAINKILLERS;
import static de.rettichlp.ucutils.common.models.InventoryItem.fromDisplayName;
import static de.rettichlp.ucutils.common.models.Purity.BEST;
import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;
import static net.minecraft.component.DataComponentTypes.LORE;
import static net.minecraft.screen.slot.SlotActionType.PICKUP;

@UCUtilsListener
public class InventoryListener implements IScreenOpenListener {

    public static InventoryItem inventorySyncStep = null;

    @Override
    public void onScreenOpen(Screen screen, int scaledWidth, int scaledHeight) {
        if (!(screen instanceof HandledScreen<?> handledScreen)) {
            return;
        }

        String title = handledScreen.getTitle().getString();
        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;

        if (inventorySyncStep == null || interactionManager == null) {
            return;
        }

        ScreenHandler screenHandler = handledScreen.getScreenHandler();

        if (screenHandler instanceof GenericContainerScreenHandler genericContainerScreenHandler) {
            if (!title.equals("Inventar")) {
                return;
            }

            utilService.delayedAction(() -> {
                switch (inventorySyncStep) {
                    case POWDER -> {
                        interactionManager.clickSlot(genericContainerScreenHandler.syncId, 0, 0, PICKUP, player);
                        return;
                    }
                    case HERBS -> {
                        interactionManager.clickSlot(genericContainerScreenHandler.syncId, 1, 0, PICKUP, player);
                        return;
                    }
                    case CRYSTALS -> {
                        interactionManager.clickSlot(genericContainerScreenHandler.syncId, 3, 0, PICKUP, player);
                        return;
                    }
                    default -> {
                    }
                }

                ItemStack medicinalHerbsItemStack = genericContainerScreenHandler.slots.get(2).getStack();
                int medicinalHerbsAmount = getAmount(medicinalHerbsItemStack, 0);
                storage.getInventory().put(MEDICINAL_HERBS, Map.of(BEST, medicinalHerbsAmount));

                ItemStack surpriseBagItemStack = genericContainerScreenHandler.slots.get(4).getStack();
                int surpriseBagAmount = getAmount(surpriseBagItemStack, 0);
                storage.getInventory().put(GRAB_BAG, Map.of(BEST, surpriseBagAmount));

                ItemStack coughSyrupItemStack = genericContainerScreenHandler.slots.get(9).getStack();
                int coughSyrupAmount = getAmount(coughSyrupItemStack, 0);
                storage.getInventory().put(COUGH_SYRUP, Map.of(BEST, coughSyrupAmount));

                ItemStack painkillersItemStack = genericContainerScreenHandler.slots.get(10).getStack();
                int painkillersAmount = getAmount(painkillersItemStack, 0);
                storage.getInventory().put(PAINKILLERS, Map.of(BEST, painkillersAmount));

                ItemStack antibioticsItemStack = genericContainerScreenHandler.slots.get(11).getStack();
                int antibioticsAmount = getAmount(antibioticsItemStack, 0);
                storage.getInventory().put(ANTIBIOTICS, Map.of(BEST, antibioticsAmount));

                ItemStack maskItemStack = genericContainerScreenHandler.slots.get(18).getStack();
                int maskAmount = getAmount(maskItemStack, 0);
                storage.getInventory().put(MASK, Map.of(BEST, maskAmount));

                ItemStack ironItemStack = genericContainerScreenHandler.slots.get(19).getStack();
                int ironAmount = getAmount(ironItemStack, 0);
                storage.getInventory().put(IRON, Map.of(BEST, ironAmount));

                ItemStack gunpowderItemStack = genericContainerScreenHandler.slots.get(20).getStack();
                int gunpowderAmount = getAmount(gunpowderItemStack, 0);
                storage.getInventory().put(GUN_POWDER, Map.of(BEST, gunpowderAmount));

                ItemStack kevlarFibersItemStack = genericContainerScreenHandler.slots.get(21).getStack();
                int kevlarFibersAmount = getAmount(kevlarFibersItemStack, 0);
                storage.getInventory().put(KEVLAR_FIBERS, Map.of(BEST, kevlarFibersAmount));

                inventorySyncStep = null;
                player.closeScreen();
            }, 250);

            return;
        }

        if (screenHandler instanceof HopperScreenHandler hopperScreenHandler) {
            utilService.delayedAction(() -> fromDisplayName(title).ifPresent(inventoryItem -> {
                Map<Purity, Integer> purityAmounts = new HashMap<>();

                DefaultedList<Slot> slots = hopperScreenHandler.slots;
                for (int i = 0; i < 4; i++) {
                    ItemStack itemStack = slots.get(i).getStack();
                    int amount = getAmount(itemStack, 2);
                    purityAmounts.put(Purity.values()[i], amount);
                }

                storage.getInventory().put(inventoryItem, purityAmounts);
                switch (inventorySyncStep) {
                    case POWDER -> {
                        inventorySyncStep = HERBS;
                        interactionManager.clickSlot(hopperScreenHandler.syncId, 4, 0, PICKUP, player);
                    }
                    case HERBS -> {
                        inventorySyncStep = CRYSTALS;
                        interactionManager.clickSlot(hopperScreenHandler.syncId, 4, 0, PICKUP, player);
                    }
                    case CRYSTALS -> {
                        inventorySyncStep = MEDICINAL_HERBS;
                        interactionManager.clickSlot(hopperScreenHandler.syncId, 4, 0, PICKUP, player);
                    }
                    default -> inventorySyncStep = null;
                }
            }), 250);
        }
    }

    private int getAmount(@NonNull ItemStack itemStack, int loreLineIndex) {
        LoreComponent loreComponent = itemStack.get(LORE);

        if (loreComponent == null) {
            return 0;
        }

        String amountString = loreComponent.lines().get(loreLineIndex).getString();
        Matcher matcher = compile("\\d+").matcher(amountString);
        return matcher.find() ? parseInt(matcher.group()) : 0;
    }
}

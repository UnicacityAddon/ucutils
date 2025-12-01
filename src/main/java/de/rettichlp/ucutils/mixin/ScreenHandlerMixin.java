package de.rettichlp.ucutils.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static java.util.Objects.isNull;
import static net.minecraft.screen.slot.SlotActionType.PICKUP;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {

    @Unique
    private static final long A_BUY_DELAY = 150;

    @Unique
    private boolean isABuyProcessing = false;

    @Inject(
            method = "onSlotClick",
            at = @At("HEAD")
    )
    private void onSlotClickMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (!MinecraftClient.getInstance().world.isClient()) {
            return;
        }

        ScreenHandler handler = (ScreenHandler) (Object) this;
        MinecraftClient client = MinecraftClient.getInstance();

        if (!storage.isABuyEnabled() || slotIndex < 0 || slotIndex >= handler.slots.size() || actionType != PICKUP || this.isABuyProcessing || !isWhitelistedInventory(client.currentScreen)) {
            return;
        }

        int aBuyAmount = storage.getABuyAmount();
        Slot clickedSlot = handler.slots.get(slotIndex);
        ItemStack itemStack = clickedSlot.getStack();

        if (aBuyAmount <= 1 || itemStack.isEmpty()) {
            return;
        }

        Int2ObjectMap<ItemStack> stackMap = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < handler.slots.size(); i++) {
            stackMap.put(i, handler.getSlot(i).getStack().copy());
        }

        this.isABuyProcessing = true;

        for (int i = 1; i < aBuyAmount; i++) {
            utilService.delayedAction(() -> {
                // check if the same inventory is still open
                Screen currentScreen = client.currentScreen;
                if (isNull(currentScreen)) {
                    return;
                }

                assert MinecraftClient.getInstance().interactionManager != null;
                MinecraftClient.getInstance().interactionManager.clickSlot(
                        handler.syncId,
                        clickedSlot.id,
                        0,
                        PICKUP,
                        player);
            }, A_BUY_DELAY * i);
        }

        utilService.delayedAction(() -> this.isABuyProcessing = false, A_BUY_DELAY * (aBuyAmount + 1));
    }

    @Unique
    private boolean isWhitelistedInventory(@Nullable Screen screen) {
        if (isNull(screen)) {
            return false;
        }

        String title = screen.getTitle().getString();
        return utilService.getWhitelistedInventoryTitles().stream().anyMatch(title::contains);
    }
}

package de.rettichlp.ucutils.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.component.ItemLore;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;
import static net.minecraft.core.component.DataComponents.LORE;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.world.inventory.ContainerInput.PICKUP;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {

    @Shadow
    @Final
    protected int imageWidth;

    @Shadow
    @Final
    protected int imageHeight;

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void ucutils$extractRenderStateTail(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;

        if (gameMode == null) {
            return;
        }

        String title = getTitle().getString();

        switch (getTitle().getString()) {
            case "ʟᴀɢᴇʀ" -> {
                int ingredient1StoredAmount = getStoredAmount(this, 11);
                int ingredient2StoredAmount = getStoredAmount(this, 13);
                int ingredient3StoredAmount = getStoredAmount(this, 15);

                int x = (this.width - this.imageWidth) / 2;
                int y = (this.height - this.imageHeight) / 2;
                int buttonX = x + this.imageWidth + 2;

                // render button right to the inventory
                Button button = new Button.Builder(literal("➤"), _ -> commandService.sendCommand("f " + ingredient1StoredAmount + "x Wirkstoff | " + ingredient2StoredAmount + "x Trägerstoff | " + ingredient3StoredAmount + "x Zusatzstoff"))
                        .bounds(buttonX, y, 20, 20)
                        .build();

                if (ingredient1StoredAmount != 0 && ingredient2StoredAmount != 0 && ingredient3StoredAmount != 0) {
                    button.extractRenderState(graphics, mouseX, mouseY, a);
                    addRenderableWidget(button);
                }
            }
            case "ᴄᴀʀᴄᴏɴᴛʀᴏʟ" -> {
                if (configuration.getOptions().car().fastLock() && !storage.isPremium()) {
                    gameMode.handleContainerInput(getMenu().containerId, 0, 0, PICKUP, player);
                }
            }
            default -> {
                if (commandService.isSuperUser()) {
                    LOGGER.info("Screen opened: {}", title);
                }
            }
        }
    }

    @Unique
    private int getStoredAmount(@NonNull MenuAccess<T> containerScreen, int slotId) {
        Slot slot = containerScreen.getMenu().getSlot(slotId);
        ItemLore itemLore = slot.getItem().get(LORE);

        if (itemLore == null) {
            return 0;
        }

        String amountString = itemLore.lines().get(1).getString();
        Matcher matcher = compile("\\d+").matcher(amountString);
        return matcher.find() ? parseInt(matcher.group()) : 0;
    }
}

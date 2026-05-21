package de.rettichlp.ucutils.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;
import static net.minecraft.component.DataComponentTypes.LORE;
import static net.minecraft.text.Text.literal;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {

    @Shadow
    protected int backgroundWidth;

    @Shadow
    protected int backgroundHeight;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void ucutils$renderTail(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        HandledScreen<?> self = (HandledScreen<?>) (Object) this;
        ScreenHandler screenHandler = self.getScreenHandler();

        String title = self.getTitle().getString();

        switch (screenHandler) {
            case GenericContainerScreenHandler genericContainerScreenHandler -> {
                switch (title) {
                    case "ʟᴀɢᴇʀ" -> {
                        int ingredient1StoredAmount = getStoredAmount(genericContainerScreenHandler, 11);
                        int ingredient2StoredAmount = getStoredAmount(genericContainerScreenHandler, 13);
                        int ingredient3StoredAmount = getStoredAmount(genericContainerScreenHandler, 15);

                        int x = (this.width - this.backgroundWidth) / 2;
                        int y = (this.height - this.backgroundHeight) / 2;
                        int buttonX = x + this.backgroundWidth + 2;

                        // render button right to the inventory
                        ButtonWidget buttonWidget = new ButtonWidget.Builder(literal("➤"), button -> commandService.sendCommand("f " + ingredient1StoredAmount + "x Wirkstoff | " + ingredient2StoredAmount + "x Trägerstoff | " + ingredient3StoredAmount + "x Zusatzstoff"))
                                .dimensions(buttonX, y, 20, 20)
                                .build();

                        if (ingredient1StoredAmount != 0 && ingredient2StoredAmount != 0 && ingredient3StoredAmount != 0) {
                            buttonWidget.render(context, mouseX, mouseY, deltaTicks);
                            addDrawableChild(buttonWidget);
                        }
                    }
                }
            }
            case null, default -> {
            }
        }
    }

    @Unique
    private int getStoredAmount(@NotNull ScreenHandler screenHandler, int slotId) {
        Slot slot = screenHandler.slots.get(slotId);
        LoreComponent loreComponent = slot.getStack().get(LORE);

        if (loreComponent == null) {
            return 0;
        }

        String amountString = loreComponent.lines().get(1).getString();
        Matcher matcher = compile("\\d+").matcher(amountString);
        return matcher.find() ? parseInt(matcher.group()) : 0;
    }
}

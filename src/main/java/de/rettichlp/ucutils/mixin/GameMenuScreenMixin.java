package de.rettichlp.ucutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.rettichlp.ucutils.common.gui.screens.options.MainOptionsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.network.chat.Component.literal;

@Mixin(PauseScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    protected GameMenuScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "createPauseMenu",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;ILnet/minecraft/client/gui/layouts/LayoutSettings;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
                    ordinal = 0))
    private void ucutils$initWidgetsInvoke(CallbackInfo ci,
                                           @Local(name = "gridLayout") GridLayout gridLayout,
                                           @Local(name = "helper") GridLayout.@NotNull RowHelper helper) {
        Button buttonWidget = Button.builder(literal("UCUtils Settings"), button -> this.minecraft.setScreen(new MainOptionsScreen()))
                .width(204)
                .build();

        // the first item of the adder has a top margin of 50, so we add a negative top margin to compensate (46 for a small gap of 4)
        helper.addChild(buttonWidget, 2, gridLayout.newCellSettings().paddingTop(50).paddingBottom(-46));
    }
}

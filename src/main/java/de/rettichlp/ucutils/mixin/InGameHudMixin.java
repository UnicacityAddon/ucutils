package de.rettichlp.ucutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.ucutils.UCUtils.MOD_ID;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.storage;
import static net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Unique
    private static final Identifier THIRST_EMPTY_TEXTURE = Identifier.of(MOD_ID, "textures/hud/thirst_empty.png");

    @Unique
    private static final Identifier THIRST_HALF_TEXTURE = Identifier.of(MOD_ID, "textures/hud/thirst_half.png");

    @Unique
    private static final Identifier THIRST_FULL_TEXTURE = Identifier.of(MOD_ID, "textures/hud/thirst_full.png");

    @Inject(
            method = "renderStatusBars",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V",
                    shift = AFTER
            )
    )
    private void ucutils$renderStatusBarsInvoke(DrawContext context,
                                                CallbackInfo ci,
                                                @Local(ordinal = 3) int m,
                                                @Local(ordinal = 8) int r) {
        if (!configuration.getOptions().showThirst() || storage.getThirst() == -1) {
            return;
        }

        Profilers.get().swap("thirst");
        renderThirst(context, r, m);
    }

    @Unique
    private void renderThirst(DrawContext context, int y, int rightX) {
        double thirst = storage.getThirst();

        for (int i = 0; i < 10; ++i) {
            int x = rightX - (i * 8) - 10;

            Identifier texture = THIRST_EMPTY_TEXTURE;

            double currentThirst = thirst - (i * 2.0);
            if (currentThirst >= 2.0) {
                texture = THIRST_FULL_TEXTURE;
            } else if (currentThirst >= 1.0) {
                texture = THIRST_HALF_TEXTURE;
            }

            context.drawTexture(GUI_TEXTURED, texture, x, y, 0, 0, 9, 9, 9, 9);
        }
    }
}

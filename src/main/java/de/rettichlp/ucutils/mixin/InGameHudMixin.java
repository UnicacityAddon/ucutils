package de.rettichlp.ucutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.ucutils.UCUtils.MOD_ID;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.lang.Math.clamp;
import static java.lang.Math.round;
import static net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.registry.tag.FluidTags.WATER;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Unique
    private static final Identifier HYDRATION_EMPTY_TEXTURE = Identifier.of(MOD_ID, "textures/hud/hydration_empty.png");

    @Unique
    private static final Identifier HYDRATION_HALF_TEXTURE = Identifier.of(MOD_ID, "textures/hud/hydration_half.png");

    @Unique
    private static final Identifier HYDRATION_FULL_TEXTURE = Identifier.of(MOD_ID, "textures/hud/hydration_full.png");

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
        if (!storage.isUnicaCity()) {
            return;
        }

        if (!configuration.getOptions().showHydration() || storage.getHydration() == -1) {
            return;
        }

        Profilers.get().swap("hydration");
        renderHydration(context, r, m);
        Profilers.get().pop();
    }

    @Unique
    private void renderHydration(DrawContext context, int top, int left) {
        double maxHydrated = 20;
        long round = round(storage.getHydration());
        int hydration = (int) clamp(round, 0, maxHydrated);

        if (player.isSubmergedIn(WATER) || player.getAir() < player.getMaxAir()) {
            top -= 10;
        }

        if (player.getVehicle() instanceof LivingEntity) {
            top -= 10;
        }

        for (int n = 0; n < 10; n++) {
            int o = left - 9 - n * 8;

            Identifier texture = THIRST_EMPTY_TEXTURE;

            int hydrationLeft = hydration - (n * 2);
            if (hydrationLeft >= 2.0) {
                texture = THIRST_FULL_TEXTURE;
            } else if (hydrationLeft >= 1.0) {
                texture = THIRST_HALF_TEXTURE;
            }

            context.drawTexture(GUI_TEXTURED, texture, o, top, 0, 0, 9, 9, 9, 9);
        }
    }
}

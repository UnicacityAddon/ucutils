package de.rettichlp.ucutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.ucutils.UCUtils.MOD_ID;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.nameTagService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.lang.Math.ceil;
import static java.lang.Math.clamp;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;
import static net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.item.Items.GOLDEN_HOE;
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

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "renderCrosshair",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V",
                    ordinal = 0,
                    shift = AFTER))
    private void ucutils$renderCrosshairInvoke(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!storage.isUnicaCity()) {
            return;
        }

        if (!nameTagService.isADuty(player.getStringifiedName())) {
            return;
        }

        if (!player.getMainHandStack().isOf(GOLDEN_HOE)) {
            return;
        }

        Profilers.get().swap("aduty_warning");
        String text = "ᴀᴅᴜᴛʏ";

        int width = getTextRenderer().getWidth(text);
        int x = context.getScaledWindowWidth() / 2 - width / 2;
        int y = context.getScaledWindowHeight() / 2 - 20;

        context.drawText(getTextRenderer(), text, x + 1, y, -16777216, false);
        context.drawText(getTextRenderer(), text, x - 1, y, -16777216, false);
        context.drawText(getTextRenderer(), text, x, y + 1, -16777216, false);
        context.drawText(getTextRenderer(), text, x, y - 1, -16777216, false);
        context.drawText(getTextRenderer(), text, x, y, (currentTimeMillis() / 500 % 2 == 0 ? RED : WHITE).getRGB(), false);

        Profilers.get().pop();
    }

    @Inject(method = "renderStatusBars",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V",
                    shift = AFTER))
    private void ucutils$renderStatusBarsInvoke(DrawContext context,
                                                CallbackInfo ci,
                                                @Local(ordinal = 3) int m,
                                                @Local(ordinal = 8) int r) {
        if (!storage.isUnicaCity()) {
            return;
        }

        if (!configuration.getOptions().showHydration() || storage.getHydration() < 0) {
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

        if (player.getVehicle() instanceof LivingEntity livingEntity) {
            int hearthRows = (int) ceil(livingEntity.getHealth() / 20.0);
            top -= hearthRows * 10;
        }

        for (int n = 0; n < 10; n++) {
            int o = left - 9 - n * 8;

            // always render empty hydration
            context.drawTexture(GUI_TEXTURED, HYDRATION_EMPTY_TEXTURE, o, top, 0, 0, 9, 9, 9, 9);

            // render texture depending on hydration
            int hydrationLeft = hydration - (n * 2);
            if (hydrationLeft >= 2.0) {
                context.drawTexture(GUI_TEXTURED, HYDRATION_FULL_TEXTURE, o, top, 0, 0, 9, 9, 9, 9);
            } else if (hydrationLeft >= 1.0) {
                context.drawTexture(GUI_TEXTURED, HYDRATION_HALF_TEXTURE, o, top, 0, 0, 9, 9, 9, 9);
            }
        }
    }
}

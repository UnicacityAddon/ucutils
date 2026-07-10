package de.rettichlp.ucutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.nio.file.Path;

import static com.mojang.blaze3d.platform.NativeImage.read;
import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static de.rettichlp.ucutils.UCUtils.MOD_ID;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.lang.Math.ceil;
import static java.lang.Math.clamp;
import static java.lang.Math.round;
import static java.nio.file.Files.newInputStream;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Unique
    private static final Identifier HYDRATION_EMPTY_TEXTURE = fromNamespaceAndPath(MOD_ID, "textures/hud/hydration_empty.png");

    @Unique
    private static final Identifier HYDRATION_HALF_TEXTURE = fromNamespaceAndPath(MOD_ID, "textures/hud/hydration_half.png");

    @Unique
    private static final Identifier HYDRATION_FULL_TEXTURE = fromNamespaceAndPath(MOD_ID, "textures/hud/hydration_full.png");

    @Unique
    private static final Identifier CAPTCHA_IDENTIFIER = fromNamespaceAndPath("ucutils", "captcha");

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "extractChat", at = @At("TAIL"))
    private void ucutils$extractChatTail(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (storage.getCaptchaMap() == null) {
            return;
        }

        NativeImage nativeImage;
        try (InputStream is = newInputStream(Path.of("screenshots/ucutils/captcha.png"))) {
            nativeImage = read(is);
        } catch (Exception e) {
            LOGGER.error("Failed to read captcha image", e);
            return;
        }

        AbstractTexture texture = new DynamicTexture(() -> "captcha", nativeImage);
        this.minecraft.getTextureManager().register(CAPTCHA_IDENTIFIER, texture);

        int side = graphics.guiWidth() / 4;
        int x = graphics.guiWidth() / 2 - side / 2;
        int y = graphics.guiHeight() / 4 - side / 2;

        graphics.blit(GUI_TEXTURED, CAPTCHA_IDENTIFIER, x, y, 0, 0, side, side, side, side);
    }

    @Inject(method = "extractPlayerHealth",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Gui;extractHearts(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V",
                    shift = AFTER))
    private void ucutils$extractPlayerHealthInvoke(GuiGraphicsExtractor graphics,
                                                   CallbackInfo ci,
                                                   @Local(name = "xRight") int xRight,
                                                   @Local(name = "yLineAir") int yLineAir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        if (!configuration.getOptions().showHydration() || storage.getHydration() < 0) {
            return;
        }

        Profiler.get().popPush("hydration");
        renderHydration(graphics, yLineAir, xRight);
        Profiler.get().pop();
    }

    @Unique
    private void renderHydration(GuiGraphicsExtractor context, int yLineAir, int xRight) {
        double maxHydrated = 20;
        long round = round(storage.getHydration());
        int hydration = (int) clamp(round, 0, maxHydrated);

        if (player.isUnderWater() || player.getAirSupply() < player.getMaxAirSupply()) {
            yLineAir -= 10;
        }

        if (player.getVehicle() instanceof LivingEntity livingEntity) {
            int hearthRows = (int) ceil(livingEntity.getHealth() / 20.0);
            yLineAir -= hearthRows * 10;
        }

        for (int n = 0; n < 10; n++) {
            int o = xRight - 9 - n * 8;

            // always render empty hydration
            context.blit(GUI_TEXTURED, HYDRATION_EMPTY_TEXTURE, o, yLineAir, 0, 0, 9, 9, 9, 9);

            // render texture depending on hydration
            int hydrationLeft = hydration - (n * 2);
            if (hydrationLeft >= 2.0) {
                context.blit(GUI_TEXTURED, HYDRATION_FULL_TEXTURE, o, yLineAir, 0, 0, 9, 9, 9, 9);
            } else if (hydrationLeft >= 1.0) {
                context.blit(GUI_TEXTURED, HYDRATION_HALF_TEXTURE, o, yLineAir, 0, 0, 9, 9, 9, 9);
            }
        }
    }
}

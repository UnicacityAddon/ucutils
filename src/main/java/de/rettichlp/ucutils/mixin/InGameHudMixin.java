package de.rettichlp.ucutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.rettichlp.ucutils.common.gui.widgets.CountdownWidget;
import de.rettichlp.ucutils.common.gui.widgets.NotificationWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsProgressTextWidget;
import de.rettichlp.ucutils.common.models.Countdown;
import de.rettichlp.ucutils.common.services.NotificationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profilers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static de.rettichlp.ucutils.UCUtils.MOD_ID;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.notificationService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.renderService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget.Alignment.RIGHT;
import static java.lang.Math.ceil;
import static java.lang.Math.clamp;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED;
import static net.minecraft.registry.tag.FluidTags.WATER;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.BOLD;
import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.RED;
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
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "renderBossBarHud", at = @At("HEAD"))
    private void ucutils$renderBossBarHudHead(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        renderNotifications(context);
        renderWidgets(context);

        if (storage.getBombPlantTimestamp() == null) {
            return;
        }

        long elapsedTimeInMillis = between(storage.getBombPlantTimestamp(), now()).toMillis();
        long minutes = MILLISECONDS.toMinutes(elapsedTimeInMillis);
        long seconds = MILLISECONDS.toSeconds(elapsedTimeInMillis) % 60;

        if (minutes >= 20) {
            storage.setBombLocation(null);
            storage.setBombPlantTimestamp(null);
        }

        Text timerText = empty()
                .append(literal("Bombe").formatted(RED))
                .append(literal(":").formatted(GRAY)).append(" ")
                .append(literal(ofNullable(storage.getBombLocation()).orElse("Unbekannt")).formatted(GOLD)).append(" ")
                .append(literal("|").formatted(GRAY)).append(" ")
                .append(literal(format("%02d:%02d", minutes, seconds)).formatted(RED, BOLD));

        int textWidth = getTextRenderer().getWidth(timerText);
        int x = (this.client.getWindow().getScaledWidth() - textWidth) / 2;
        int y = 15;

        context.drawTextWithShadow(getTextRenderer(), timerText, x, y, 0xFFFFFFFF);
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
    private void renderNotifications(DrawContext drawContext) {
        ArrayList<AbstractUCUtilsProgressTextWidget<?>> widgets = new ArrayList<>();
        widgets.addAll(getCountdownWidgets());
        widgets.addAll(getNotificationWidgets());

        for (int i = 0; i < widgets.size(); i++) {
            AbstractUCUtilsProgressTextWidget<?> abstractUCUtilsProgressTextWidget = widgets.get(i);
            int x = MinecraftClient.getInstance().getWindow().getScaledWidth() - abstractUCUtilsProgressTextWidget.getWidth() - 4;
            int y = 19 * i + 4;
            abstractUCUtilsProgressTextWidget.draw(drawContext, x, y, RIGHT);
        }
    }

    @Unique
    private @NotNull @Unmodifiable List<CountdownWidget> getCountdownWidgets() {
        return storage.getCountdowns().stream()
                .filter(Countdown::isActive)
                .map(Countdown::toWidget)
                .toList();
    }

    @Unique
    private @NotNull @Unmodifiable List<NotificationWidget> getNotificationWidgets() {
        return notificationService.getActiveNotifications().stream()
                .map(NotificationService.Notification::toWidget)
                .toList();
    }

    @Unique
    private void renderWidgets(DrawContext drawContext) {
        renderService.getWidgets().forEach(ucUtilsWidgetInstance -> ucUtilsWidgetInstance.draw(drawContext));
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

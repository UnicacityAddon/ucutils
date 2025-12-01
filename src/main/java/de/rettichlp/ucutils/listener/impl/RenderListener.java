package de.rettichlp.ucutils.listener.impl;

import de.rettichlp.ucutils.common.gui.widgets.CountdownWidget;
import de.rettichlp.ucutils.common.gui.widgets.NotificationWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.AbstractPKUtilsProgressTextWidget;
import de.rettichlp.ucutils.common.models.Countdown;
import de.rettichlp.ucutils.common.registry.PKUtilsListener;
import de.rettichlp.ucutils.common.services.NotificationService;
import de.rettichlp.ucutils.listener.IHudRenderListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

import static de.rettichlp.ucutils.PKUtils.notificationService;
import static de.rettichlp.ucutils.PKUtils.renderService;
import static de.rettichlp.ucutils.PKUtils.storage;
import static de.rettichlp.ucutils.common.gui.widgets.base.AbstractPKUtilsWidget.Alignment.RIGHT;

@PKUtilsListener
public class RenderListener implements IHudRenderListener {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        renderNotifications(drawContext);
        renderWidgets(drawContext);
    }

    private void renderNotifications(DrawContext drawContext) {
        ArrayList<AbstractPKUtilsProgressTextWidget<?>> widgets = new ArrayList<>();
        widgets.addAll(getCountdownWidgets());
        widgets.addAll(getNotificationWidgets());

        for (int i = 0; i < widgets.size(); i++) {
            AbstractPKUtilsProgressTextWidget<?> abstractPKUtilsProgressTextWidget = widgets.get(i);
            int x = MinecraftClient.getInstance().getWindow().getScaledWidth() - abstractPKUtilsProgressTextWidget.getWidth() - 4;
            int y = 19 * i + 4;
            abstractPKUtilsProgressTextWidget.draw(drawContext, x, y, RIGHT);
        }
    }

    private @NotNull @Unmodifiable List<CountdownWidget> getCountdownWidgets() {
        return storage.getCountdowns().stream()
                .filter(Countdown::isActive)
                .map(Countdown::toWidget)
                .toList();
    }

    private @NotNull @Unmodifiable List<NotificationWidget> getNotificationWidgets() {
        return notificationService.getActiveNotifications().stream()
                .map(NotificationService.Notification::toWidget)
                .toList();
    }

    private void renderWidgets(DrawContext drawContext) {
        renderService.getWidgets().forEach(pkUtilsWidgetInstance -> pkUtilsWidgetInstance.draw(drawContext));
    }
}

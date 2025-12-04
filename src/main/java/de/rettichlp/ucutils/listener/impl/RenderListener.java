package de.rettichlp.ucutils.listener.impl;

import de.rettichlp.ucutils.common.gui.widgets.CountdownWidget;
import de.rettichlp.ucutils.common.gui.widgets.NotificationWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsProgressTextWidget;
import de.rettichlp.ucutils.common.models.Countdown;
import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.common.services.NotificationService;
import de.rettichlp.ucutils.listener.IHudRenderListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

import static de.rettichlp.ucutils.UCUtils.notificationService;
import static de.rettichlp.ucutils.UCUtils.renderService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget.Alignment.RIGHT;

@UCUtilsListener
public class RenderListener implements IHudRenderListener {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        renderNotifications(drawContext);
        renderWidgets(drawContext);
    }

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
        renderService.getWidgets().forEach(ucUtilsWidgetInstance -> ucUtilsWidgetInstance.draw(drawContext));
    }
}

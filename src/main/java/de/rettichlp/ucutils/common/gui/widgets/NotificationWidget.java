package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsProgressTextWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import lombok.RequiredArgsConstructor;
import net.minecraft.text.Text;

import java.awt.Color;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static net.minecraft.text.Text.empty;

@RequiredArgsConstructor
@UCUtilsWidget(registryName = "notification")
public class NotificationWidget extends AbstractUCUtilsProgressTextWidget<NotificationWidget.Configuration> {

    private final Text text;
    private final Color borderColor;
    private final LocalDateTime creationTime;
    private final long durationInMillis;

    @Override
    public Text text() {
        return this.text;
    }

    @Override
    public double progress() {
        return calculateProgress(this.creationTime, this.durationInMillis);
    }

    @Override
    public Color getBorderColor() {
        return this.borderColor;
    }

    @Override
    public Text getDisplayName() {
        return empty();
    }

    @Override
    public Text getTooltip() {
        return empty();
    }

    @Override
    public boolean isVisible() {
        return this.creationTime.plus(this.durationInMillis, MILLIS).isAfter(now());
    }

    public static class Configuration extends UCUtilsWidgetConfiguration {}
}

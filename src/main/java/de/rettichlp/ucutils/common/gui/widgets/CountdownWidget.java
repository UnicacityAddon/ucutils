package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsProgressTextWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static net.minecraft.network.chat.Component.empty;

@RequiredArgsConstructor
@UCUtilsWidget(registryName = "countdown")
public class CountdownWidget extends AbstractUCUtilsProgressTextWidget<CountdownWidget.Configuration> {

    private final Component text;
    private final LocalDateTime creationTime;
    private final long durationInMillis;

    @Override
    public Component text() {
        return this.text;
    }

    @Override
    public double progress() {
        return calculateProgress(this.creationTime, this.durationInMillis);
    }

    @Override
    public Component getDisplayName() {
        return empty();
    }

    @Override
    public Component getTooltip() {
        return empty();
    }

    @Override
    public boolean isVisible() {
        return this.creationTime.plus(this.durationInMillis, MILLIS).isAfter(now());
    }

    public static class Configuration extends UCUtilsWidgetConfiguration {}
}

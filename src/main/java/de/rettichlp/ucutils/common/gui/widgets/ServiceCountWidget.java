package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.ucutils.common.gui.screens.options.WidgetOptionsPositionScreen;
import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsTextWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.awt.Color;

import static de.rettichlp.ucutils.UCUtils.storage;
import static java.awt.Color.RED;
import static java.lang.String.valueOf;
import static net.minecraft.network.chat.Component.translatable;

@UCUtilsWidget(registryName = "service_count", defaultX = 4.0, defaultY = 42.0)
public class ServiceCountWidget extends AbstractUCUtilsTextWidget<ServiceCountWidget.Configuration> {

    private static final Component WIDGETS_SERVICE_COUNT_OPTIONS_NAME = translatable("ucutils.options.widgets.service_count.options.name");
    private static final Component WIDGETS_SERVICE_COUNT_OPTIONS_TOOLTIP = translatable("ucutils.options.widgets.service_count.options.tooltip");

    @Override
    public Component text() {
        return keyValue("Services", valueOf(storage.getActiveServices()));
    }

    @Override
    public Color getBorderColor() {
        return RED;
    }

    @Override
    public Component getDisplayName() {
        return WIDGETS_SERVICE_COUNT_OPTIONS_NAME;
    }

    @Override
    public Component getTooltip() {
        return WIDGETS_SERVICE_COUNT_OPTIONS_TOOLTIP;
    }

    @Override
    public boolean isVisible() {
        // visible if in the position options screen to allow positioning
        return storage.getActiveServices() > 0 || Minecraft.getInstance().screen instanceof WidgetOptionsPositionScreen;
    }

    public static class Configuration extends UCUtilsWidgetConfiguration {}
}

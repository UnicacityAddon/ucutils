package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCTextWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.WidgetConfiguration;
import de.rettichlp.ucutils.common.gui.screens.options.WidgetOptionsPositionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.services.RenderService.keyValue;
import static java.awt.Color.RED;
import static java.lang.String.valueOf;
import static net.minecraft.network.chat.Component.translatable;

public class ServiceCountWidget extends AbstractTRCTextWidget<ServiceCountWidget.Configuration> {

    private static final Component WIDGETS_SERVICE_COUNT_OPTIONS_NAME = translatable("ucutils.options.widgets.service_count.options.name");
    private static final Component WIDGETS_SERVICE_COUNT_OPTIONS_TOOLTIP = translatable("ucutils.options.widgets.service_count.options.tooltip");

    @Override
    public Component text() {
        return keyValue("Services", valueOf(storage.getActiveServices())).withColor(RED.getRGB());
    }

    @Override
    public @Nullable String getRegistryName() {
        return "service_count";
    }

    @Override
    public Component getLabel() {
        return WIDGETS_SERVICE_COUNT_OPTIONS_NAME;
    }

    @Override
    public Component getTooltip() {
        return WIDGETS_SERVICE_COUNT_OPTIONS_TOOLTIP;
    }

    @Override
    public void addOptions(@NonNull TRCOptionsList optionsList) {}

    @Override
    public boolean isVisible() {
        // visible if in the position options screen to allow positioning
        return storage.getActiveServices() > 0 || Minecraft.getInstance().screen instanceof WidgetOptionsPositionScreen;
    }

    public static class Configuration extends WidgetConfiguration {}
}

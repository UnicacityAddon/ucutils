package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCTextWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.WidgetConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.services.RenderService.keyValue;
import static java.awt.Color.RED;
import static java.lang.String.valueOf;
import static net.minecraft.network.chat.Component.translatable;

public class ServiceCountWidget extends AbstractTRCTextWidget<ServiceCountWidget.Configuration> {

    @Override
    public Component text() {
        return keyValue(translatable("ucutils.options.widgets.service_count.label"), valueOf(storage.getActiveServices())).withColor(RED.getRGB());
    }

    @Override
    public @Nullable String getRegistryName() {
        return "service_count";
    }

    @Override
    public Component getLabel() {
        return translatable("ucutils.options.widgets.service_count.options.name");
    }

    @Override
    public Component getTooltip() {
        return translatable("ucutils.options.widgets.service_count.options.tooltip");
    }

    @Override
    public void addOptions(@NonNull TRCOptionsList optionsList) {}

    @Override
    public boolean isVisible() {
        // visible if in the position options screen to allow positioning
        return super.isVisible() && (storage.getActiveServices() > 0 || isWidgetPositionScreen());
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Configuration extends WidgetConfiguration {}
}

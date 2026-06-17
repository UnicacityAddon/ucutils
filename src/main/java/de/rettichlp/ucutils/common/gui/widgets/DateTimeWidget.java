package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsTextWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import net.minecraft.network.chat.Component;

import static de.rettichlp.ucutils.UCUtils.messageService;
import static java.time.LocalDateTime.now;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

@UCUtilsWidget(registryName = "date_time", defaultX = 4.0, defaultY = 4.0)
public class DateTimeWidget extends AbstractUCUtilsTextWidget<DateTimeWidget.Configuration> {

    @Override
    public Component text() {
        return literal(messageService.dateTimeToFriendlyString(now()));
    }

    @Override
    public Component getDisplayName() {
        return translatable("ucutils.options.widgets.date_time.options.name");
    }

    @Override
    public Component getTooltip() {
        return translatable("ucutils.options.widgets.date_time.options.tooltip");
    }

    public static class Configuration extends UCUtilsWidgetConfiguration {}
}

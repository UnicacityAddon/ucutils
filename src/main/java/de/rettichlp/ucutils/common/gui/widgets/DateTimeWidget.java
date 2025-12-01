package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsTextWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import net.minecraft.text.Text;

import static de.rettichlp.ucutils.UCUtils.messageService;
import static java.time.LocalDateTime.now;
import static net.minecraft.text.Text.of;
import static net.minecraft.text.Text.translatable;

@UCUtilsWidget(registryName = "date_time", defaultX = 4.0, defaultY = 4.0)
public class DateTimeWidget extends AbstractUCUtilsTextWidget<DateTimeWidget.Configuration> {

    @Override
    public Text text() {
        return of(messageService.dateTimeToFriendlyString(now()));
    }

    @Override
    public Text getDisplayName() {
        return translatable("ucutils.options.widgets.date_time.options.name");
    }

    @Override
    public Text getTooltip() {
        return translatable("ucutils.options.widgets.date_time.options.tooltip");
    }

    public static class Configuration extends UCUtilsWidgetConfiguration {}
}

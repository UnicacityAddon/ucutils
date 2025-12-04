package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsTextWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import lombok.AllArgsConstructor;
import net.minecraft.text.Text;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.translatable;

@UCUtilsWidget(registryName = "money", defaultX = 4.0, defaultY = 23.0)
public class MoneyWidget extends AbstractUCUtilsTextWidget<MoneyWidget.Configuration> {

    @Override
    public Text text() {
        return empty()
                .append(keyValue("Geld", configuration.getMoneyCashAmount() + "$")).append(" ")
                .append(keyValue("Bank", configuration.getMoneyBankAmount() + "$"));
    }

    @Override
    public Text getDisplayName() {
        return translatable("ucutils.options.widgets.money.options.name");
    }

    @Override
    public Text getTooltip() {
        return translatable("ucutils.options.widgets.money.options.tooltip");
    }

    @AllArgsConstructor
    public static class Configuration extends UCUtilsWidgetConfiguration {}
}

package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsTextWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import lombok.AllArgsConstructor;
import net.minecraft.network.chat.Component;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static java.awt.Color.RED;
import static java.lang.System.currentTimeMillis;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.translatable;

@UCUtilsWidget(registryName = "money", defaultX = 4.0, defaultY = 23.0)
public class MoneyWidget extends AbstractUCUtilsTextWidget<MoneyWidget.Configuration> {

    @Override
    public Component text() {
        // with over 100.000$ on bank and PayDay within next 5 minutes, animate text
        return configuration.getMinutesSinceLastPayDay() >= 55 && configuration.getMoneyBankAmount() > 100000 && (currentTimeMillis() / 500 % 2 == 0)
                ? empty()
                .append(keyValue("Geld", configuration.getMoneyCashAmount() + "$")).append(" ")
                .append(keyValue("Bank", configuration.getMoneyBankAmount() + "$").copy().withColor(RED.getRGB()))
                : empty()
                .append(keyValue("Geld", configuration.getMoneyCashAmount() + "$")).append(" ")
                .append(keyValue("Bank", configuration.getMoneyBankAmount() + "$"));
    }

    @Override
    public Component getDisplayName() {
        return translatable("ucutils.options.widgets.money.options.name");
    }

    @Override
    public Component getTooltip() {
        return translatable("ucutils.options.widgets.money.options.tooltip");
    }

    @AllArgsConstructor
    public static class Configuration extends UCUtilsWidgetConfiguration {}
}

package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsTextWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.IOptionWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.awt.Color;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.renderService;
import static java.awt.Color.RED;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.horizontal;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.of;
import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Formatting.DARK_GRAY;

@UCUtilsWidget(registryName = "payday", defaultX = 126.0, defaultY = 4.0)
public class PayDayWidget extends AbstractUCUtilsTextWidget<PayDayWidget.Configuration> {

    private static final Text WIDGETS_PAYDAY_OPTIONS_NAME = translatable("ucutils.options.widgets.payday.options.name");
    private static final Text WIDGETS_PAYDAY_OPTIONS_TOOLTIP = translatable("ucutils.options.widgets.payday.options.tooltip");
    private static final Text WIDGETS_PAYDAY_OPTIONS_SALARY_NAME = translatable("ucutils.options.widgets.payday.options.salary.name");
    private static final Text WIDGETS_PAYDAY_OPTIONS_SALARY_TOOLTIP = translatable("ucutils.options.widgets.payday.options.salary.tooltip");
    private static final Text WIDGETS_PAYDAY_OPTIONS_EXPERIENCE_NAME = translatable("ucutils.options.widgets.payday.options.experience.name");
    private static final Text WIDGETS_PAYDAY_OPTIONS_EXPERIENCE_TOOLTIP = translatable("ucutils.options.widgets.payday.options.experience.tooltip");

    @Override
    public Text text() {
        MutableText payDayInfoText = keyValue("PayDay", empty()
                .append(of(valueOf(configuration.getMinutesSinceLastPayDay())))
                .append(of("/").copy().formatted(DARK_GRAY))
                .append(of("60")));

        if (getWidgetConfiguration().isShowSalary()) {
            payDayInfoText.append(" ").append(keyValue("Gehalt", configuration.getPredictedPayDaySalary() + "$"));
        }

        if (getWidgetConfiguration().isShowExperience()) {
            payDayInfoText.append(" ").append(keyValue("Exp", valueOf(configuration.getPredictedPayDayExp())));
        }

        return payDayInfoText;
    }

    @Override
    public Text getDisplayName() {
        return WIDGETS_PAYDAY_OPTIONS_NAME;
    }

    @Override
    public Text getTooltip() {
        return WIDGETS_PAYDAY_OPTIONS_TOOLTIP;
    }

    @Override
    public Color getBackgroundColor() {
        // with over 100.000$ on bank and PayDay within next 5 minutes, animate background
        return configuration.getMinutesSinceLastPayDay() >= 55 && configuration.getMoneyBankAmount() > 100000 && (currentTimeMillis() / 500 % 2 == 0)
                ? RED
                : super.getBackgroundColor();
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Configuration extends UCUtilsWidgetConfiguration implements IOptionWidget {

        private boolean showSalary = true;
        private boolean showExperience = true;

        @Override
        public Widget optionsWidget() {
            DirectionalLayoutWidget directionalLayoutWidget = horizontal().spacing(8);
            renderService.addToggleButton(directionalLayoutWidget, WIDGETS_PAYDAY_OPTIONS_SALARY_NAME, WIDGETS_PAYDAY_OPTIONS_SALARY_TOOLTIP, (options, value) -> this.showSalary = value, options -> this.showSalary, 150);
            renderService.addToggleButton(directionalLayoutWidget, WIDGETS_PAYDAY_OPTIONS_EXPERIENCE_NAME, WIDGETS_PAYDAY_OPTIONS_EXPERIENCE_TOOLTIP, (options, value) -> this.showExperience = value, options -> this.showExperience, 150);
            return directionalLayoutWidget;
        }
    }
}

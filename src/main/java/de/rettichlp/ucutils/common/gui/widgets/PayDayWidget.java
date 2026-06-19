package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsTextWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.IOptionWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.awt.Color;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.renderService;
import static java.awt.Color.RED;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.client.gui.layouts.LinearLayout.horizontal;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

@UCUtilsWidget(registryName = "payday", defaultX = 126.0, defaultY = 4.0)
public class PayDayWidget extends AbstractUCUtilsTextWidget<PayDayWidget.Configuration> {

    private static final Component WIDGETS_PAYDAY_OPTIONS_NAME = translatable("ucutils.options.widgets.payday.options.name");
    private static final Component WIDGETS_PAYDAY_OPTIONS_TOOLTIP = translatable("ucutils.options.widgets.payday.options.tooltip");
    private static final Component WIDGETS_PAYDAY_OPTIONS_SALARY_NAME = translatable("ucutils.options.widgets.payday.options.salary.name");
    private static final Component WIDGETS_PAYDAY_OPTIONS_SALARY_TOOLTIP = translatable("ucutils.options.widgets.payday.options.salary.tooltip");
    private static final Component WIDGETS_PAYDAY_OPTIONS_EXPERIENCE_NAME = translatable("ucutils.options.widgets.payday.options.experience.name");
    private static final Component WIDGETS_PAYDAY_OPTIONS_EXPERIENCE_TOOLTIP = translatable("ucutils.options.widgets.payday.options.experience.tooltip");

    @Override
    public Component text() {
        MutableComponent payDayInfoText = keyValue("PayDay", empty()
                .append(literal(valueOf(configuration.getMinutesSinceLastPayDay())))
                .append(literal("/").withStyle(DARK_GRAY))
                .append(literal("60")));

        if (getWidgetConfiguration().isShowSalary()) {
            payDayInfoText.append(" ").append(keyValue("Gehalt", configuration.getPredictedPayDaySalary() + "$"));
        }

        if (getWidgetConfiguration().isShowExperience()) {
            payDayInfoText.append(" ").append(keyValue("Exp", valueOf(configuration.getPredictedPayDayExp())));
        }

        return payDayInfoText;
    }

    @Override
    public Color getBackgroundColor() {
        // with over 100.000$ on bank and PayDay within next 5 minutes, animate background
        return configuration.getMinutesSinceLastPayDay() >= 55 && configuration.getMoneyBankAmount() > 100000 && (currentTimeMillis() / 500 % 2 == 0)
                ? RED
                : super.getBackgroundColor();
    }

    @Override
    public Component getDisplayName() {
        return WIDGETS_PAYDAY_OPTIONS_NAME;
    }

    @Override
    public Component getTooltip() {
        return WIDGETS_PAYDAY_OPTIONS_TOOLTIP;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Configuration extends UCUtilsWidgetConfiguration implements IOptionWidget {

        private boolean showSalary = true;
        private boolean showExperience = true;

        @Override
        public LayoutElement optionsWidget() {
            LinearLayout directionalLayoutWidget = horizontal().spacing(8);
            renderService.addToggleButton(directionalLayoutWidget, WIDGETS_PAYDAY_OPTIONS_SALARY_NAME, WIDGETS_PAYDAY_OPTIONS_SALARY_TOOLTIP, (options, value) -> this.showSalary = value, options -> this.showSalary, 150);
            renderService.addToggleButton(directionalLayoutWidget, WIDGETS_PAYDAY_OPTIONS_EXPERIENCE_NAME, WIDGETS_PAYDAY_OPTIONS_EXPERIENCE_TOOLTIP, (options, value) -> this.showExperience = value, options -> this.showExperience, 150);
            return directionalLayoutWidget;
        }
    }
}

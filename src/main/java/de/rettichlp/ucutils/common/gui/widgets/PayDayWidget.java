package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCTextWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.WidgetConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.Color;

import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.ON;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.common.services.RenderService.keyValue;
import static java.awt.Color.RED;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

public class PayDayWidget extends AbstractTRCTextWidget<PayDayWidget.Configuration> {

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

        Color fontColor = configuration.getMinutesSinceLastPayDay() >= 55 && configuration.getMoneyBankAmount() > 100000 && (currentTimeMillis() / 500 % 2 == 0)
                ? RED
                : getWidgetConfiguration().getColor();

        return payDayInfoText.withColor(fontColor.getRGB());
    }

    @Override
    public @Nullable String getRegistryName() {
        return "payday";
    }

    @Override
    public Component getLabel() {
        return WIDGETS_PAYDAY_OPTIONS_NAME;
    }

    @Override
    public Component getTooltip() {
        return WIDGETS_PAYDAY_OPTIONS_TOOLTIP;
    }

    @Override
    public void addOptions(@NonNull TRCOptionsList optionsList) {
        optionsList.addToggleButton(WIDGETS_PAYDAY_OPTIONS_SALARY_NAME, create(WIDGETS_PAYDAY_OPTIONS_SALARY_TOOLTIP), getWidgetConfiguration().isShowSalary(), (_, value) -> getWidgetConfiguration().setShowSalary(value == ON));
        optionsList.addToggleButton(WIDGETS_PAYDAY_OPTIONS_EXPERIENCE_NAME, create(WIDGETS_PAYDAY_OPTIONS_EXPERIENCE_TOOLTIP), getWidgetConfiguration().isShowExperience(), (_, value) -> getWidgetConfiguration().setShowExperience(value == ON));
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Configuration extends WidgetConfiguration {

        private boolean showSalary = true;
        private boolean showExperience = true;
    }
}

package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.configuration.options.CarOptions;
import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import de.rettichlp.ucutils.common.gui.screens.components.ToggleButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.text.Text;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static net.minecraft.client.gui.widget.Positioner.create;
import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Formatting.GOLD;

public class CarOptionsScreen extends OptionsScreen {

    private static final Text TEXT_CAR = translatable("ucutils.options.text.car");
    private static final Text CAR_PREMIUM_INFO = translatable("ucutils.options.car.premium_info");
    private static final Text CAR_GENERAL_FAST_FIND_NAME = translatable("ucutils.options.car.general.fast_find.name");
    private static final Text CAR_GENERAL_FAST_FIND_TOOLTIP = translatable("ucutils.options.car.general.fast_find.tooltip");
    private static final Text CAR_GENERAL_FAST_LOCK_NAME = translatable("ucutils.options.car.general.fast_lock.name");
    private static final Text CAR_GENERAL_FAST_LOCK_TOOLTIP = translatable("ucutils.options.car.general.fast_lock.tooltip");
    private static final Text CAR_GENERAL_HIGHLIGHT_NAME = translatable("ucutils.options.car.general.highlight.name");
    private static final Text CAR_GENERAL_HIGHLIGHT_TOOLTIP = translatable("ucutils.options.car.general.highlight.tooltip");
    private static final Text CAR_AUTOMATION_LOCK_NAME = translatable("ucutils.options.car.automation.lock.name");
    private static final Text CAR_AUTOMATION_LOCK_TOOLTIP = translatable("ucutils.options.car.automation.lock.tooltip");
    private static final Text CAR_AUTOMATION_START_NAME = translatable("ucutils.options.car.automation.start.name");
    private static final Text CAR_AUTOMATION_START_TOOLTIP = translatable("ucutils.options.car.automation.start.tooltip");
    private static final Text CAR_AUTOMATION_CHECK_KFZ_NAME = translatable("ucutils.options.car.automation.check_kfz.name");
    private static final Text CAR_AUTOMATION_CHECK_KFZ_TOOLTIP = translatable("ucutils.options.car.automation.check_kfz.tooltip");

    public CarOptionsScreen(Screen parent) {
        super(parent, TEXT_CAR);
    }

    @Override
    public void initBody() {
        GridWidget gridWidget = this.layout.addBody(new GridWidget());
        gridWidget.setColumnSpacing(8).setRowSpacing(4);
        GridWidget.Adder gridWidgetAdder = gridWidget.createAdder(2);

        CarOptions carOptions = configuration.getOptions().car();

        ToggleButtonWidget toggleButton1 = new ToggleButtonWidget(CAR_GENERAL_HIGHLIGHT_NAME, carOptions::highlight, carOptions.highlight());
        toggleButton1.setWidth(150);
        toggleButton1.setTooltip(Tooltip.of(CAR_GENERAL_HIGHLIGHT_TOOLTIP));
        gridWidgetAdder.add(toggleButton1);

        ToggleButtonWidget toggleButton2 = new ToggleButtonWidget(CAR_GENERAL_FAST_FIND_NAME, carOptions::fastFind, carOptions.fastFind());
        toggleButton2.setWidth(150);
        toggleButton2.setTooltip(Tooltip.of(CAR_GENERAL_FAST_FIND_TOOLTIP));
        gridWidgetAdder.add(toggleButton2);

        ToggleButtonWidget toggleButton3 = new ToggleButtonWidget(CAR_AUTOMATION_CHECK_KFZ_NAME, carOptions::automatedCheckKfz, carOptions.automatedCheckKfz());
        toggleButton3.setWidth(150);
        toggleButton3.setTooltip(Tooltip.of(CAR_AUTOMATION_CHECK_KFZ_TOOLTIP));
        gridWidgetAdder.add(toggleButton3);

        MultilineTextWidget multilineTextWidget = gridWidgetAdder.add(new MultilineTextWidget(CAR_PREMIUM_INFO.copy().formatted(GOLD), this.textRenderer), 2, create().alignHorizontalCenter().marginTop(16));
        multilineTextWidget.setMaxWidth(308);
        multilineTextWidget.setCentered(true);

        ToggleButtonWidget toggleButton4 = new ToggleButtonWidget(CAR_GENERAL_FAST_LOCK_NAME, carOptions::fastLock, carOptions.fastLock());
        toggleButton4.setWidth(150);
        toggleButton4.setTooltip(Tooltip.of(CAR_GENERAL_FAST_LOCK_TOOLTIP));
        gridWidgetAdder.add(toggleButton4);

        ToggleButtonWidget toggleButton5 = new ToggleButtonWidget(CAR_AUTOMATION_LOCK_NAME, carOptions::automatedLock, carOptions.automatedLock());
        toggleButton5.setWidth(150);
        toggleButton5.setTooltip(Tooltip.of(CAR_AUTOMATION_LOCK_TOOLTIP));
        gridWidgetAdder.add(toggleButton5);

        ToggleButtonWidget toggleButton6 = new ToggleButtonWidget(CAR_AUTOMATION_START_NAME, carOptions::automatedStart, carOptions.automatedStart());
        toggleButton6.setWidth(150);
        toggleButton6.setTooltip(Tooltip.of(CAR_AUTOMATION_START_TOOLTIP));
        gridWidgetAdder.add(toggleButton6);

        gridWidget.refreshPositions();
        gridWidget.forEachChild(this::addDrawableChild);
    }
}

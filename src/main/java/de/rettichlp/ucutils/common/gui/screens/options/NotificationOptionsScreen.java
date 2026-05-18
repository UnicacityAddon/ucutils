package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.configuration.options.NotificationOptions;
import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import de.rettichlp.ucutils.common.gui.screens.components.ToggleButtonWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.renderService;
import static net.minecraft.text.Text.translatable;

public class NotificationOptionsScreen extends OptionsScreen {

    private static final Text TEXT_NOTIFICATIONS = translatable("ucutils.options.text.notifications");
    private static final Text NOTIFICATION_JOIN_QUIT_NAME = translatable("ucutils.options.notifications.join_quit.name");
    private static final Text NOTIFICATION_JOIN_QUIT_TOOLTIP = translatable("ucutils.options.notifications.join_quit.tooltip");
    private static final Text NOTIFICATION_ADUTY_NAME = translatable("ucutils.options.notifications.aduty.name");
    private static final Text NOTIFICATION_ADUTY_TOOLTIP = translatable("ucutils.options.notifications.aduty.tooltip");
    private static final Text NOTIFICATION_REPORT_NAME = translatable("ucutils.options.notifications.report.name");
    private static final Text NOTIFICATION_REPORT_TOOLTIP = translatable("ucutils.options.notifications.report.tooltip");
    private static final Text NOTIFICATION_BUILD_MODE_NAME = translatable("ucutils.options.notifications.build_mode.name");
    private static final Text NOTIFICATION_BUILD_MODE_TOOLTIP = translatable("ucutils.options.notifications.build_mode.tooltip");

    public NotificationOptionsScreen(Screen parent) {
        super(parent, TEXT_NOTIFICATIONS);
    }

    @Override
    public void doOnClose() {
        renderService.getWidgets().forEach(AbstractUCUtilsWidget::saveConfiguration);
        super.doOnClose();
    }

    @Override
    public void initBody() {
        GridWidget gridWidget = this.layout.addBody(new GridWidget());
        gridWidget.setColumnSpacing(8).setRowSpacing(4);
        GridWidget.Adder gridWidgetAdder = gridWidget.createAdder(2);

        NotificationOptions notificationOptions = configuration.getOptions().notification();

        ToggleButtonWidget toggleButton1 = new ToggleButtonWidget(NOTIFICATION_JOIN_QUIT_NAME, notificationOptions::joinQuit, notificationOptions.joinQuit());
        toggleButton1.setWidth(150);
        toggleButton1.setTooltip(Tooltip.of(NOTIFICATION_JOIN_QUIT_TOOLTIP));
        gridWidgetAdder.add(toggleButton1);

        ToggleButtonWidget toggleButton2 = new ToggleButtonWidget(NOTIFICATION_ADUTY_NAME, notificationOptions::aDuty, notificationOptions.aDuty());
        toggleButton2.setWidth(150);
        toggleButton2.setTooltip(Tooltip.of(NOTIFICATION_ADUTY_TOOLTIP));
        gridWidgetAdder.add(toggleButton2);

        ToggleButtonWidget toggleButton3 = new ToggleButtonWidget(NOTIFICATION_REPORT_NAME, notificationOptions::report, notificationOptions.report());
        toggleButton3.setWidth(150);
        toggleButton3.setTooltip(Tooltip.of(NOTIFICATION_REPORT_TOOLTIP));
        gridWidgetAdder.add(toggleButton3);

        ToggleButtonWidget toggleButton4 = new ToggleButtonWidget(NOTIFICATION_BUILD_MODE_NAME, notificationOptions::buildMode, notificationOptions.buildMode());
        toggleButton4.setWidth(150);
        toggleButton4.setTooltip(Tooltip.of(NOTIFICATION_BUILD_MODE_TOOLTIP));
        gridWidgetAdder.add(toggleButton4);

        gridWidget.refreshPositions();
        gridWidget.forEachChild(this::addDrawableChild);
    }
}

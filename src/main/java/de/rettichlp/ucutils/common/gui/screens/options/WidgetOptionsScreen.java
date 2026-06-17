package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import de.rettichlp.ucutils.common.gui.screens.components.ToggleButtonWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.IOptionWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static de.rettichlp.ucutils.UCUtils.renderService;
import static net.minecraft.client.gui.layouts.LinearLayout.vertical;
import static net.minecraft.network.chat.Component.translatable;

public class WidgetOptionsScreen extends OptionsScreen {

    private static final Component TEXT_WIDGETS = translatable("ucutils.options.text.widgets");
    private static final Component TEXT_GENERAL = translatable("ucutils.options.text.general");
    private static final Component TEXT_POSITION = translatable("ucutils.options.text.position");

    public WidgetOptionsScreen(Screen parent) {
        super(parent, TEXT_WIDGETS, false);
    }

    @Override
    public void doOnClose() {
        renderService.getWidgets().forEach(AbstractUCUtilsWidget::saveConfiguration);
        super.doOnClose();
    }

    @Override
    public void initBody() {
        LinearLayout directionalLayoutWidget = this.layout.addToContents(vertical().spacing(4));

        // general
        directionalLayoutWidget.addChild(new StringWidget(TEXT_GENERAL, this.font), LayoutSettings::alignHorizontallyCenter);

        directionalLayoutWidget.addChild(Button.builder(TEXT_POSITION, button -> this.minecraft.setScreen(new WidgetOptionsPositionScreen(this))).width(308).build());

        // general - enable status
        GridLayout gridLayout = this.layout.addToContents(new GridLayout());
        gridLayout.columnSpacing(8).rowSpacing(4);
        GridLayout.RowHelper gridLayoutRowHelper = gridLayout.createRowHelper(2);

        renderService.getWidgets().forEach(abstractUCUtilsWidget -> {
            Component displayName = abstractUCUtilsWidget.getDisplayName();
            ToggleButtonWidget toggleButton = new ToggleButtonWidget(displayName, value -> abstractUCUtilsWidget.getWidgetConfiguration().setEnabled(value), abstractUCUtilsWidget.getWidgetConfiguration().isEnabled());
            toggleButton.setTooltip(Tooltip.create(abstractUCUtilsWidget.getTooltip()));
            gridLayoutRowHelper.addChild(toggleButton);
        });

        gridLayout.arrangeElements();
        gridLayout.visitWidgets(this::addRenderableWidget);

        // options section per widget
        renderService.getWidgets().stream()
                .filter(abstractUCUtilsWidget -> abstractUCUtilsWidget.getWidgetConfiguration() instanceof IOptionWidget)
                .forEach(abstractUCUtilsWidget -> {
                    IOptionWidget iOptionWidget = (IOptionWidget) abstractUCUtilsWidget.getWidgetConfiguration();

                    // section title
                    Component text = abstractUCUtilsWidget.getDisplayName();
                    directionalLayoutWidget.addChild(new StringWidget(text, this.font), positioner -> positioner.alignHorizontallyCenter().paddingTop(16));

                    // options widget
                    directionalLayoutWidget.addChild(iOptionWidget.optionsWidget(), LayoutSettings::alignHorizontallyCenter);
                });

        directionalLayoutWidget.visitWidgets(this::addRenderableWidget);
    }

    // disable background rendering to see overlay better

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {}
}

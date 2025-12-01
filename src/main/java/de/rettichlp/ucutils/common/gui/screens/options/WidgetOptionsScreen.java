package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import de.rettichlp.ucutils.common.gui.screens.components.ToggleButtonWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.IOptionWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import static de.rettichlp.ucutils.UCUtils.renderService;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.vertical;
import static net.minecraft.text.Text.translatable;

public class WidgetOptionsScreen extends OptionsScreen {

    private static final Text TEXT_WIDGETS = translatable("ucutils.options.text.widgets");
    private static final Text TEXT_GENERAL = translatable("ucutils.options.text.general");
    private static final Text TEXT_POSITION = translatable("ucutils.options.text.position");

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
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(vertical().spacing(4));

        // general
        directionalLayoutWidget.add(new TextWidget(TEXT_GENERAL, this.textRenderer), Positioner::alignHorizontalCenter);

        renderService.addButton(directionalLayoutWidget, TEXT_POSITION, button -> this.client.setScreen(new WidgetOptionsPositionScreen(this)), 308);

        // general - enable status
        GridWidget gridWidget = directionalLayoutWidget.add(new GridWidget());
        gridWidget.setColumnSpacing(8).setRowSpacing(4);
        GridWidget.Adder gridWidgetAdder = gridWidget.createAdder(2);

        renderService.getWidgets().forEach(abstractUCUtilsWidget -> {
            Text displayName = abstractUCUtilsWidget.getDisplayName();
            ToggleButtonWidget toggleButton = new ToggleButtonWidget(displayName, value -> abstractUCUtilsWidget.getWidgetConfiguration().setEnabled(value), abstractUCUtilsWidget.getWidgetConfiguration().isEnabled());
            toggleButton.setTooltip(Tooltip.of(abstractUCUtilsWidget.getTooltip()));
            gridWidgetAdder.add(toggleButton);
        });

        gridWidget.refreshPositions();
        gridWidget.forEachChild(this::addDrawableChild);

        // options section per widget
        renderService.getWidgets().stream()
                .filter(abstractUCUtilsWidget -> abstractUCUtilsWidget.getWidgetConfiguration() instanceof IOptionWidget)
                .forEach(abstractUCUtilsWidget -> {
                    IOptionWidget iOptionWidget = (IOptionWidget) abstractUCUtilsWidget.getWidgetConfiguration();

                    // section title
                    Text text = abstractUCUtilsWidget.getDisplayName();
                    directionalLayoutWidget.add(new TextWidget(text, this.textRenderer), positioner -> positioner.alignHorizontalCenter().marginTop(16));

                    // options widget
                    directionalLayoutWidget.add(iOptionWidget.optionsWidget(), Positioner::alignHorizontalCenter);
                });

        directionalLayoutWidget.forEachChild(this::addDrawableChild);
    }

    // disable background rendering to see overlay better
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}
}

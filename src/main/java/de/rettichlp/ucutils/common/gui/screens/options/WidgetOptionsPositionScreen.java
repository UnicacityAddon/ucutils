package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.gui.screens.UCUtilsScreen;
import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.renderService;
import static de.rettichlp.ucutils.common.services.RenderService.TEXT_BOX_PADDING;
import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.GRAY;
import static java.awt.Color.GREEN;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.horizontal;
import static net.minecraft.screen.ScreenTexts.CANCEL;
import static net.minecraft.screen.ScreenTexts.DONE;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.of;

public class WidgetOptionsPositionScreen extends UCUtilsScreen {

    private AbstractUCUtilsWidget<?> selectedWidget;
    private double oldMouseX;
    private double oldMouseY;

    public WidgetOptionsPositionScreen(Screen parent) {
        super(empty(), empty(), parent, false);
    }

    @Override
    public void initBody() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(horizontal().spacing(8), positioner -> positioner.marginTop(this.client.getWindow().getScaledHeight() / 4));

        renderService.addButton(directionalLayoutWidget, DONE, button -> {
            renderService.getWidgets().forEach(AbstractUCUtilsWidget::saveConfiguration);
            back();
        }, 150);

        renderService.addButton(directionalLayoutWidget, CANCEL, button -> {
            // restore configurations from the configuration file
            renderService.getWidgets().forEach(AbstractUCUtilsWidget::loadConfiguration);
            back();
        }, 150);

        directionalLayoutWidget.forEachChild(this::addDrawableChild);
    }

    @Override
    public void doOnClose() {
        configuration.saveToFile();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // draw center help lines
        Window window = this.client.getWindow();
        int scaledWidth = window.getScaledWidth();
        int scaledHeight = window.getScaledHeight();
        context.drawHorizontalLine(0, scaledWidth, scaledHeight / 2 - 1, GREEN.getRGB());
        context.drawVerticalLine(scaledWidth / 2 - 1, 0, scaledHeight, GREEN.getRGB());

        // draw widget help lines
        renderService.getWidgets().forEach(abstractUCUtilsWidget -> {
            double xTopLeft = abstractUCUtilsWidget.getWidgetConfiguration().getX();
            double yTopLeft = abstractUCUtilsWidget.getWidgetConfiguration().getY();
            double xBottomRight = xTopLeft + abstractUCUtilsWidget.getWidth();
            double yBottomRight = yTopLeft + abstractUCUtilsWidget.getHeight();

            context.drawHorizontalLine(0, scaledWidth, (int) yTopLeft, GRAY.getRGB());
            context.drawHorizontalLine(0, scaledWidth, (int) yBottomRight - 1, GRAY.getRGB());
            context.drawVerticalLine((int) xTopLeft, 0, scaledHeight, GRAY.getRGB());
            context.drawVerticalLine((int) xBottomRight - 1, 0, scaledHeight, GRAY.getRGB());
        });

        if (isNull(this.selectedWidget)) {
            return;
        }

        int textX = mouseX + 10;
        int textY = mouseY + 10;

        UCUtilsWidgetConfiguration widgetConfiguration = this.selectedWidget.getWidgetConfiguration();

        // draw border around the selected widget
        double x = widgetConfiguration.getX();
        double y = widgetConfiguration.getY();

        // draw widget location text box
        Text widgetLocationText = of("X: " + x + " Y: " + y + " (W: " + this.selectedWidget.getWidth() + " H: " + this.selectedWidget.getHeight() + ")");
        context.fill(textX - TEXT_BOX_PADDING, textY - TEXT_BOX_PADDING, textX + this.textRenderer.getWidth(widgetLocationText) + TEXT_BOX_PADDING, textY + this.textRenderer.fontHeight + TEXT_BOX_PADDING, renderService.getSecondaryColor(BLACK).getRGB());
        context.drawText(this.textRenderer, widgetLocationText, textX, textY, 0xFFFFFFFF, false);

        // draw widget center lines
        double centerX = x + (this.selectedWidget.getWidth() / 2.0);
        double centerY = y + (this.selectedWidget.getHeight() / 2.0);
        context.drawHorizontalLine(0, scaledWidth, (int) centerY, BLUE.getRGB());
        context.drawVerticalLine((int) centerX, 0, scaledHeight, BLUE.getRGB());
    }

    // disable background rendering to see overlay better
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);

        double deltaX = mouseX - this.oldMouseX;
        double deltaY = mouseY - this.oldMouseY;

        this.oldMouseX = mouseX;
        this.oldMouseY = mouseY;

        if (nonNull(this.selectedWidget)) {
            UCUtilsWidgetConfiguration widgetConfiguration = this.selectedWidget.getWidgetConfiguration();
            widgetConfiguration.setX(widgetConfiguration.getX() + deltaX);
            widgetConfiguration.setY(widgetConfiguration.getY() + deltaY);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        boolean mouseClicked = super.mouseClicked(click, doubled);

        renderService.getWidgets().stream()
                .filter(abstractUCUtilsWidget -> abstractUCUtilsWidget.isMouseOver(click.x(), click.y()))
                .findFirst()
                .ifPresent(abstractUCUtilsWidget -> this.selectedWidget = abstractUCUtilsWidget);

        return mouseClicked;
    }

    @Override
    public boolean mouseReleased(Click click) {
        boolean mouseReleased = super.mouseReleased(click);
        this.selectedWidget = null;
        return mouseReleased;
    }
}

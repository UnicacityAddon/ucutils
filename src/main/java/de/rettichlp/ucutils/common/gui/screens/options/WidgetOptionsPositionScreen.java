package de.rettichlp.ucutils.common.gui.screens.options;

import com.mojang.blaze3d.platform.Window;
import de.rettichlp.ucutils.common.gui.screens.UCUtilsScreen;
import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.renderService;
import static de.rettichlp.ucutils.common.services.RenderService.TEXT_BOX_PADDING;
import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.GRAY;
import static java.awt.Color.GREEN;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static net.minecraft.client.gui.layouts.LinearLayout.horizontal;
import static net.minecraft.network.chat.CommonComponents.GUI_CANCEL;
import static net.minecraft.network.chat.CommonComponents.GUI_DONE;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

public class WidgetOptionsPositionScreen extends UCUtilsScreen {

    private AbstractUCUtilsWidget<?> selectedWidget;
    private double oldMouseX;
    private double oldMouseY;

    public WidgetOptionsPositionScreen(Screen parent) {
        super(empty(), empty(), parent, false);
    }

    @Override
    public void initBody() {
        LinearLayout directionalLayoutWidget = this.layout.addToContents(horizontal().spacing(8), positioner -> positioner.paddingTop(this.minecraft.getWindow().getGuiScaledHeight() / 4));

        directionalLayoutWidget.addChild(Button.builder(GUI_DONE, button -> {
            renderService.getWidgets().forEach(AbstractUCUtilsWidget::saveConfiguration);
            back();
        }).width(150).build());

        directionalLayoutWidget.addChild(Button.builder(GUI_CANCEL, button -> {
            // restore configurations from the configuration file
            renderService.getWidgets().forEach(AbstractUCUtilsWidget::loadConfiguration);
            back();
        }).width(150).build());

        directionalLayoutWidget.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void doOnClose() {
        configuration.saveToFile();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        // draw center help lines
        Window window = this.minecraft.getWindow();
        int scaledWidth = window.getGuiScaledWidth();
        int scaledHeight = window.getGuiScaledHeight();
        graphics.horizontalLine(0, scaledWidth, scaledHeight / 2 - 1, GREEN.getRGB());
        graphics.verticalLine(scaledWidth / 2 - 1, 0, scaledHeight, GREEN.getRGB());

        // draw widget help lines
        renderService.getWidgets().forEach(abstractUCUtilsWidget -> {
            double xTopLeft = abstractUCUtilsWidget.getWidgetConfiguration().getX();
            double yTopLeft = abstractUCUtilsWidget.getWidgetConfiguration().getY();
            double xBottomRight = xTopLeft + abstractUCUtilsWidget.getWidth();
            double yBottomRight = yTopLeft + abstractUCUtilsWidget.getHeight();

            graphics.horizontalLine(0, scaledWidth, (int) yTopLeft, GRAY.getRGB());
            graphics.horizontalLine(0, scaledWidth, (int) yBottomRight - 1, GRAY.getRGB());
            graphics.verticalLine((int) xTopLeft, 0, scaledHeight, GRAY.getRGB());
            graphics.verticalLine((int) xBottomRight - 1, 0, scaledHeight, GRAY.getRGB());
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
        Component widgetLocationText = literal("X: " + x + " Y: " + y + " (W: " + this.selectedWidget.getWidth() + " H: " + this.selectedWidget.getHeight() + ")");
        graphics.fill(textX - TEXT_BOX_PADDING, textY - TEXT_BOX_PADDING, textX + this.font.width(widgetLocationText) + TEXT_BOX_PADDING, textY + this.font.lineHeight + TEXT_BOX_PADDING, renderService.getSecondaryColor(BLACK).getRGB());
        graphics.text(this.font, widgetLocationText, textX, textY, 0xFFFFFFFF, false);

        // draw widget center lines
        double centerX = x + (this.selectedWidget.getWidth() / 2.0);
        double centerY = y + (this.selectedWidget.getHeight() / 2.0);
        graphics.horizontalLine(0, scaledWidth, (int) centerY, BLUE.getRGB());
        graphics.verticalLine((int) centerX, 0, scaledHeight, BLUE.getRGB());
    }

    // disable background rendering to see overlay better
    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {}

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
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        boolean mouseClicked = super.mouseClicked(event, doubleClick);

        renderService.getWidgets().stream()
                .filter(abstractUCUtilsWidget -> abstractUCUtilsWidget.isMouseOver(event.x(), event.y()))
                .findFirst()
                .ifPresent(abstractUCUtilsWidget -> this.selectedWidget = abstractUCUtilsWidget);

        return mouseClicked;
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        boolean mouseReleased = super.mouseReleased(event);
        this.selectedWidget = null;
        return mouseReleased;
    }
}

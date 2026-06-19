package de.rettichlp.ucutils.common.gui.widgets.alignment;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.network.chat.Component.empty;

public class AlignVerticalWidget extends AlignWidget<AbstractUCUtilsWidget<UCUtilsWidgetConfiguration>> {

    @Override
    public void add(AbstractUCUtilsWidget<UCUtilsWidgetConfiguration> entry) {
        this.ucUtilsWidgets.add(entry);
    }

    @Override
    public Component getDisplayName() {
        return empty();
    }

    @Override
    public Component getTooltip() {
        return empty();
    }

    @Override
    public int getWidth() {
        return this.ucUtilsWidgets.stream().map(AbstractUCUtilsWidget::getWidth).max(Integer::compareTo).orElse(0);
    }

    @Override
    public int getHeight() {
        return this.ucUtilsWidgets.stream().map(AbstractUCUtilsWidget::getHeight).reduce(0, Integer::sum);
    }

    @Override
    public void draw(@NotNull GuiGraphicsExtractor graphics, int x, int y, AbstractUCUtilsWidget.Alignment alignment) {
        int yOffset = y;

        for (AbstractUCUtilsWidget<UCUtilsWidgetConfiguration> ucUtilsWidget : this.ucUtilsWidgets) {
            // apply alignment
            int alignmentXModifier = switch (alignment) {
                case LEFT -> 0;
                case CENTER -> (getWidth() - ucUtilsWidget.getWidth()) / 2;
                case RIGHT -> getWidth() - ucUtilsWidget.getWidth();
            };

            ucUtilsWidget.draw(graphics, x + alignmentXModifier, yOffset, alignment);
            yOffset += ucUtilsWidget.getHeight();
        }
    }
}

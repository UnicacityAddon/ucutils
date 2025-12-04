package de.rettichlp.ucutils.common.gui.widgets.alignment;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.text.Text.empty;

public class AlignHorizontalWidget extends AlignWidget<AbstractUCUtilsWidget<UCUtilsWidgetConfiguration>> {

    @Override
    public void add(AbstractUCUtilsWidget<UCUtilsWidgetConfiguration> entry) {
        this.ucUtilsWidgets.add(entry);
    }

    @Override
    public Text getDisplayName() {
        return empty();
    }

    @Override
    public Text getTooltip() {
        return empty();
    }

    @Override
    public int getWidth() {
        return this.ucUtilsWidgets.stream().map(AbstractUCUtilsWidget::getWidth).reduce(0, Integer::sum);
    }

    @Override
    public int getHeight() {
        return this.ucUtilsWidgets.stream().map(AbstractUCUtilsWidget::getHeight).max(Integer::compareTo).orElse(0);
    }

    @Override
    public void draw(@NotNull DrawContext drawContext, int x, int y, AbstractUCUtilsWidget.Alignment alignment) {
        int xOffset = x;

        for (AbstractUCUtilsWidget<UCUtilsWidgetConfiguration> ucUtilsWidget : this.ucUtilsWidgets) {
            ucUtilsWidget.draw(drawContext, xOffset, y, alignment);
            xOffset += ucUtilsWidget.getWidth();
        }
    }
}

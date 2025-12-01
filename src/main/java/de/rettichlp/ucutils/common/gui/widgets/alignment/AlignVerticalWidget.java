package de.rettichlp.ucutils.common.gui.widgets.alignment;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

import static de.rettichlp.ucutils.UCUtils.renderService;
import static net.minecraft.text.Text.empty;

public class AlignVerticalWidget extends AlignWidget<AbstractUCUtilsWidget> {

    @Override
    public void add(AbstractUCUtilsWidget entry) {
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
        return this.ucUtilsWidgets.stream().map(AbstractUCUtilsWidget::getWidth).max(Integer::compareTo).orElse(0);
    }

    @Override
    public int getHeight() {
        return this.ucUtilsWidgets.stream().map(AbstractUCUtilsWidget::getHeight).reduce(0, Integer::sum);
    }

    @Override
    public void draw(@NotNull DrawContext drawContext, int x, int y, AbstractUCUtilsWidget.Alignment alignment) {
        int yOffset = y;

        for (AbstractUCUtilsWidget ucUtilsWidget : this.ucUtilsWidgets) {
            // apply alignment
            int alignmentXModifier = switch (alignment) {
                case LEFT -> 0;
                case CENTER -> (getWidth() - ucUtilsWidget.getWidth()) / 2;
                case RIGHT -> getWidth() - ucUtilsWidget.getWidth();
            };

            ucUtilsWidget.draw(drawContext, x + alignmentXModifier, yOffset, alignment);
            yOffset += ucUtilsWidget.getHeight();
        }

        // debug: draw outline
        if (renderService.isDebugEnabled()) {
            // FIXME drawContext.drawBorder(x, y, getWidth(), getHeight(), new Color(0, 255, 0).getRGB());
        }
    }
}

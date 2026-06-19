package de.rettichlp.ucutils.common.gui.widgets.base;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.time.temporal.Temporal;

import static de.rettichlp.ucutils.common.services.RenderService.TEXT_BOX_PADDING;
import static java.lang.Math.clamp;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;

public abstract class AbstractUCUtilsProgressTextWidget<C extends UCUtilsWidgetConfiguration> extends AbstractUCUtilsTextWidget<C> {

    @Override
    public abstract Component text();

    @Override
    public void draw(@NotNull GuiGraphicsExtractor graphics, int x, int y, Alignment alignment) {
        graphics.fill(x, y, x + getWidth(), y + getHeight(), getBackgroundColor().getRGB());
        graphics.text(getFont(), text(), x + TEXT_BOX_PADDING, y + TEXT_BOX_PADDING, 0xFFFFFFFF, false);

        int maxProgressWidth = getWidth() - TEXT_BOX_PADDING * 2;
        int xProgressStart = (int) (x + TEXT_BOX_PADDING + maxProgressWidth * progress());
        int xProgressEnd = x + getWidth() - TEXT_BOX_PADDING;

        graphics.horizontalLine(xProgressStart, xProgressEnd, y + getHeight() - 3, getBorderColor().getRGB());
    }

    public abstract double progress();

    protected double calculateProgress(Temporal creationTime, long durationInMillis) {
        long elapsedMillis = between(creationTime, now()).toMillis();
        double progress = (double) elapsedMillis / durationInMillis;
        return clamp(progress, 0.0, 1.0);
    }
}

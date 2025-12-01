package de.rettichlp.ucutils.common.gui.screens.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDateTime;

import static de.rettichlp.ucutils.PKUtils.messageService;
import static de.rettichlp.ucutils.PKUtils.renderService;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.horizontal;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.of;

public class WeekSelectionWidget extends ClickableWidget {

    private final DirectionalLayoutWidget internalLayout = horizontal().spacing(8);

    public WeekSelectionWidget(ChronoLocalDateTime<LocalDate> from,
                               ChronoLocalDateTime<LocalDate> to,
                               ButtonWidget.PressAction onPressPrevious,
                               ButtonWidget.PressAction onPressNext) {
        super(0, 0, 456, 20, empty());

        renderService.addButton(this.internalLayout, of("←"), onPressPrevious, 20);

        renderService.addButton(this.internalLayout, empty()
                .append(of(messageService.dateTimeToFriendlyString(from)))
                .append(" - ")
                .append(of(messageService.dateTimeToFriendlyString(to))), button -> {}, 400);

        renderService.addButton(this.internalLayout, of("→"), onPressNext, 20);
    }

    @Override
    protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        this.internalLayout.setX(getX());
        this.internalLayout.setY(getY());

        this.internalLayout.refreshPositions();

        this.internalLayout.forEachChild(clickableWidget -> clickableWidget.render(context, mouseX, mouseY, delta));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.internalLayout.forEachChild(clickableWidget -> {
            clickableWidget.mouseClicked(mouseX, mouseY, button);
        });

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}

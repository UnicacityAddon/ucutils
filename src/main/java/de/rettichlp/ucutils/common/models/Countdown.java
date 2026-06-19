package de.rettichlp.ucutils.common.models;

import de.rettichlp.ucutils.common.gui.widgets.CountdownWidget;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;

import java.time.Duration;
import java.time.LocalDateTime;

import static de.rettichlp.ucutils.UCUtils.messageService;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.WHITE;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

@Getter
@RequiredArgsConstructor
public class Countdown {

    private final LocalDateTime startTime = now();
    private final String title;
    private final Duration duration;

    public Countdown(String title, Duration duration, Runnable runAfter) {
        this.title = title;
        this.duration = duration;

        utilService.delayedAction(runAfter, this.duration.toMillis());
    }

    public boolean isActive() {
        return getRemainingDuration().isPositive();
    }

    public Duration getRemainingDuration() {
        return between(now(), this.startTime.plus(this.duration));
    }

    public CountdownWidget toWidget() {
        String millisToFriendlyString = messageService.millisToFriendlyString(getRemainingDuration().toMillis());

        Component text = empty()
                .append(literal(this.title).withStyle(WHITE))
                .append(literal(":").withStyle(GRAY)).append(" ")
                .append(literal(millisToFriendlyString));

        return new CountdownWidget(text, this.startTime, this.duration.toMillis());
    }
}

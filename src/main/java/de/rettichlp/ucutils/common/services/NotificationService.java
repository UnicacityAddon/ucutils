package de.rettichlp.ucutils.common.services;

import de.rettichlp.ucutils.common.gui.widgets.NotificationWidget;
import lombok.Data;
import net.minecraft.text.Text;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.time.LocalDateTime.now;
import static java.util.Comparator.comparing;
import static java.util.Objects.hash;
import static java.util.Objects.nonNull;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class NotificationService {

    private final Collection<Notification> notifications = new ArrayList<>();

    public void sendSuccessNotification(Text text) {
        sendNotification(text, GREEN, 5000);
    }

    public void sendInfoNotification(Text text) {
        sendNotification(text, CYAN, 5000);
    }

    public void sendWarningNotification(Text text) {
        sendNotification(text, ORANGE, 5000);
    }

    public void sendErrorNotification(Text text) {
        sendNotification(text, RED, 5000);
    }

    public void sendNotification(Text text, Color color, long durationInMillis) {
        Notification notification = new Notification(text, durationInMillis);
        notification.setColor(color);
        this.notifications.add(notification);
    }

    public List<Notification> getActiveNotifications() {
        return this.notifications.stream()
                .filter(Objects::nonNull)
                .filter(notification -> now().isBefore(notification.getTimestamp().plus(notification.getDurationInMillis(), MILLISECONDS.toChronoUnit())))
                .sorted(comparing(Notification::getTimestamp))
                .toList();
    }

    @Data
    public static class Notification {

        private final UUID id = randomUUID();
        private final Text text;
        private final long durationInMillis;
        private final LocalDateTime timestamp = now();
        private Color color = WHITE;

        @Override
        public int hashCode() {
            return hash(this.id, this.text, this.durationInMillis, this.timestamp, this.color);
        }

        @Override
        public boolean equals(Object o) {
            return nonNull(o) && o instanceof Notification that && Objects.equals(this.id, that.id);
        }

        public NotificationWidget toWidget() {
            return new NotificationWidget(this.text, this.color, this.timestamp, this.durationInMillis);
        }
    }
}

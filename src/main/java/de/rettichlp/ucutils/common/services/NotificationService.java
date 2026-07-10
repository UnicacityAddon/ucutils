package de.rettichlp.ucutils.common.services;

import de.rettichlp.therettingtoncompanion.models.Notification;
import lombok.Getter;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.models.Sound.NOTIFICATION;
import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.time.Duration.ofMillis;

public class NotificationService {

    @Getter
    private final Set<Notification> notifications = new HashSet<>();

    public void sendSuccessNotification(Component text) {
        sendNotification(text, GREEN, 5000);
    }

    public void sendInfoNotification(Component text) {
        sendNotification(text, CYAN, 5000);
    }

    public void sendWarningNotification(Component text) {
        sendNotification(text, ORANGE, 5000);
    }

    public void sendErrorNotification(Component text) {
        sendNotification(text, RED, 5000);
    }

    public void sendNotification(Component text, Color color, long durationInMillis) {
        Notification notification = Notification.builder()
                .componentSupplier(() -> text)
                .color(color)
                .displayDuration(ofMillis(durationInMillis))
                .build();

        this.notifications.add(notification);
    }

    public void notificationSound(int repeating) {
        if (!configuration.getOptions().sound().notification()) {
            return;
        }

        for (int i = 0; i < repeating; i++) {
            utilService.delayedAction(() -> NOTIFICATION.play(1, 2), i * 150L);
        }
    }
}

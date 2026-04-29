package de.rettichlp.ucutils.common.api;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpResponse;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static de.rettichlp.ucutils.UCUtils.notificationService;
import static net.minecraft.text.Text.translatable;

@Getter
public class ApiException extends RuntimeException {

    private final HttpResponse<String> response;

    public ApiException(@NotNull HttpResponse<String> response) {
        super("Error while sending request: [" + response.statusCode() + "] -> " + (response.body().isBlank() ? "No response body" : response.body()) + " (" + response.request().uri().toString() + ")");
        this.response = response;
    }

    public ApiException(@NotNull HttpResponse<String> response, String message) {
        super(message);
        this.response = response;
    }

    public void sendNotification() {
        notificationService.sendErrorNotification(translatable("ucutils.notification.error.api", this.response.statusCode()));
    }

    public void log() {
        LOGGER.warn(getMessage());
    }
}

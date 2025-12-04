package de.rettichlp.ucutils.common.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static de.rettichlp.ucutils.UCUtils.syncService;
import static de.rettichlp.ucutils.common.services.WebSocketService.WebSocketMessageType.fromValue;
import static java.util.Arrays.stream;

public class WebSocketService implements WebSocket.Listener {

    @Override
    public void onOpen(@NotNull WebSocket webSocket) {
        LOGGER.info("Successfully connected to WebSocket");
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        LOGGER.info("WebSocket data received: {}", data);

        WebSocketMessageType webSocketMessageType = fromValue(data);
        switch (webSocketMessageType) {
            case REQUEST_CHECK_FOR_UPDATES -> syncService.checkForUpdates();
            case UNKNOWN -> LOGGER.warn("Received unknown WebSocket message type: {}", data);
        }

        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Getter
    @AllArgsConstructor
    enum WebSocketMessageType {

        UNKNOWN("UNKNOWN"),
        REQUEST_UPDATE_FACTION_MEMBERS("REQUEST_UPDATE_FACTION_MEMBERS"),
        REQUEST_UPDATE_BLACKLIST_REASONS("REQUEST_UPDATE_BLACKLIST_REASONS"),
        REQUEST_CHECK_FOR_UPDATES("REQUEST_CHECK_FOR_UPDATES");

        private final String value;

        public static WebSocketMessageType fromValue(CharSequence value) {
            String typeString = ((String) value).split(":")[0];

            return stream(values())
                    .filter(webSocketMessageType -> webSocketMessageType.getValue().contentEquals(typeString))
                    .findFirst()
                    .orElse(UNKNOWN);
        }
    }
}

package de.rettichlp.pkutils.common.services;

import org.jetbrains.annotations.NotNull;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

import static de.rettichlp.pkutils.PKUtils.LOGGER;
import static de.rettichlp.pkutils.PKUtils.syncService;

public class WebSocketService implements WebSocket.Listener {

    @Override
    public void onOpen(@NotNull WebSocket webSocket) {
        LOGGER.info("Successfully connected to WebSocket");
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        LOGGER.info("WebSocket data received: {}", data);

        WebSocketMessageType webSocketMessageType = WebSocketMessageType.valueOf((String) data);
        switch (webSocketMessageType) {
            case REQUEST_UPDATE_FACTION_MEMBERS -> syncService.syncFactionMembersWithApi();
            case REQUEST_UPDATE_BLACKLIST_REASONS -> syncService.syncBlacklistReasonsFromApi();
            case REQUEST_CHECK_FOR_UPDATES -> syncService.checkForUpdates();
        }

        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    private enum WebSocketMessageType {

        REQUEST_UPDATE_FACTION_MEMBERS,
        REQUEST_UPDATE_BLACKLIST_REASONS,
        REQUEST_CHECK_FOR_UPDATES
    }
}

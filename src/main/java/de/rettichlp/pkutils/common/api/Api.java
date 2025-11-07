package de.rettichlp.pkutils.common.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import de.rettichlp.pkutils.common.api.response.ErrorResponse;
import de.rettichlp.pkutils.common.api.response.FactionPlayerDataResponse;
import de.rettichlp.pkutils.common.api.response.GetUserInfoResponse;
import de.rettichlp.pkutils.common.api.response.WeeklyTime;
import de.rettichlp.pkutils.common.models.ActivityEntry;
import de.rettichlp.pkutils.common.models.BlacklistReason;
import de.rettichlp.pkutils.common.models.EquipEntry;
import de.rettichlp.pkutils.common.models.Faction;
import de.rettichlp.pkutils.common.models.FactionEntry;
import de.rettichlp.pkutils.common.services.WebSocketService;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static de.rettichlp.pkutils.PKUtils.LOGGER;
import static de.rettichlp.pkutils.PKUtils.notificationService;
import static de.rettichlp.pkutils.PKUtils.storage;
import static de.rettichlp.pkutils.PKUtils.syncService;
import static de.rettichlp.pkutils.PKUtils.utilService;
import static java.lang.String.join;
import static java.lang.String.valueOf;
import static java.net.URI.create;
import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

public class Api {

    private static final String MINECRAFT_UUID_STRING = ofNullable(MinecraftClient.getInstance())
            .map(MinecraftClient::getGameProfile)
            .map(GameProfile::getId)
            .map(UUID::toString)
            .orElse("");

    private static final String MINECRAFT_NAME = ofNullable(MinecraftClient.getInstance())
            .map(MinecraftClient::getGameProfile)
            .map(GameProfile::getName)
            .orElse("");

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final String baseUrl = "https://pkutils.rettichlp.de"; //http://localhost:6010/pkutils
    private final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("X-Minecraft-UUID", MINECRAFT_UUID_STRING)
            .header("X-Minecraft-Name", MINECRAFT_NAME)
            .header("X-PKU-Version", valueOf(utilService.getVersion()));

    @Getter
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>) (json, typeOfT, context) -> Instant.parse(json.getAsString()))
            .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) -> LocalTime.parse(json.getAsString()))
            .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
            .create();

    public void getUserInfo(String playerName, Consumer<GetUserInfoResponse> callback) {
        get("/v1/user/info?playerName=" + playerName, new TypeToken<>() {}, callback);
    }

    public void postUserRegister() {
        // register user
        post("/v1/user/register", new Object(), () -> LOGGER.info("Successfully registered user"));

        // initialize websocket connection
        String webSocketUrl = "ws://91.107.193.19:6010/pkutils/v1/ws";
        this.httpClient.newWebSocketBuilder().buildAsync(create(webSocketUrl), new WebSocketService()).exceptionally(throwable -> {
            LOGGER.error("Error while connecting to WebSocket", throwable);
            return null;
        });
    }

    public void getBlacklistReasons(@NotNull Faction faction, Consumer<List<BlacklistReason>> callback) {
        get("/v1/blacklist/reasons?faction=" + faction.name(), new TypeToken<>() {}, callback);
    }

    public void postBlacklistReasons(@NotNull Faction faction, List<BlacklistReason> blacklistReasons) {
        post("/v1/blacklist/reasons?faction=" + faction.name(), blacklistReasons, () -> LOGGER.info("Successfully updated blacklist reasons"));
    }

    public void getFactionMembers(Consumer<List<FactionEntry>> callback) {
        get("/v2/faction/members", new TypeToken<>() {}, callback);
    }

    public void postFactionMembers() {
        post("/v2/faction/members", storage.getFactionEntries(), () -> LOGGER.info("Successfully updated faction members"));
    }

    public void getFactionPlayerData(@NotNull ChronoLocalDateTime<LocalDate> from,
                                     @NotNull ChronoLocalDateTime<LocalDate> to,
                                     Iterable<String> playerNames,
                                     Consumer<List<FactionPlayerDataResponse>> callback) {
        String playerNamesParam = join(",", playerNames);
        Instant fromInstant = from.atZone(utilService.getServerZoneId()).toInstant();
        Instant toInstant = to.atZone(utilService.getServerZoneId()).toInstant();
        get("/v2/faction/playerdata?playerNames=" + playerNamesParam + "&from=" + fromInstant + "&to=" + toInstant, new TypeToken<>() {}, callback);
    }

    public void getFactionResetTime(Faction faction, Consumer<WeeklyTime> callback) {
        get("/v2/faction/resettime?faction=" + faction, new TypeToken<>() {}, callback);
    }

    public void putFactionActivityAdd(ActivityEntry.Type type) {
        if (!storage.isPunicaKitty()) {
            return;
        }

        put("/v2/faction/activity/add?type=" + type, null, () -> notificationService.sendInfoNotification(type.getSuccessMessage()));
    }

    public void putFactionEquipAdd(EquipEntry.Type type) {
        if (!storage.isPunicaKitty()) {
            return;
        }

        put("/v2/faction/equip/add?type=" + type, null, () -> notificationService.sendInfoNotification(type.getSuccessMessage()));
    }

    public void getBlacklistReasonData(Consumer<Map<Faction, List<BlacklistReason>>> callback) {
        get("https://gist.githubusercontent.com/rettichlp/54e97f4dbb3988bf22554c01d62af666/raw/pkutils-blacklistreasons.json", new TypeToken<>() {}, callback);
    }

    public void getModrinthVersions(Consumer<List<Map<String, Object>>> callback) {
        get("https://api.modrinth.com/v2/project/pkutils/version", new TypeToken<>() {}, callback);
    }

    private <T> void get(@NotNull String uri, TypeToken<T> typeToken, Consumer<T> callback) {
        HttpRequest httpRequest = this.requestBuilder.copy()
                .uri(uri.startsWith("https://") ? create(uri) : create(this.baseUrl + uri))
                .GET()
                .build();

        sendRequest(httpRequest, typeToken, callback);
    }

    private void put(String uri, Object bodyObject, Runnable runnable) {
        String jsonString = this.gson.toJson(bodyObject);
        HttpRequest.BodyPublisher body = ofString(jsonString);

        HttpRequest httpRequest = this.requestBuilder.copy()
                .uri(create(this.baseUrl + uri))
                .PUT(body)
                .build();

        sendRequest(httpRequest, null, object -> runnable.run());
    }

    private void post(String uri, Object bodyObject, Runnable runnable) {
        String jsonString = this.gson.toJson(bodyObject);
        HttpRequest.BodyPublisher body = ofString(jsonString);

        HttpRequest httpRequest = this.requestBuilder.copy()
                .uri(create(this.baseUrl + uri))
                .POST(body)
                .build();

        sendRequest(httpRequest, null, object -> runnable.run());
    }

    private <T> void sendRequest(HttpRequest httpRequest, TypeToken<T> typeToken, Consumer<T> callback) {
        if (!syncService.dataUsageConfirmed()) {
            LOGGER.warn("Data usage not confirmed, skipping API request: [{}] {}", httpRequest.method(), httpRequest.uri().toString());
            return;
        }

        this.httpClient.sendAsync(httpRequest, ofString())
                .thenApply(this::catchDefaultApiError)
                .thenApply(response -> ofNullable(typeToken)
                        .map(tt -> this.gson.fromJson(response.body(), tt))
                        .orElse(null))
                .thenAccept(responseObject -> {
                    LOGGER.info("Successfully sent request: [{}] {}", httpRequest.method(), httpRequest.uri().toString());
                    callback.accept(responseObject);
                })
                .exceptionally(throwable -> {
                    handleError(httpRequest, throwable);
                    return null;
                });
    }

    @Contract("_ -> param1")
    private @NotNull HttpResponse<String> catchDefaultApiError(@NotNull HttpResponse<String> response) {
        // check if the status code is 2xx and throw an exception if not
        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            return response;
        }

        // try to map to PKUtils API error response
        ErrorResponse errorResponse = getGson().fromJson(response.body(), ErrorResponse.class);

        if (nonNull(errorResponse)) {
            throw new PKUtilsApiException(response, errorResponse);
        }

        throw new ApiException(response);
    }

    private void handleError(HttpRequest httpRequest, @NotNull Throwable throwable) {
        if (throwable.getCause() instanceof ApiException apiException) {
            apiException.sendNotification();
            apiException.log();
        } else {
            LOGGER.error("Unexpected error while sending request: [{}] {}", httpRequest.method(), httpRequest.uri().toString(), throwable);
        }
    }
}

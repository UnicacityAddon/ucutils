package de.rettichlp.ucutils.common.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import de.rettichlp.ucutils.common.api.response.ErrorResponse;
import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.models.FactionMember;
import de.rettichlp.ucutils.common.models.TeamResponse;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static java.net.URI.create;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

public class Api {

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .header("Accept", "application/json")
            .header("Content-Type", "application/json");

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

    public void getModrinthVersions(Consumer<List<Map<String, Object>>> callback) {
        get("https://api.modrinth.com/v2/project/ucutils/version", new TypeToken<>() {}, callback);
    }

    public void getFactionMembers(@NotNull Faction faction, Consumer<List<FactionMember>> callback) {
        get("https://api.unicacity.eu/api/factions/" + faction.getApiKey() + "/members", new TypeToken<>() {}, callback);
    }

    public void getTeamMembers(Consumer<TeamResponse> callback) {
        get("https://api.unicacity.eu/api/team", new TypeToken<>() {}, callback);
    }

    private <T> void get(@NotNull String uri, TypeToken<T> typeToken, Consumer<T> callback) {
        HttpRequest httpRequest = this.requestBuilder.copy()
                .uri(create(uri))
                .GET()
                .build();

        sendRequest(httpRequest, typeToken, callback);
    }

    private <T> void sendRequest(HttpRequest httpRequest, TypeToken<T> typeToken, Consumer<T> callback) {
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

        // try to map to UCUtils API error response
        ErrorResponse errorResponse = getGson().fromJson(response.body(), ErrorResponse.class);

        if (nonNull(errorResponse)) {
            throw new UCUtilsApiException(response, errorResponse);
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

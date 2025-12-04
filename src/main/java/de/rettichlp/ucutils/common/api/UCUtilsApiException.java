package de.rettichlp.ucutils.common.api;

import de.rettichlp.ucutils.common.api.response.ErrorResponse;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpResponse;

@Getter
public class UCUtilsApiException extends ApiException {

    public UCUtilsApiException(@NotNull HttpResponse<String> response, @NotNull ErrorResponse errorResponse) {
        super(response, "Error while sending UCUtils API request: [" + errorResponse.httpStatusCode() + "] " + errorResponse.httpStatus() + " -> " + errorResponse.info() + " (" + response.request().uri().toString() + ")");
    }
}

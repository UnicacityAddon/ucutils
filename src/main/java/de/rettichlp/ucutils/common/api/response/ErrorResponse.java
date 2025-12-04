package de.rettichlp.ucutils.common.api.response;

public record ErrorResponse(int httpStatusCode, String httpStatus, String info) {
}

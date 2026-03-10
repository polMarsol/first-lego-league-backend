package cat.udl.eps.softarch.fll.controller.dto;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
		String error,
		String message,
		String timestamp,
		String path,
		Object details) {

	public static ApiErrorResponse of(String error, String message, String path) {
		return new ApiErrorResponse(error, message, Instant.now().toString(), path, null);
	}

	public static ApiErrorResponse of(String error, String message, String path, Object details) {
		return new ApiErrorResponse(error, message, Instant.now().toString(), path, details);
	}
}

package cat.udl.eps.softarch.fll.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import cat.udl.eps.softarch.fll.controller.dto.ApiErrorResponse;
import cat.udl.eps.softarch.fll.exception.EditionVolunteerException;

@RestControllerAdvice
public class EditionVolunteerExceptionHandler {

	@ExceptionHandler(EditionVolunteerException.class)
	public ResponseEntity<ApiErrorResponse> handleEditionVolunteerException(
			EditionVolunteerException exception,
			HttpServletRequest request) {
		HttpStatus status = resolveStatus(exception.getErrorCode());
		return ResponseEntity.status(status)
				.body(ApiErrorResponse.of(exception.getErrorCode(), exception.getMessage(), request.getRequestURI()));
	}

	private HttpStatus resolveStatus(String errorCode) {
		return switch (errorCode) {
			case "EDITION_NOT_FOUND" -> HttpStatus.NOT_FOUND;
			default -> HttpStatus.BAD_REQUEST;
		};
	}
}

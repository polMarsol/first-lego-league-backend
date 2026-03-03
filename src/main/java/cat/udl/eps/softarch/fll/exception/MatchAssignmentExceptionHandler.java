package cat.udl.eps.softarch.fll.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MatchAssignmentExceptionHandler {

	@ExceptionHandler(MatchAssignmentException.class)
	public ResponseEntity<ErrorResponse> handleMatchAssignmentException(MatchAssignmentException ex) {
		HttpStatus status = switch (ex.getErrorCode()) {
			case MATCH_NOT_FOUND, REFEREE_NOT_FOUND -> HttpStatus.NOT_FOUND;
			case AVAILABILITY_CONFLICT, MATCH_ALREADY_HAS_REFEREE -> HttpStatus.CONFLICT;
			case INVALID_ROLE, INVALID_MATCH_STATE, INVALID_ID_FORMAT -> HttpStatus.UNPROCESSABLE_CONTENT;
		};
		ErrorResponse body = new ErrorResponse(ex.getErrorCode().name(), ex.getMessage());
		return ResponseEntity.status(status).body(body);
	}

	public record ErrorResponse(String error, String message) {}
}

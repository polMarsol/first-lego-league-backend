package cat.udl.eps.softarch.fll.exception;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import cat.udl.eps.softarch.fll.controller.dto.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationException(
			MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		List<String> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.toList();
		String message = String.join("; ", errors);
		return ResponseEntity.unprocessableEntity()
				.body(ApiErrorResponse.of("VALIDATION_ERROR", message, request.getRequestURI()));
	}
}

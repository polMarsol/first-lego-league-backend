package cat.udl.eps.softarch.fll.exception;

import lombok.Getter;

@Getter
public class EditionLifecycleException extends RuntimeException {

	private final String error;

	public EditionLifecycleException(String error, String message) {
		super(message);
		this.error = error;
	}
}

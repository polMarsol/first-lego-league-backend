package cat.udl.eps.softarch.fll.exception;

public class EditionVolunteerException extends RuntimeException {

	private final String errorCode;

	public EditionVolunteerException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
}

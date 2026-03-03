package cat.udl.eps.softarch.fll.exception;

public class MatchAssignmentException extends RuntimeException {
	private final MatchAssignmentErrorCode errorCode;

	public MatchAssignmentException(MatchAssignmentErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public MatchAssignmentErrorCode getErrorCode() {
		return errorCode;
	}
}

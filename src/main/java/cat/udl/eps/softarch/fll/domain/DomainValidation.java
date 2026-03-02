package cat.udl.eps.softarch.fll.domain;

import java.util.regex.Pattern;

public final class DomainValidation {

	private static final Pattern EMAIL_PATTERN =
			Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	private DomainValidation() {}

	public static void requireNonNullId(Object id, String fieldName) {
		if (id == null) {
			throw new DomainValidationException(fieldName + " must not be null");
		}
	}

	public static void requireNonBlank(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new DomainValidationException(fieldName + " must not be blank");
		}
	}

	public static void requireValidEmail(String email, String fieldName) {
		requireNonBlank(email, fieldName);
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			throw new DomainValidationException(fieldName + " must be a valid email address");
		}
	}

	public static void requireNonNegative(Integer value, String fieldName) {
		if (value == null) {
			throw new DomainValidationException(fieldName + " must not be null");
		}
		if (value < 0) {
			throw new DomainValidationException(fieldName + " must not be negative");
		}
	}

	public static void requireNonNull(Object value, String fieldName) {
		if (value == null) {
			throw new DomainValidationException(fieldName + " must not be null");
		}
	}
}

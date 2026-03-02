package cat.udl.eps.softarch.fll.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DomainValidationTest {

	@Nested
	class RequireNonNullId {

		@Test
		void acceptsNonNull() {
			assertDoesNotThrow(() -> DomainValidation.requireNonNullId("abc", "id"));
		}

		@Test
		void rejectsNull() {
			DomainValidationException ex = assertThrows(DomainValidationException.class,
					() -> DomainValidation.requireNonNullId(null, "id"));
			assertEquals("id must not be null", ex.getMessage());
		}
	}

	@Nested
	class RequireNonBlank {

		@Test
		void acceptsNonBlank() {
			assertDoesNotThrow(() -> DomainValidation.requireNonBlank("hello", "field"));
		}

		@Test
		void rejectsNull() {
			assertThrows(DomainValidationException.class,
					() -> DomainValidation.requireNonBlank(null, "field"));
		}

		@Test
		void rejectsBlank() {
			assertThrows(DomainValidationException.class,
					() -> DomainValidation.requireNonBlank("   ", "field"));
		}

		@Test
		void rejectsEmpty() {
			assertThrows(DomainValidationException.class,
					() -> DomainValidation.requireNonBlank("", "field"));
		}
	}

	@Nested
	class RequireValidEmail {

		@Test
		void acceptsValidEmail() {
			assertDoesNotThrow(() -> DomainValidation.requireValidEmail("user@example.com", "email"));
		}

		@Test
		void rejectsNull() {
			assertThrows(DomainValidationException.class,
					() -> DomainValidation.requireValidEmail(null, "email"));
		}

		@Test
		void rejectsInvalidFormat() {
			assertThrows(DomainValidationException.class,
					() -> DomainValidation.requireValidEmail("not-an-email", "email"));
		}

		@Test
		void rejectsMissingDomain() {
			assertThrows(DomainValidationException.class,
					() -> DomainValidation.requireValidEmail("user@", "email"));
		}

		@Test
		void rejectsMissingAt() {
			assertThrows(DomainValidationException.class,
					() -> DomainValidation.requireValidEmail("userexample.com", "email"));
		}
	}

	@Nested
	class RequireNonNegative {

		@Test
		void acceptsZero() {
			assertDoesNotThrow(() -> DomainValidation.requireNonNegative(0, "score"));
		}

		@Test
		void acceptsPositive() {
			assertDoesNotThrow(() -> DomainValidation.requireNonNegative(100, "score"));
		}

		@Test
		void rejectsNull() {
			assertThrows(DomainValidationException.class,
					() -> DomainValidation.requireNonNegative(null, "score"));
		}

		@Test
		void rejectsNegative() {
			DomainValidationException ex = assertThrows(DomainValidationException.class,
					() -> DomainValidation.requireNonNegative(-1, "score"));
			assertEquals("score must not be negative", ex.getMessage());
		}
	}

	@Nested
	class RequireNonNull {

		@Test
		void acceptsNonNull() {
			assertDoesNotThrow(() -> DomainValidation.requireNonNull("value", "field"));
		}

		@Test
		void rejectsNull() {
			DomainValidationException ex = assertThrows(DomainValidationException.class,
					() -> DomainValidation.requireNonNull(null, "field"));
			assertEquals("field must not be null", ex.getMessage());
		}
	}
}

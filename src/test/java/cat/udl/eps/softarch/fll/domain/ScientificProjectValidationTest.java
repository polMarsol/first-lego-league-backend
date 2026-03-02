package cat.udl.eps.softarch.fll.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ScientificProjectValidationTest {

	@Test
	void validConstructionWithScore() {
		assertDoesNotThrow(() -> ScientificProject.create(85));
	}

	@Test
	void validConstructionWithZeroScore() {
		assertDoesNotThrow(() -> ScientificProject.create(0));
	}

	@Test
	void validConstructionWithNullScore() {
		assertDoesNotThrow(() -> ScientificProject.create(null));
	}

	@Nested
	class NegativeScore {

		@Test
		void negativeScoreThrows() {
			assertThrows(DomainValidationException.class,
					() -> ScientificProject.create(-1));
		}

		@Test
		void largeNegativeScoreThrows() {
			assertThrows(DomainValidationException.class,
					() -> ScientificProject.create(-100));
		}
	}
}

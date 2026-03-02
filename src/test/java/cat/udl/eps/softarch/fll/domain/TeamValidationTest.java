package cat.udl.eps.softarch.fll.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TeamValidationTest {

	@Test
	void validConstruction() {
		assertDoesNotThrow(() -> Team.create("Robotics", "Lleida", 2020, "Challenge"));
	}

	@Test
	void singleNameConstruction() {
		assertDoesNotThrow(() -> Team.create("Robotics"));
	}

	@Nested
	class NullId {

		@Test
		void nullNameThrows() {
			DomainValidationException ex = assertThrows(DomainValidationException.class,
					() -> Team.create(null, "Lleida", 2020, "Challenge"));
			assertEquals("name must not be blank", ex.getMessage());
		}

		@Test
		void nullNameSingleArgThrows() {
			assertThrows(DomainValidationException.class, () -> Team.create(null));
		}
	}

	@Nested
	class EmptyName {

		@Test
		void blankNameThrows() {
			assertThrows(DomainValidationException.class,
					() -> Team.create("   ", "Lleida", 2020, "Challenge"));
		}

		@Test
		void emptyNameThrows() {
			assertThrows(DomainValidationException.class,
					() -> Team.create("", "Lleida", 2020, "Challenge"));
		}

		@Test
		void blankCityThrows() {
			assertThrows(DomainValidationException.class,
					() -> Team.create("Robotics", "  ", 2020, "Challenge"));
		}

		@Test
		void blankCategoryThrows() {
			assertThrows(DomainValidationException.class,
					() -> Team.create("Robotics", "Lleida", 2020, ""));
		}
	}

	@Nested
	class NullRequiredField {

		@Test
		void nullFoundationYearThrows() {
			assertThrows(DomainValidationException.class,
					() -> Team.create("Robotics", "Lleida", null, "Challenge"));
		}
	}
}

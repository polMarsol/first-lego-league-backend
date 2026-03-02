package cat.udl.eps.softarch.fll.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class VenueValidationTest {

	@Test
	void validConstruction() {
		assertDoesNotThrow(() -> Venue.create("Lleida Arena", "Lleida"));
	}

	@Nested
	class EmptyName {

		@Test
		void nullNameThrows() {
			assertThrows(DomainValidationException.class,
					() -> Venue.create(null, "Lleida"));
		}

		@Test
		void blankNameThrows() {
			assertThrows(DomainValidationException.class,
					() -> Venue.create("  ", "Lleida"));
		}

		@Test
		void nullCityThrows() {
			assertThrows(DomainValidationException.class,
					() -> Venue.create("Lleida Arena", null));
		}

		@Test
		void blankCityThrows() {
			assertThrows(DomainValidationException.class,
					() -> Venue.create("Lleida Arena", ""));
		}
	}
}

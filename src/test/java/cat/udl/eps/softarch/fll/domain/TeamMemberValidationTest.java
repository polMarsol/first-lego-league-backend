package cat.udl.eps.softarch.fll.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TeamMemberValidationTest {

	private final Team validTeam = Team.create("TestTeam", "Barcelona", 2000, "category");

	@Test
	void validConstruction() {
		assertDoesNotThrow(() -> TeamMember.create("Alice", "Player", LocalDate.ofYearDay(0, 1), validTeam));
	}

	@Test
	void futureDateThrows() {
		assertThrows(DomainValidationException.class,
			() -> TeamMember.create("Alice", "Player", LocalDate.now().plusDays(1), validTeam));
	}

	@Nested
	class EmptyName {

		@Test
		void nullNameThrows() {
			assertThrows(DomainValidationException.class,
				() -> TeamMember.create(null, "Player", LocalDate.ofYearDay(0, 1), validTeam));
		}

		@Test
		void blankNameThrows() {
			assertThrows(DomainValidationException.class,
				() -> TeamMember.create("  ", "Player", LocalDate.ofYearDay(0, 1), validTeam));
		}

		@Test
		void blankRoleThrows() {
			assertThrows(DomainValidationException.class,
				() -> TeamMember.create("Alice", "", LocalDate.ofYearDay(0, 1), validTeam));
		}
	}

	@Nested
	class NullRequiredReference {

		@Test
		void nullTeamThrows() {
			assertThrows(DomainValidationException.class,
				() -> TeamMember.create("Alice", "Player", LocalDate.ofYearDay(0, 1), null));
		}
	}
}

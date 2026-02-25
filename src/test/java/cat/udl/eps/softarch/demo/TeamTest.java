package cat.udl.eps.softarch.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import cat.udl.eps.softarch.demo.domain.Team;
import cat.udl.eps.softarch.demo.domain.TeamMember;

class TeamTest {
	private Team team;

	@BeforeEach
	void setUp() {
		team = new Team("UdL Eagles");
		team.setCity("Igualada");
		team.setCategory("Senior");
	}

	@Test
	@DisplayName("Validate max 10 team members")
	void testMemberLimit() {
		for (int i = 0; i < 10; i++) {
			team.addMember(new TeamMember());
		}
		assertThrows(IllegalStateException.class, () -> team.addMember(new TeamMember()));
	}

	@Test
	@DisplayName("Set inscription date on prePersist when null")
	void testPrePersistDate() {
		team.setInscriptionDate(null);
		team.prePersist();
		assertNotNull(team.getInscriptionDate());
		assertEquals(LocalDate.now(), team.getInscriptionDate());
	}

	@Test
	@DisplayName("Keep existing inscription date on prePersist")
	void testPrePersistWithExistingDate() {
		LocalDate yesterday = LocalDate.now().minusDays(1);
		team.setInscriptionDate(yesterday);
		team.prePersist();
		assertEquals(yesterday, team.getInscriptionDate());
	}
}

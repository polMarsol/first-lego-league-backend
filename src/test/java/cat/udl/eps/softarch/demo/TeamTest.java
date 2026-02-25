package cat.udl.eps.softarch.demo; 

import static org.junit.jupiter.api.Assertions.*;
import cat.udl.eps.softarch.demo.domain.Team; 
import cat.udl.eps.softarch.demo.domain.TeamMember; 
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TeamTest {
	private Team team;

	@BeforeEach 
	void setUp() {
		team = new Team("UdL Eagles");
		team.setCity("Igualada");
		team.setCategory("Senior");
	}

	@Test
	@DisplayName("Validar límit de 10 membres")
	void testMemberLimit() {
		
		for (int i = 0; i < 10; i++) {
			team.addMember(new TeamMember());
		}

		assertThrows(IllegalStateException.class, () -> {
			team.addMember(new TeamMember());
		});
	}

	@Test
	@DisplayName("Verificar prePersist per a la data d'inscripció")
	void testPrePersistDate() {
		team.setInscriptionDate(null);
		team.prePersist(); 

		assertNotNull(team.getInscriptionDate());
		assertEquals(LocalDate.now(), team.getInscriptionDate());
	}

	@Test
	void testPrePersistWithExistingDate() {
		LocalDate yesterday = LocalDate.now().minusDays(1);
		team.setInscriptionDate(yesterday);
		team.prePersist();
		assertEquals(yesterday, team.getInscriptionDate());
	}
}

package cat.udl.eps.softarch.demo; // <--- Assegura't que el package coincideixi

import static org.junit.jupiter.api.Assertions.*;
import cat.udl.eps.softarch.demo.domain.Team; // <--- Afegeix l'import explícit si cal
import cat.udl.eps.softarch.demo.domain.TeamMember; // <--- Importa també el membre
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TeamTest {
	private Team team;

	@BeforeEach // S'executa abans de cada @Test [cite: 922, 1045]
	void setUp() {
		team = new Team("UdL Eagles");
		team.setCity("Igualada");
		team.setCategory("Senior");
	}

	@Test
	@DisplayName("Validar límit de 10 membres")
	void testMemberLimit() {
		// Omplim l'equip fins al límit
		for (int i = 0; i < 10; i++) {
			team.addMember(new TeamMember());
		}

		// Verifiquem que el membre 11 llança l'excepció IllegalStateException
		assertThrows(IllegalStateException.class, () -> {
			team.addMember(new TeamMember());
		});
	}

	@Test
	@DisplayName("Verificar prePersist per a la data d'inscripció")
	void testPrePersistDate() {
		team.setInscriptionDate(null);
		team.prePersist(); // Simulem l'esdeveniment de JPA [cite: 1928]

		assertNotNull(team.getInscriptionDate());
		assertEquals(LocalDate.now(), team.getInscriptionDate());
	}

	@Test
	void testPrePersistWithExistingDate() {
		LocalDate yesterday = LocalDate.now().minusDays(1);
		team.setInscriptionDate(yesterday);
		team.prePersist();
		assertEquals(yesterday, team.getInscriptionDate()); // No ha de sobreescriure-la
	}
}

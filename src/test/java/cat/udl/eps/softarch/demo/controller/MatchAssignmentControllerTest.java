package cat.udl.eps.softarch.demo.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import cat.udl.eps.softarch.demo.MainApplication;
import cat.udl.eps.softarch.demo.api.dto.AssignRefereeRequest;
import cat.udl.eps.softarch.demo.domain.Floater;
import cat.udl.eps.softarch.demo.domain.Match;
import cat.udl.eps.softarch.demo.domain.MatchState;
import cat.udl.eps.softarch.demo.domain.Referee;
import cat.udl.eps.softarch.demo.repository.FloaterRepository;
import cat.udl.eps.softarch.demo.repository.MatchRepository;
import cat.udl.eps.softarch.demo.repository.RefereeRepository;

@ContextConfiguration(classes = {MainApplication.class}, loader = SpringBootContextLoader.class)
@DirtiesContext
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ActiveProfiles("test")
class MatchAssignmentControllerTest {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private RefereeRepository refereeRepository;

	@Autowired
	private FloaterRepository floaterRepository;

	private MockMvc mockMvc;
	private ResultActions result;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(wac)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
		matchRepository.deleteAll();
		floaterRepository.deleteAll();
		refereeRepository.deleteAll();
	}

	@Test
	void assignRefereeReturns401WhenUnauthenticated() throws Exception {
		Match match = createScheduledMatch(null, LocalDateTime.of(2026, 3, 1, 10, 0));
		Referee referee = createReferee();
		AssignRefereeRequest request = new AssignRefereeRequest(match.getId().toString(), referee.getId().toString());

		result = mockMvc.perform(post("/match-assignments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)));

		result.andExpect(status().isUnauthorized());
	}

	@Test
	void assignRefereeReturnsSuccessPayload() throws Exception {
		Match match = createScheduledMatch(null, LocalDateTime.of(2026, 3, 1, 10, 0));
		Referee referee = createReferee();
		AssignRefereeRequest request = new AssignRefereeRequest(match.getId().toString(), referee.getId().toString());

		result = performAuthenticated(request);

		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.matchId").value(match.getId().toString()))
				.andExpect(jsonPath("$.refereeId").value(referee.getId().toString()))
				.andExpect(jsonPath("$.status").value("ASSIGNED"));
	}

	@Test
	void assignRefereeReturns404ForMatchNotFound() throws Exception {
		Referee referee = createReferee();
		AssignRefereeRequest request = new AssignRefereeRequest("99999", referee.getId().toString());
		result = performAuthenticated(request);
		result.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("MATCH_NOT_FOUND"));
	}

	@Test
	void assignRefereeReturns404ForRefereeNotFound() throws Exception {
		Match match = createScheduledMatch(null, LocalDateTime.of(2026, 3, 1, 10, 0));
		AssignRefereeRequest request = new AssignRefereeRequest(match.getId().toString(), "99999");
		result = performAuthenticated(request);
		result.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("REFEREE_NOT_FOUND"));
	}

	@Test
	void assignRefereeReturns409ForAvailabilityConflict() throws Exception {
		Referee referee = createReferee();
		createScheduledMatch(referee, LocalDateTime.of(2026, 3, 1, 10, 0));
		Match newMatch = createScheduledMatch(null, LocalDateTime.of(2026, 3, 1, 10, 30));

		AssignRefereeRequest request = new AssignRefereeRequest(newMatch.getId().toString(), referee.getId().toString());
		result = performAuthenticated(request);
		result.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("AVAILABILITY_CONFLICT"));
	}

	@Test
	void assignRefereeReturns409ForExistingMatchReferee() throws Exception {
		Referee firstReferee = createReferee();
		Referee secondReferee = createReferee();
		Match match = createScheduledMatch(firstReferee, LocalDateTime.of(2026, 3, 1, 10, 0));

		AssignRefereeRequest request = new AssignRefereeRequest(match.getId().toString(), secondReferee.getId().toString());
		result = performAuthenticated(request);
		result.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("MATCH_ALREADY_HAS_REFEREE"));
	}

	@Test
	void assignRefereeReturns422ForInvalidRole() throws Exception {
		Match match = createScheduledMatch(null, LocalDateTime.of(2026, 3, 1, 10, 0));
		Floater floater = createFloater();

		AssignRefereeRequest request = new AssignRefereeRequest(match.getId().toString(), floater.getId().toString());
		result = performAuthenticated(request);
		result.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.error").value("INVALID_ROLE"));
	}

	@Test
	void assignRefereeReturns422ForInvalidMatchState() throws Exception {
		Match finishedMatch = createFinishedMatch();
		Referee referee = createReferee();

		AssignRefereeRequest request = new AssignRefereeRequest(
				finishedMatch.getId().toString(),
				referee.getId().toString());
		result = performAuthenticated(request);
		result.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.error").value("INVALID_MATCH_STATE"));
	}

	@Test
	void assignRefereeReturns422ForInvalidIdFormat() throws Exception {
		AssignRefereeRequest request = new AssignRefereeRequest("invalid", "2");
		result = performAuthenticated(request);
		result.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.error").value("INVALID_ID_FORMAT"));
	}

	private ResultActions performAuthenticated(AssignRefereeRequest request) throws Exception {
		return mockMvc.perform(post("/match-assignments")
				.with(httpBasic("demo", "password"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)));
	}

	private Match createScheduledMatch(Referee referee, LocalDateTime startTime) {
		Match match = new Match();
		match.setStartTime(startTime);
		match.setEndTime(startTime.plusHours(1));
		match.setState(MatchState.SCHEDULED);
		match.setReferee(referee);
		return matchRepository.save(match);
	}

	private Match createFinishedMatch() {
		Match match = new Match();
		match.setStartTime(LocalDateTime.of(2026, 3, 1, 10, 0));
		match.setEndTime(LocalDateTime.of(2026, 3, 1, 11, 0));
		match.setState(MatchState.FINISHED);
		return matchRepository.save(match);
	}

	private Referee createReferee() {
		Referee referee = new Referee();
		referee.setName("Referee");
		referee.setPhoneNumber("123456789");
		referee.setEmailAddress("ref" + System.nanoTime() + "@mail.com");
		return refereeRepository.save(referee);
	}

	private Floater createFloater() {
		Floater floater = new Floater();
		floater.setName("Floater");
		floater.setPhoneNumber("987654321");
		floater.setEmailAddress("floater" + System.nanoTime() + "@mail.com");
		floater.setStudentCode("F" + System.nanoTime());
		return floaterRepository.save(floater);
	}
}

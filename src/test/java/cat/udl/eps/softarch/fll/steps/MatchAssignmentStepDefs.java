package cat.udl.eps.softarch.fll.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.time.LocalTime;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import cat.udl.eps.softarch.fll.domain.Floater;
import cat.udl.eps.softarch.fll.domain.Match;
import cat.udl.eps.softarch.fll.domain.MatchState;
import cat.udl.eps.softarch.fll.domain.Referee;
import cat.udl.eps.softarch.fll.repository.FloaterRepository;
import cat.udl.eps.softarch.fll.repository.MatchRepository;
import cat.udl.eps.softarch.fll.repository.MatchResultRepository;
import cat.udl.eps.softarch.fll.repository.RefereeRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class MatchAssignmentStepDefs {

	private final StepDefs stepDefs;
	private final MatchRepository matchRepository;
	private final RefereeRepository refereeRepository;
	private final FloaterRepository floaterRepository;
	private final MatchResultRepository matchResultRepository;

	private Match currentMatch;
	private Referee currentReferee;
	private Referee firstReferee;
	private Referee secondReferee;
	private Floater currentFloater;

	public MatchAssignmentStepDefs(
			StepDefs stepDefs,
			MatchRepository matchRepository,
			RefereeRepository refereeRepository,
			FloaterRepository floaterRepository,
			MatchResultRepository matchResultRepository) {
		this.stepDefs = stepDefs;
		this.matchRepository = matchRepository;
		this.refereeRepository = refereeRepository;
		this.floaterRepository = floaterRepository;
		this.matchResultRepository = matchResultRepository;
	}

	@Given("the match assignment system is empty")
	public void clearMatchAssignmentSystem() {
		matchResultRepository.deleteAll();
		matchRepository.deleteAll();
		floaterRepository.deleteAll();
		refereeRepository.deleteAll();
		currentMatch = null;
		currentReferee = null;
		firstReferee = null;
		secondReferee = null;
		currentFloater = null;
	}

	@Given("a scheduled match exists from {string} to {string}")
	public void createScheduledMatch(String startTime, String endTime) {
		currentMatch = createMatch(MatchState.SCHEDULED, startTime, endTime, null);
	}

	@Given("a finished match exists from {string} to {string}")
	public void createFinishedMatch(String startTime, String endTime) {
		currentMatch = createMatch(MatchState.FINISHED, startTime, endTime, null);
	}

	@Given("a referee volunteer exists")
	public void createReferee() {
		Referee referee = new Referee();
		referee.setName("Referee One");
		referee.setEmailAddress("ref" + System.nanoTime() + "@mail.com");
		referee.setPhoneNumber("123456789");
		currentReferee = refereeRepository.save(referee);
		if (firstReferee == null) {
			firstReferee = currentReferee;
		}
	}

	@Given("another referee volunteer exists")
	public void createAnotherReferee() {
		Referee referee = new Referee();
		referee.setName("Referee Two");
		referee.setEmailAddress("ref" + System.nanoTime() + "@mail.com");
		referee.setPhoneNumber("987654321");
		secondReferee = refereeRepository.save(referee);
	}

	@Given("a floater volunteer exists")
	public void createFloater() {
		Floater floater = new Floater();
		floater.setName("Floater One");
		floater.setEmailAddress("floater" + System.nanoTime() + "@mail.com");
		floater.setPhoneNumber("555555555");
		floater.setStudentCode("F" + System.nanoTime());
		currentFloater = floaterRepository.save(floater);
	}

	@Given("a scheduled match exists from {string} to {string} assigned to the first referee")
	public void createScheduledMatchAssignedToFirstReferee(String startTime, String endTime) {
		currentMatch = createMatch(MatchState.SCHEDULED, startTime, endTime, firstReferee);
	}

	@Given("a scheduled match exists from {string} to {string} assigned to the referee")
	public void createScheduledMatchAssignedToReferee(String startTime, String endTime) {
		createMatch(MatchState.SCHEDULED, startTime, endTime, currentReferee);
	}

	@When("I assign that referee to that match")
	public void assignCurrentRefereeToCurrentMatch() throws Exception {
		assign(currentMatch.getId().toString(), currentReferee.getId().toString());
	}

	@When("I assign that floater to that match")
	public void assignCurrentFloaterToCurrentMatch() throws Exception {
		assign(currentMatch.getId().toString(), currentFloater.getId().toString());
	}

	@When("I assign the second referee to that match")
	public void assignSecondRefereeToCurrentMatch() throws Exception {
		assign(currentMatch.getId().toString(), secondReferee.getId().toString());
	}

	@When("I assign referee id {string} to match id {string}")
	public void assignByIds(String refereeId, String matchId) throws Exception {
		assign(matchId, refereeId);
	}

	@When("I assign referee id {string} to that match")
	public void assignRefereeIdToCurrentMatch(String refereeId) throws Exception {
		assign(currentMatch.getId().toString(), refereeId);
	}

	@Then("assignment response status is {string}")
	public void verifyAssignmentStatus(String expectedStatus) throws Exception {
		stepDefs.result.andExpect(jsonPath("$.status").value(expectedStatus));
	}

	@And("assignment error code is {string}")
	public void verifyAssignmentErrorCode(String expectedErrorCode) throws Exception {
		stepDefs.result.andExpect(jsonPath("$.error").value(expectedErrorCode));
	}

	@And("the match is assigned to that referee")
	public void verifyMatchAssignedToReferee() {
		Match persisted = matchRepository.findById(currentMatch.getId()).orElseThrow();
		assertNotNull(persisted.getReferee());
		assertEquals(currentReferee.getId(), persisted.getReferee().getId());
	}

	private Match createMatch(MatchState state, String startTime, String endTime, Referee referee) {
		Match match = new Match();
		match.setState(state);
		match.setStartTime(LocalTime.parse(startTime));
		match.setEndTime(LocalTime.parse(endTime));
		match.setReferee(referee);
		return matchRepository.save(match);
	}

	private void assign(String matchId, String refereeId) throws Exception {
		JSONObject request = new JSONObject();
		request.put("matchId", matchId);
		request.put("refereeId", refereeId);
		stepDefs.result = stepDefs.mockMvc.perform(
				post("/matchAssignments/assign")
						.with(AuthenticationStepDefs.authenticate())
						.contentType(MediaType.APPLICATION_JSON)
						.content(request.toString())
						.accept(MediaType.APPLICATION_JSON));
	}
}

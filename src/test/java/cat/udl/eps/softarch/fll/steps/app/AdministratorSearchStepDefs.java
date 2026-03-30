package cat.udl.eps.softarch.fll.steps.app;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;

public class AdministratorSearchStepDefs {

	private final StepDefs stepDefs;

	public AdministratorSearchStepDefs(StepDefs stepDefs) {
		this.stepDefs = stepDefs;
	}

	@When("I search administrators by username containing {string}")
	public void iSearchAdministratorsByUsernameContaining(String text) throws Exception {
		stepDefs.result = stepDefs.mockMvc.perform(get("/administrators/search/findByIdContaining")
				.param("text", text)
				.accept(MediaType.APPLICATION_JSON)
				.with(AuthenticationStepDefs.authenticate()))
			.andDo(print());
	}

	@And("^the administrators search response should contain (\\d+) result[s]?$")
	public void administratorsSearchResponseShouldContainCount(int expectedCount) throws Exception {
		String responseBody = stepDefs.result.andReturn().getResponse().getContentAsString();
		JsonNode root = stepDefs.mapper.readTree(responseBody);
		JsonNode administrators = root.path("_embedded").path("administrators");
		int actualCount = administrators.isArray() ? administrators.size() : 0;
		assertEquals(expectedCount, actualCount);
	}

	@And("the administrators search response should include administrator with username {string}")
	public void administratorsSearchResponseShouldIncludeAdministratorWithUsername(String username) throws Exception {
		stepDefs.result.andExpect(jsonPath("$._embedded.administrators[*].username", hasItem(username)));
	}
}

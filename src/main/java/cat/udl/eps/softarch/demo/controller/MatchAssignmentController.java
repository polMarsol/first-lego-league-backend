package cat.udl.eps.softarch.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import cat.udl.eps.softarch.demo.api.dto.AssignRefereeRequest;
import cat.udl.eps.softarch.demo.api.dto.AssignRefereeResponse;
import cat.udl.eps.softarch.demo.service.MatchAssignmentService;
import jakarta.validation.Valid;

@Validated
@RestController
public class MatchAssignmentController {

	private final MatchAssignmentService matchAssignmentService;

	public MatchAssignmentController(MatchAssignmentService matchAssignmentService) {
		this.matchAssignmentService = matchAssignmentService;
	}

	@PostMapping("/match-assignments")
	@PreAuthorize("isAuthenticated()")
	public AssignRefereeResponse assignReferee(@Valid @RequestBody AssignRefereeRequest request) {
		return matchAssignmentService.assignReferee(request.matchId(), request.refereeId());
	}
}

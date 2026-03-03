package cat.udl.eps.softarch.fll.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cat.udl.eps.softarch.fll.domain.Match;
import cat.udl.eps.softarch.fll.domain.MatchState;
import cat.udl.eps.softarch.fll.domain.Referee;
import cat.udl.eps.softarch.fll.domain.Volunteer;
import cat.udl.eps.softarch.fll.exception.MatchAssignmentErrorCode;
import cat.udl.eps.softarch.fll.exception.MatchAssignmentException;
import cat.udl.eps.softarch.fll.repository.MatchRepository;
import cat.udl.eps.softarch.fll.repository.VolunteerRepository;

@Service
public class MatchAssignmentService {
	private final MatchRepository matchRepository;
	private final VolunteerRepository volunteerRepository;

	public MatchAssignmentService(MatchRepository matchRepository, VolunteerRepository volunteerRepository) {
		this.matchRepository = matchRepository;
		this.volunteerRepository = volunteerRepository;
	}

	@Transactional
	public Match assignReferee(String matchId, String refereeId) {
		Long parsedMatchId = parseIdOrThrow(matchId);
		Long parsedRefereeId = parseIdOrThrow(refereeId);

		Match match = matchRepository.findByIdForUpdate(parsedMatchId)
				.orElseThrow(() -> new MatchAssignmentException(
						MatchAssignmentErrorCode.MATCH_NOT_FOUND, "Match not found: " + matchId));

		if (match.getReferee() != null) {
			throw new MatchAssignmentException(
					MatchAssignmentErrorCode.MATCH_ALREADY_HAS_REFEREE,
					"Match already has a referee assigned");
		}

		if (match.getState() == null || match.getState() == MatchState.FINISHED
				|| match.getStartTime() == null || match.getEndTime() == null) {
			throw new MatchAssignmentException(
					MatchAssignmentErrorCode.INVALID_MATCH_STATE,
					"Match is not in a valid state for referee assignment");
		}

		Volunteer volunteer = volunteerRepository.findByIdForUpdate(parsedRefereeId)
				.orElseThrow(() -> new MatchAssignmentException(
						MatchAssignmentErrorCode.REFEREE_NOT_FOUND, "Referee not found: " + refereeId));

		if (!(volunteer instanceof Referee referee)) {
			throw new MatchAssignmentException(
					MatchAssignmentErrorCode.INVALID_ROLE,
					"Volunteer does not have the Referee role");
		}

		List<Match> conflicts = matchRepository.findOverlappingAssignments(
				referee, match.getStartTime(), match.getEndTime(), match.getId());
		if (!conflicts.isEmpty()) {
			throw new MatchAssignmentException(
					MatchAssignmentErrorCode.AVAILABILITY_CONFLICT,
					"Referee is already assigned to another overlapping match");
		}

		match.setReferee(referee);
		return matchRepository.save(match);
	}

	private Long parseIdOrThrow(String value) {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException ex) {
			throw new MatchAssignmentException(
					MatchAssignmentErrorCode.INVALID_ID_FORMAT, "Invalid ID format: " + value);
		}
	}
}

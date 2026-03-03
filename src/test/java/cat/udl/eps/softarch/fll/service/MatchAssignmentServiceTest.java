package cat.udl.eps.softarch.fll.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import cat.udl.eps.softarch.fll.domain.Floater;
import cat.udl.eps.softarch.fll.domain.Match;
import cat.udl.eps.softarch.fll.domain.MatchState;
import cat.udl.eps.softarch.fll.domain.Referee;
import cat.udl.eps.softarch.fll.exception.MatchAssignmentErrorCode;
import cat.udl.eps.softarch.fll.exception.MatchAssignmentException;
import cat.udl.eps.softarch.fll.repository.MatchRepository;
import cat.udl.eps.softarch.fll.repository.VolunteerRepository;

@ExtendWith(MockitoExtension.class)
class MatchAssignmentServiceTest {

	@Mock
	private MatchRepository matchRepository;
	@Mock
	private VolunteerRepository volunteerRepository;

	private MatchAssignmentService service;

	@BeforeEach
	void setUp() {
		service = new MatchAssignmentService(matchRepository, volunteerRepository);
	}

	@Test
	void assignRefereeSuccess() {
		Match match = buildMatch(10L, MatchState.SCHEDULED, null);
		Referee referee = buildReferee(20L);

		when(matchRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(match));
		when(volunteerRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(referee));
		when(matchRepository.findOverlappingAssignments(
				referee, match.getStartTime(), match.getEndTime(), match.getId())).thenReturn(List.of());
		when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Match result = service.assignReferee("10", "20");

		assertSame(referee, result.getReferee());
		verify(matchRepository).save(match);
	}

	@Test
	void assignRefereeFailsWhenMatchNotFound() {
		when(matchRepository.findByIdForUpdate(10L)).thenReturn(Optional.empty());

		MatchAssignmentException ex = assertThrows(
				MatchAssignmentException.class, () -> service.assignReferee("10", "20"));

		assertEquals(MatchAssignmentErrorCode.MATCH_NOT_FOUND, ex.getErrorCode());
	}

	@Test
	void assignRefereeFailsWhenRefereeNotFound() {
		Match match = buildMatch(10L, MatchState.SCHEDULED, null);
		when(matchRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(match));
		when(volunteerRepository.findByIdForUpdate(20L)).thenReturn(Optional.empty());

		MatchAssignmentException ex = assertThrows(
				MatchAssignmentException.class, () -> service.assignReferee("10", "20"));

		assertEquals(MatchAssignmentErrorCode.REFEREE_NOT_FOUND, ex.getErrorCode());
	}

	@Test
	void assignRefereeFailsWhenVolunteerIsNotReferee() {
		Match match = buildMatch(10L, MatchState.SCHEDULED, null);
		Floater floater = new Floater();
		floater.setId(20L);

		when(matchRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(match));
		when(volunteerRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(floater));

		MatchAssignmentException ex = assertThrows(
				MatchAssignmentException.class, () -> service.assignReferee("10", "20"));

		assertEquals(MatchAssignmentErrorCode.INVALID_ROLE, ex.getErrorCode());
	}

	@Test
	void assignRefereeFailsWhenAvailabilityConflict() {
		Match match = buildMatch(10L, MatchState.SCHEDULED, null);
		Referee referee = buildReferee(20L);
		Match overlapping = buildMatch(11L, MatchState.SCHEDULED, referee);

		when(matchRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(match));
		when(volunteerRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(referee));
		when(matchRepository.findOverlappingAssignments(
				referee, match.getStartTime(), match.getEndTime(), match.getId())).thenReturn(List.of(overlapping));

		MatchAssignmentException ex = assertThrows(
				MatchAssignmentException.class, () -> service.assignReferee("10", "20"));

		assertEquals(MatchAssignmentErrorCode.AVAILABILITY_CONFLICT, ex.getErrorCode());
	}

	@Test
	void assignRefereeFailsWhenMatchAlreadyHasReferee() {
		Referee assigned = buildReferee(99L);
		Match match = buildMatch(10L, MatchState.SCHEDULED, assigned);

		when(matchRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(match));

		MatchAssignmentException ex = assertThrows(
				MatchAssignmentException.class, () -> service.assignReferee("10", "20"));

		assertEquals(MatchAssignmentErrorCode.MATCH_ALREADY_HAS_REFEREE, ex.getErrorCode());
	}

	@Test
	void assignRefereeFailsWhenMatchStateIsInvalid() {
		Match match = buildMatch(10L, MatchState.FINISHED, null);
		when(matchRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(match));

		MatchAssignmentException ex = assertThrows(
				MatchAssignmentException.class, () -> service.assignReferee("10", "20"));

		assertEquals(MatchAssignmentErrorCode.INVALID_MATCH_STATE, ex.getErrorCode());
	}

	@Test
	void assignRefereeFailsWhenMatchIdIsInvalidFormat() {
		MatchAssignmentException ex = assertThrows(
				MatchAssignmentException.class, () -> service.assignReferee("abc", "20"));

		assertEquals(MatchAssignmentErrorCode.INVALID_ID_FORMAT, ex.getErrorCode());
	}

	private Match buildMatch(Long id, MatchState state, Referee referee) {
		Match match = new Match();
		match.setId(id);
		match.setStartTime(LocalTime.of(10, 0));
		match.setEndTime(LocalTime.of(11, 0));
		match.setState(state);
		match.setReferee(referee);
		return match;
	}

	private Referee buildReferee(Long id) {
		Referee referee = new Referee();
		referee.setId(id);
		return referee;
	}
}


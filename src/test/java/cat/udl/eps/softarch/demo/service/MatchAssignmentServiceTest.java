package cat.udl.eps.softarch.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import cat.udl.eps.softarch.demo.api.dto.AssignRefereeResponse;
import cat.udl.eps.softarch.demo.domain.Floater;
import cat.udl.eps.softarch.demo.domain.Match;
import cat.udl.eps.softarch.demo.domain.MatchState;
import cat.udl.eps.softarch.demo.domain.Referee;
import cat.udl.eps.softarch.demo.exception.AssignmentErrorCode;
import cat.udl.eps.softarch.demo.exception.AssignmentValidationException;
import cat.udl.eps.softarch.demo.repository.MatchRepository;
import cat.udl.eps.softarch.demo.repository.VolunteerRepository;

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
		Match match = newScheduledMatch(1L, LocalDateTime.of(2026, 3, 1, 10, 0), LocalDateTime.of(2026, 3, 1, 11, 0));
		Referee referee = newReferee(2L);

		when(matchRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(match));
		when(volunteerRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(referee));
		when(matchRepository.existsOverlappingAssignment(2L, match.getStartTime(), match.getEndTime())).thenReturn(false);

		AssignRefereeResponse response = service.assignReferee("1", "2");

		assertEquals("1", response.matchId());
		assertEquals("2", response.refereeId());
		assertEquals("ASSIGNED", response.status());
		assertEquals(referee, match.getReferee());
		verify(matchRepository).findByIdForUpdate(1L);
		verify(matchRepository).save(match);
	}

	@Test
	void assignRefereeFailsWhenMatchNotFound() {
		when(matchRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

		AssignmentValidationException ex = assertThrows(
				AssignmentValidationException.class,
				() -> service.assignReferee("1", "2"));

		assertEquals(AssignmentErrorCode.MATCH_NOT_FOUND, ex.getErrorCode());
	}

	@Test
	void assignRefereeFailsWhenRefereeNotFound() {
		Match match = newScheduledMatch(1L, LocalDateTime.of(2026, 3, 1, 10, 0), LocalDateTime.of(2026, 3, 1, 11, 0));
		when(matchRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(match));
		when(volunteerRepository.findByIdForUpdate(2L)).thenReturn(Optional.empty());

		AssignmentValidationException ex = assertThrows(
				AssignmentValidationException.class,
				() -> service.assignReferee("1", "2"));

		assertEquals(AssignmentErrorCode.REFEREE_NOT_FOUND, ex.getErrorCode());
		verify(matchRepository, never()).save(match);
	}

	@Test
	void assignRefereeFailsWhenVolunteerIsNotReferee() {
		Match match = newScheduledMatch(1L, LocalDateTime.of(2026, 3, 1, 10, 0), LocalDateTime.of(2026, 3, 1, 11, 0));
		Floater floater = new Floater();
		floater.setId(2L);

		when(matchRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(match));
		when(volunteerRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(floater));

		AssignmentValidationException ex = assertThrows(
				AssignmentValidationException.class,
				() -> service.assignReferee("1", "2"));

		assertEquals(AssignmentErrorCode.INVALID_ROLE, ex.getErrorCode());
	}

	@Test
	void assignRefereeFailsWhenMatchAlreadyHasReferee() {
		Match match = newScheduledMatch(1L, LocalDateTime.of(2026, 3, 1, 10, 0), LocalDateTime.of(2026, 3, 1, 11, 0));
		match.setReferee(newReferee(5L));
		Referee referee = newReferee(2L);

		when(matchRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(match));
		when(volunteerRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(referee));

		AssignmentValidationException ex = assertThrows(
				AssignmentValidationException.class,
				() -> service.assignReferee("1", "2"));

		assertEquals(AssignmentErrorCode.MATCH_ALREADY_HAS_REFEREE, ex.getErrorCode());
	}

	@Test
	void assignRefereeFailsWhenMatchStateIsInvalid() {
		Match match = newScheduledMatch(1L, LocalDateTime.of(2026, 3, 1, 10, 0), LocalDateTime.of(2026, 3, 1, 11, 0));
		match.setState(MatchState.FINISHED);
		Referee referee = newReferee(2L);

		when(matchRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(match));
		when(volunteerRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(referee));

		AssignmentValidationException ex = assertThrows(
				AssignmentValidationException.class,
				() -> service.assignReferee("1", "2"));

		assertEquals(AssignmentErrorCode.INVALID_MATCH_STATE, ex.getErrorCode());
	}

	@Test
	void assignRefereeFailsWhenAvailabilityConflicts() {
		Match match = newScheduledMatch(1L, LocalDateTime.of(2026, 3, 1, 10, 0), LocalDateTime.of(2026, 3, 1, 11, 0));
		Referee referee = newReferee(2L);

		when(matchRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(match));
		when(volunteerRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(referee));
		when(matchRepository.existsOverlappingAssignment(2L, match.getStartTime(), match.getEndTime())).thenReturn(true);

		AssignmentValidationException ex = assertThrows(
				AssignmentValidationException.class,
				() -> service.assignReferee("1", "2"));

		assertEquals(AssignmentErrorCode.AVAILABILITY_CONFLICT, ex.getErrorCode());
	}

	@Test
	void assignRefereeFailsWhenIdFormatIsInvalid() {
		AssignmentValidationException ex = assertThrows(
				AssignmentValidationException.class,
				() -> service.assignReferee("invalid", "2"));

		assertEquals(AssignmentErrorCode.INVALID_ID_FORMAT, ex.getErrorCode());
	}

	private Match newScheduledMatch(Long id, LocalDateTime startTime, LocalDateTime endTime) {
		Match match = new Match();
		match.setId(id);
		match.setStartTime(startTime);
		match.setEndTime(endTime);
		match.setState(MatchState.SCHEDULED);
		return match;
	}

	private Referee newReferee(Long id) {
		Referee referee = new Referee();
		referee.setId(id);
		referee.setName("Referee " + id);
		referee.setEmailAddress("ref" + id + "@mail.com");
		referee.setPhoneNumber("123456789");
		return referee;
	}
}

package cat.udl.eps.softarch.demo.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import jakarta.persistence.LockModeType;
import cat.udl.eps.softarch.demo.domain.Match;

@RepositoryRestResource
public interface MatchRepository extends CrudRepository<Match, Long>, PagingAndSortingRepository<Match, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT m FROM Match m WHERE m.id = :id")
	@RestResource(exported = false)
	Optional<Match> findByIdForUpdate(@Param("id") Long id);

	@Query("""
			SELECT COUNT(m) > 0
			FROM Match m
			WHERE m.referee.id = :refereeId
			AND m.startTime < :endTime
			AND :startTime < m.endTime
			""")
	boolean existsOverlappingAssignment(
			@Param("refereeId") Long refereeId,
			@Param("startTime") LocalDateTime startTime,
			@Param("endTime") LocalDateTime endTime);
}

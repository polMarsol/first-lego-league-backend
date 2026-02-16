package cat.udl.eps.softarch.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import cat.udl.eps.softarch.demo.domain.TeamMember;
import java.util.List;

@RepositoryRestResource(path = "members")
public interface TeamMemberRepository extends CrudRepository<TeamMember, Long>, PagingAndSortingRepository<TeamMember, Long> {
	List<TeamMember> findByName(@Param("name") String name);
}


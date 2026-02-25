package cat.udl.eps.softarch.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import cat.udl.eps.softarch.demo.domain.Referee;

@RepositoryRestResource
public interface RefereeRepository extends CrudRepository<Referee, Long>, PagingAndSortingRepository<Referee, Long> {

	@Override
	@RestResource(exported = false)
	<S extends Referee> S save(S entity);

	@Override
	@RestResource(exported = false)
	void delete(Referee entity);

	@Override
	@RestResource(exported = false)
	void deleteById(Long id);
}

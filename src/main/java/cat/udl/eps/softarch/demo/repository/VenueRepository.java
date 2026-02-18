package cat.udl.eps.softarch.demo.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import cat.udl.eps.softarch.demo.domain.Venue;

@RepositoryRestResource
public interface VenueRepository extends CrudRepository<Venue, Long>, PagingAndSortingRepository<Venue, Long> {

	List<Venue> findAll();

	Optional<Venue> findByName(String name);

}

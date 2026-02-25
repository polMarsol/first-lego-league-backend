package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Edition;
import cat.udl.eps.softarch.demo.domain.MediaContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@Tag(name = "MediaContents", description = "Repository for managing MediaContent entities")
@RepositoryRestResource
public interface MediaContentRepository extends CrudRepository<MediaContent, String>, PagingAndSortingRepository<MediaContent, String> {

	@Operation(summary = "Search media content by type",
			description = "Returns a list of MediaContent with the specified type.")
	List<MediaContent> findByType(@Param("type") String type);

	@Operation(summary = "Search media content by edition",
			description = "Returns a list of MediaContent belonging to the specified edition.")
	List<MediaContent> findByEdition(@Param("edition") Edition edition);

}

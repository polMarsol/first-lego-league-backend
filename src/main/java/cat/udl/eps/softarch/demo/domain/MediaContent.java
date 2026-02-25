package cat.udl.eps.softarch.demo.domain;

import org.jspecify.annotations.Nullable;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class MediaContent extends UriEntity<String> {

	@Id
	@NotBlank
	private String url;

	@NotBlank
	@Column(name = "media_type")
	private String type;

	@ManyToOne
	@JsonIdentityReference(alwaysAsId = true)
	private Edition edition;

	@Override
	public @Nullable String getId() {
		return url;
	}
}

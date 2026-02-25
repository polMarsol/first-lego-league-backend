package cat.udl.eps.softarch.demo.domain;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "floaters")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Floater extends Volunteer {


	@NotBlank(message = "Student code is mandatory")
	@Column(unique = true)
	private String studentCode;

	@ManyToMany(mappedBy = "floaters")
	@ToString.Exclude
	private Set<Team> assistedTeams = new HashSet<>();
}


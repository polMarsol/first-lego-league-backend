package cat.udl.eps.softarch.demo.domain;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
		name = "competition_match",
		indexes = @Index(
				name = "idx_competition_match_referee_start_end",
				columnList = "referee_id,start_time,end_time"))
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Match extends UriEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime startTime;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime endTime;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MatchState state;

	@ManyToOne
	@JoinColumn(name = "referee_id")
	private Referee referee;
}

package ch.dvbern.ebegu.entities;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Entitaet zum Speichern von Fall in der Datenbank.
 */
@Audited
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = "fallNummer", name = "UK_fall_nummer"),
	indexes = @Index(name = "IX_fall_fall_nummer", columnList = "fallNummer")
)
public class Fall extends AbstractEntity {

	private static final long serialVersionUID = -9154456879261811678L;

//	@GeneratedValue
//	@Column(unique = true, nullable = false, updatable = false)
//	private Long fallNummer;

	@NotNull
	@Generated(GenerationTime.INSERT)
	@Column(columnDefinition = "integer auto_increment")
	private int fallNummer;

	public int getFallNummer() {
		return fallNummer;
	}

	public void setFallNummer(int fallNummer) {
		this.fallNummer = fallNummer;
	}
}

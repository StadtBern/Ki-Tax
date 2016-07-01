package ch.dvbern.ebegu.entities;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
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

	@NotNull
	@Generated(GenerationTime.INSERT)
	@Column(columnDefinition = "integer auto_increment")
	private int fallNummer;

	@Nullable
	@ManyToOne(optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_fall_verantwortlicher_id"))
	private Benutzer verantwortlicher = null;



	public int getFallNummer() {
		return fallNummer;
	}

	public void setFallNummer(int fallNummer) {
		this.fallNummer = fallNummer;
	}

	@Nullable
	public Benutzer getVerantwortlicher() {
		return verantwortlicher;
	}

	public void setVerantwortlicher(@Nullable Benutzer verantwortlicher) {
		this.verantwortlicher = verantwortlicher;
	}
}

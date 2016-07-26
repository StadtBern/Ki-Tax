package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Entitaet zum Speichern von Traegerschaft in der Datenbank.
 */
@Audited
@Entity
public class Traegerschaft extends AbstractEntity {

	private static final long serialVersionUID = -8403454439884704618L;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String name;

	@NotNull
	@Column(nullable = false)
	private Boolean active = true;

	public Traegerschaft() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}

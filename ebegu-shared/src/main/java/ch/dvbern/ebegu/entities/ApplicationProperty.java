package ch.dvbern.ebegu.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.envers.Audited;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;
import static ch.dvbern.ebegu.util.Constants.DB_TEXTAREA_LENGTH;

/**
 * Entitaet zum Speichern von diversen Applikationsproperties in der Datenbank.
 */
@Audited
@Entity
public class ApplicationProperty extends AbstractEntity {

	private static final long serialVersionUID = -7687645920282879260L;
	@Size(max = DB_DEFAULT_MAX_LENGTH)
	@NotNull
	@Column(nullable = false, length = DB_DEFAULT_MAX_LENGTH, unique = true)
	private String name;

	@Size(max = DB_TEXTAREA_LENGTH)
	@NotNull
	@Column(nullable = false, length = DB_TEXTAREA_LENGTH)
	private String value;


	public ApplicationProperty() {
	}

	public ApplicationProperty(final String key, final String value) {
		this.name = key;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

}

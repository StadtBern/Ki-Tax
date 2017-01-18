package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.ApplicationPropertyKey;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;
import static ch.dvbern.ebegu.util.Constants.DB_TEXTAREA_LENGTH;

/**
 * Entitaet zum Speichern von diversen Applikationsproperties in der Datenbank.
 */
@Audited
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = "name", name = "UK_application_property_name")
)
public class ApplicationProperty extends AbstractEntity {

	private static final long serialVersionUID = -7687645920282879260L;
	@NotNull
	@Column(nullable = false, length = DB_DEFAULT_MAX_LENGTH)
	@Enumerated(EnumType.STRING)
	private ApplicationPropertyKey name;

	@Size(max = DB_TEXTAREA_LENGTH)
	@NotNull
	@Column(nullable = false, length = DB_TEXTAREA_LENGTH)
	private String value;


	public ApplicationProperty() {
	}

	public ApplicationProperty(final ApplicationPropertyKey key, final String value) {
		this.name = key;
		this.value = value;
	}

	public ApplicationPropertyKey getName() {
		return name;
	}

	public void setName(final ApplicationPropertyKey name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

}

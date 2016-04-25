package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Entitaet zum Speichern von Institution in der Datenbank.
 */
@Audited
@Entity
public class Institution extends AbstractEntity {

	private static final long serialVersionUID = -8406487439884760618L;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String name;

	@ManyToOne(optional = false)
	private Traegerschaft traegerschaft;

	@ManyToOne(optional = false)
	private Mandant mandant;

}

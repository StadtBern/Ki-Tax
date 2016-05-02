package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.enterprise.inject.Default;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity fuer Kinder.
 */
@Audited
@Entity
public class Kind extends AbstractPersonEntity {

	private static final long serialVersionUID = -9032257320578372570L;

	@Max(100)
	@Min(0)
	@NotNull
	private Integer wohnhaftImGleichenHaushalt;

	@Nullable
	@Default
	private Boolean unterstuetzungspflicht = false;

	@NotNull
	private Boolean familienErgaenzendeBetreuung = false;

	@Nullable
	private Boolean mutterspracheDeutsch;

	@ManyToOne(optional = true)
	private Fachstelle fachstelle;

	@Max(100)
	@Min(0)
	@Nullable
	private Integer betreuungspensumFachstelle;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String bemerkungen;

}

package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.types.DateRange;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

/**
 * Abstrakte Entitaet. Muss von Entitaeten erweitert werden, die eine Periode (DateRange) mit datumVon und datumBis haben.
 */
@MappedSuperclass
@Audited
public class AbstractDateRangedEntity extends AbstractEntity {

	private static final long serialVersionUID = -7541083148864749528L;

	@Nonnull
	@Embedded
	private DateRange gueltigkeit = new DateRange();

	@Nonnull
	public DateRange getGueltigkeit() {
		return gueltigkeit;
	}

	public void setGueltigkeit(@Nonnull DateRange gueltigkeit) {
		this.gueltigkeit = gueltigkeit;
	}

}

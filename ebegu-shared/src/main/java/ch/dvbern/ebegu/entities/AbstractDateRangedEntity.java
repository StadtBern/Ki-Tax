package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Gueltigkeit;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.validation.Valid;
import java.util.Objects;

/**
 * Abstrakte Entitaet. Muss von Entitaeten erweitert werden, die eine Periode (DateRange) mit datumVon und datumBis haben.
 */
@MappedSuperclass
@Audited
public class AbstractDateRangedEntity extends AbstractEntity implements Gueltigkeit {

	private static final long serialVersionUID = -7541083148864749528L;

	@Nonnull
	@Embedded
	@Valid
	private DateRange gueltigkeit = new DateRange();

	public AbstractDateRangedEntity() {
	}

	public AbstractDateRangedEntity(@Nonnull AbstractDateRangedEntity toCopy) {
		this.setVorgaengerId(toCopy.getId());
		this.gueltigkeit = new DateRange(toCopy.gueltigkeit);
	}

	@Nonnull
	public DateRange getGueltigkeit() {
		return gueltigkeit;
	}

	public void setGueltigkeit(@Nonnull DateRange gueltigkeit) {
		this.gueltigkeit = gueltigkeit;
	}

	@SuppressWarnings("ObjectEquality")
	public boolean isSame(AbstractDateRangedEntity otherAbstDateRangedEntity) {
		if (this == otherAbstDateRangedEntity) {
			return true;
		}
		if (otherAbstDateRangedEntity == null || getClass() != otherAbstDateRangedEntity.getClass()) {
			return false;
		}
		return Objects.equals(this.getGueltigkeit(), otherAbstDateRangedEntity.getGueltigkeit());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("gueltigkeit", gueltigkeit)
			.toString();
	}
}

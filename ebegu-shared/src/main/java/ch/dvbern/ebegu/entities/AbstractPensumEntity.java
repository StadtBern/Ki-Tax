package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Objects;

/**
 * Abstrakte Entitaet. Muss von Entitaeten erweitert werden, die ein Pensum (Prozent) und ein DateRange beeinhalten.
 */
@MappedSuperclass
@Audited
public class AbstractPensumEntity extends AbstractDateRangedEntity {

	private static final long serialVersionUID = -7576083148864149528L;

	@Max(100)
	@Min(0)
	@Nullable
	private Integer pensum;

	@Nullable
	public Integer getPensum() {
		return pensum;
	}

	public void setPensum(@Nullable Integer pensum) {
		this.pensum = pensum;
	}

	@SuppressWarnings("ObjectEquality")
	public boolean isSame(AbstractPensumEntity otherAbstDateRangedEntity) {
		if (this == otherAbstDateRangedEntity) {
			return true;
		}
		if (otherAbstDateRangedEntity == null || getClass() != otherAbstDateRangedEntity.getClass()) {
			return false;
		}
		return super.isSame(otherAbstDateRangedEntity) && Objects.equals(this.getPensum(), otherAbstDateRangedEntity.getPensum());
	}
}

package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

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
}

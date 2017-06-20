package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
	@NotNull
	@Column(nullable = false)
	private Integer pensum;

	public AbstractPensumEntity() {
	}


	@Nonnull
	public Integer getPensum() {
		return pensum;
	}

	public void setPensum(@Nonnull Integer pensum) {
		this.pensum = pensum;
	}

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		final AbstractPensumEntity otherAbstDateRangedEntity = (AbstractPensumEntity) other;
		return super.isSame(otherAbstDateRangedEntity)
			&& Objects.equals(this.getPensum(), otherAbstDateRangedEntity.getPensum());
	}

	public AbstractPensumEntity copyForMutation(AbstractPensumEntity mutation) {
		super.copyForMutation(mutation);
		copyForMutationOrErneuerung(mutation);
		return mutation;
	}

	public AbstractPensumEntity copyForErneuerung(AbstractPensumEntity mutation) {
		super.copyForErneuerung(mutation);
		copyForMutationOrErneuerung(mutation);
		return mutation;
	}

	private void copyForMutationOrErneuerung(AbstractPensumEntity mutation) {
		mutation.setPensum(this.getPensum());
	}
}

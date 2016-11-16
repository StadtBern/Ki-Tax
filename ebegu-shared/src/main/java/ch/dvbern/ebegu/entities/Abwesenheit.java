package ch.dvbern.ebegu.entities;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * Entity fuer Abwesenheit.
 */
@Audited
@Entity
@SuppressWarnings("ComparableImplementedButEqualsNotOverridden")
public class Abwesenheit extends AbstractDateRangedEntity implements Comparable<Abwesenheit> {

	private static final long serialVersionUID = -6776981643150835840L;


	public Abwesenheit() {

	}

	public Abwesenheit(Abwesenheit toCopy) {
		super(toCopy);
	}

	@Override
	public int compareTo(Abwesenheit o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getGueltigkeit(), o.getGueltigkeit());
		builder.append(this.getId(), o.getId());
		return builder.toComparison();
	}

}

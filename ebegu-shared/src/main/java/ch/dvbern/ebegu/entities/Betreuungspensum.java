package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.types.DateRange;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.Entity;

/**
 * Entity fuer Betreuungspensen.
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
@SuppressWarnings("ComparableImplementedButEqualsNotOverridden")
@Audited
@Entity
public class Betreuungspensum extends AbstractPensumEntity implements Comparable<Betreuungspensum>{

	private static final long serialVersionUID = -9032857320571372370L;

	public Betreuungspensum() {

	}

	public Betreuungspensum(DateRange gueltigkeit) {
		this.setGueltigkeit(gueltigkeit);
	}

	public Betreuungspensum(@Nonnull Betreuungspensum toCopy) {
		super(toCopy);
	}

	@Override
	public int compareTo(Betreuungspensum o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getGueltigkeit(), o.getGueltigkeit());
		builder.append(this.getId(), o.getId());
		return builder.toComparison();
	}
}

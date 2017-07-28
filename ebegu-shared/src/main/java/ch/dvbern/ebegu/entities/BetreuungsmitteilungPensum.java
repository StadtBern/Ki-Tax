package ch.dvbern.ebegu.entities;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Entity fuer BetreuungsmitteilungPensum.
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
@SuppressWarnings("ComparableImplementedButEqualsNotOverridden")
@Audited
@Entity
public class BetreuungsmitteilungPensum extends AbstractPensumEntity implements Comparable<BetreuungsmitteilungPensum> {

	private static final long serialVersionUID = -9032858720574672370L;

	@ManyToOne(optional = false)
	@NotNull
	private Betreuungsmitteilung betreuungsmitteilung;


	public Betreuungsmitteilung getBetreuungsmitteilung() {
		return betreuungsmitteilung;
	}

	public void setBetreuungsmitteilung(Betreuungsmitteilung betreuungsmitteilung) {
		this.betreuungsmitteilung = betreuungsmitteilung;
	}

	@Override
	public int compareTo(@Nonnull BetreuungsmitteilungPensum o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getGueltigkeit(), o.getGueltigkeit());
		builder.append(this.getId(), o.getId());
		return builder.toComparison();
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
		if (!super.isSame(other)) {
			return false;
		}
		if (!(other instanceof BetreuungsmitteilungPensum)) {
			return false;
		}
		final BetreuungsmitteilungPensum otherBetreuungsmitteilungPensum = (BetreuungsmitteilungPensum) other;
		return getBetreuungsmitteilung().isSame(otherBetreuungsmitteilungPensum.getBetreuungsmitteilung());
	}
}

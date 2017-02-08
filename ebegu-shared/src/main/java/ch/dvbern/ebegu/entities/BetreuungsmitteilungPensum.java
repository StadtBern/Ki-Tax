package ch.dvbern.ebegu.entities;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Entity fuer BetreuungsmitteilungPensum.
 */
@Audited
@Entity
public class BetreuungsmitteilungPensum extends AbstractPensumEntity implements Comparable<BetreuungsmitteilungPensum> {

	private static final long serialVersionUID = -9032858720574672370L;

	@ManyToOne(optional = false)
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
}

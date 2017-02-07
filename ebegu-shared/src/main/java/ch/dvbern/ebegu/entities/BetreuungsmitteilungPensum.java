package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Entity fuer BetreuungsmitteilungPensum.
 */
@Audited
@Entity
public class BetreuungsmitteilungPensum extends AbstractPensumEntity {

	private static final long serialVersionUID = -9032858720574672370L;

	@ManyToOne(optional = false)
	private Betreuungsmitteilung betreuungsmitteilung;



	public Betreuungsmitteilung getBetreuungsmitteilung() {
		return betreuungsmitteilung;
	}

	public void setBetreuungsmitteilung(Betreuungsmitteilung betreuungsmitteilung) {
		this.betreuungsmitteilung = betreuungsmitteilung;
	}
}

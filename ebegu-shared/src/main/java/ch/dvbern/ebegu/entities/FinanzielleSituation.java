package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * Entität für die Finanzielle Situation
 */
@Audited
@Entity
public class FinanzielleSituation extends AbstractFinanzielleSituation {

	private static final long serialVersionUID = -4401110366293613225L;

	@Column(nullable = true)
	private BigDecimal nettolohn;

	public BigDecimal getNettolohn() {
		return nettolohn;
	}

	public void setNettolohn(final BigDecimal nettolohn) {
		this.nettolohn = nettolohn;
	}

}

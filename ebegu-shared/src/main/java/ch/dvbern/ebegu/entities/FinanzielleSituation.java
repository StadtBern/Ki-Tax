package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.util.FinanzielleSituationRechner;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entität für die Finanzielle Situation
 */
@Audited
@Entity
public class FinanzielleSituation extends AbstractFinanzielleSituation {

	private static final long serialVersionUID = -4401110366293613225L;

	@Column(nullable = true)
	private BigDecimal nettolohn;

	@Column(nullable = true)
	private BigDecimal geschaeftsgewinnBasisjahrMinus2;

	@Column(nullable = true)
	private BigDecimal geschaeftsgewinnBasisjahrMinus1;


	public FinanzielleSituation() {
	}


	@Transient
	public BigDecimal calcGeschaeftsgewinnDurchschnitt(){
		return FinanzielleSituationRechner.calcGeschaeftsgewinnDurchschnitt(this);
	}

	@Override
	public BigDecimal getNettolohn() {
		return nettolohn;
	}

	public void setNettolohn(final BigDecimal nettolohn) {
		this.nettolohn = nettolohn;
	}

	public BigDecimal getGeschaeftsgewinnBasisjahrMinus2() {
		return geschaeftsgewinnBasisjahrMinus2;
	}

	public void setGeschaeftsgewinnBasisjahrMinus2(final BigDecimal geschaeftsgewinnBasisjahrMinus2) {
		this.geschaeftsgewinnBasisjahrMinus2 = geschaeftsgewinnBasisjahrMinus2;
	}

	public BigDecimal getGeschaeftsgewinnBasisjahrMinus1() {
		return geschaeftsgewinnBasisjahrMinus1;
	}

	public void setGeschaeftsgewinnBasisjahrMinus1(final BigDecimal geschaeftsgewinnBasisjahrMinus1) {
		this.geschaeftsgewinnBasisjahrMinus1 = geschaeftsgewinnBasisjahrMinus1;
	}

	public FinanzielleSituation copyForMutation(FinanzielleSituation mutation) {
		super.copyForMutation(mutation);
		mutation.setNettolohn(this.getNettolohn());
		mutation.setGeschaeftsgewinnBasisjahrMinus1(this.getGeschaeftsgewinnBasisjahrMinus1());
		mutation.setGeschaeftsgewinnBasisjahrMinus2(this.getGeschaeftsgewinnBasisjahrMinus2());
		return mutation;
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
		final FinanzielleSituation otherFinSit = (FinanzielleSituation) other;
		return Objects.equals(getNettolohn(), otherFinSit.getNettolohn()) &&
			Objects.equals(getGeschaeftsgewinnBasisjahrMinus1(), otherFinSit.getGeschaeftsgewinnBasisjahrMinus1()) &&
			Objects.equals(getGeschaeftsgewinnBasisjahrMinus2(), otherFinSit.getGeschaeftsgewinnBasisjahrMinus2());
	}
}

/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import ch.dvbern.ebegu.util.FinanzielleSituationRechner;
import ch.dvbern.ebegu.util.MathUtil;
import org.hibernate.envers.Audited;

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
		if (!(other instanceof FinanzielleSituation)) {
			return false;
		}
		final FinanzielleSituation otherFinSit = (FinanzielleSituation) other;
		return MathUtil.isSame(getNettolohn(), otherFinSit.getNettolohn()) &&
			MathUtil.isSame(getGeschaeftsgewinnBasisjahrMinus1(), otherFinSit.getGeschaeftsgewinnBasisjahrMinus1()) &&
			MathUtil.isSame(getGeschaeftsgewinnBasisjahrMinus2(), otherFinSit.getGeschaeftsgewinnBasisjahrMinus2());
	}
}

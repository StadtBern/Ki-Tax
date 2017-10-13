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
import java.util.Objects;

import javax.annotation.Nullable;
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

	@Nullable
	@Column(nullable = true)
	private BigDecimal nettolohn;

	@Nullable
	@Column(nullable = true)
	private BigDecimal geschaeftsgewinnBasisjahrMinus2;

	@Nullable
	@Column(nullable = true)
	private BigDecimal geschaeftsgewinnBasisjahrMinus1;

	// Diese beiden Felder werden nicht immer eingegeben, deswegen Boolean und nicht boolean, damit sie auch null sein duerfen
	@Nullable
	@Column(nullable = true)
	private Boolean sozialhilfeBezueger;

	@Nullable
	@Column(nullable = true)
	private Boolean verguenstigungGewuenscht;

	public FinanzielleSituation() {
	}

	@Transient
	public BigDecimal calcGeschaeftsgewinnDurchschnitt() {
		return FinanzielleSituationRechner.calcGeschaeftsgewinnDurchschnitt(this);
	}

	@Nullable
	@Override
	public BigDecimal getNettolohn() {
		return nettolohn;
	}

	@Nullable
	public void setNettolohn(@Nullable final BigDecimal nettolohn) {
		this.nettolohn = nettolohn;
	}

	@Nullable
	public BigDecimal getGeschaeftsgewinnBasisjahrMinus2() {
		return geschaeftsgewinnBasisjahrMinus2;
	}

	public void setGeschaeftsgewinnBasisjahrMinus2(@Nullable final BigDecimal geschaeftsgewinnBasisjahrMinus2) {
		this.geschaeftsgewinnBasisjahrMinus2 = geschaeftsgewinnBasisjahrMinus2;
	}

	@Nullable
	public BigDecimal getGeschaeftsgewinnBasisjahrMinus1() {
		return geschaeftsgewinnBasisjahrMinus1;
	}

	public void setGeschaeftsgewinnBasisjahrMinus1(@Nullable final BigDecimal geschaeftsgewinnBasisjahrMinus1) {
		this.geschaeftsgewinnBasisjahrMinus1 = geschaeftsgewinnBasisjahrMinus1;
	}

	@Nullable
	public Boolean getSozialhilfeBezueger() {
		return sozialhilfeBezueger;
	}

	public void setSozialhilfeBezueger(@Nullable Boolean sozialhilfeBezueger) {
		this.sozialhilfeBezueger = sozialhilfeBezueger;
	}

	@Nullable
	public Boolean getVerguenstigungGewuenscht() {
		return verguenstigungGewuenscht;
	}

	public void setVerguenstigungGewuenscht(@Nullable Boolean verguenstigungGewuenscht) {
		this.verguenstigungGewuenscht = verguenstigungGewuenscht;
	}

	public FinanzielleSituation copyForMutation(FinanzielleSituation mutation) {
		super.copyForMutation(mutation);
		mutation.setNettolohn(this.getNettolohn());
		mutation.setGeschaeftsgewinnBasisjahrMinus1(this.getGeschaeftsgewinnBasisjahrMinus1());
		mutation.setGeschaeftsgewinnBasisjahrMinus2(this.getGeschaeftsgewinnBasisjahrMinus2());
		mutation.setSozialhilfeBezueger(this.getSozialhilfeBezueger());
		mutation.setVerguenstigungGewuenscht(this.getVerguenstigungGewuenscht());
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
			MathUtil.isSame(getGeschaeftsgewinnBasisjahrMinus2(), otherFinSit.getGeschaeftsgewinnBasisjahrMinus2()) &&
			Objects.equals(getSozialhilfeBezueger(), otherFinSit.getSozialhilfeBezueger()) &&
			Objects.equals(getVerguenstigungGewuenscht(), otherFinSit.getVerguenstigungGewuenscht());
	}
}

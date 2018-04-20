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

package ch.dvbern.ebegu.dto;

import java.math.BigDecimal;

import ch.dvbern.ebegu.util.MathUtil;

/**
 * DTO für die Resultate der Berechnungen der Finanziellen Situation
 */
public class FinanzielleSituationResultateDTO {

	private BigDecimal geschaeftsgewinnDurchschnittGesuchsteller1 = BigDecimal.ZERO;
	private BigDecimal geschaeftsgewinnDurchschnittGesuchsteller2 = BigDecimal.ZERO;
	private BigDecimal einkommenBeiderGesuchsteller = BigDecimal.ZERO;
	private BigDecimal nettovermoegenFuenfProzent = BigDecimal.ZERO;
	private BigDecimal anrechenbaresEinkommen = BigDecimal.ZERO;
	private BigDecimal abzuegeBeiderGesuchsteller = BigDecimal.ZERO;
	private BigDecimal massgebendesEinkVorAbzFamGr = BigDecimal.ZERO;

	public FinanzielleSituationResultateDTO() {
		initToZero();
	}

	private void initToZero() {
		// Alle Werte auf 0 initialisieren, falls Null
		// Wenn negativ -> 0
		geschaeftsgewinnDurchschnittGesuchsteller1 = MathUtil.positiveNonNullAndRound(geschaeftsgewinnDurchschnittGesuchsteller1);
		geschaeftsgewinnDurchschnittGesuchsteller2 = MathUtil.positiveNonNullAndRound(geschaeftsgewinnDurchschnittGesuchsteller2);
		einkommenBeiderGesuchsteller = MathUtil.positiveNonNullAndRound(einkommenBeiderGesuchsteller);
		nettovermoegenFuenfProzent = MathUtil.positiveNonNullAndRound(nettovermoegenFuenfProzent);
		anrechenbaresEinkommen = MathUtil.positiveNonNullAndRound(anrechenbaresEinkommen);
		abzuegeBeiderGesuchsteller = MathUtil.positiveNonNullAndRound(abzuegeBeiderGesuchsteller);
		massgebendesEinkVorAbzFamGr = MathUtil.positiveNonNullAndRound(massgebendesEinkVorAbzFamGr);
	}

	public BigDecimal getGeschaeftsgewinnDurchschnittGesuchsteller1() {
		return geschaeftsgewinnDurchschnittGesuchsteller1;
	}

	public void setGeschaeftsgewinnDurchschnittGesuchsteller1(BigDecimal geschaeftsgewinnDurchschnittGesuchsteller1) {
		this.geschaeftsgewinnDurchschnittGesuchsteller1 = geschaeftsgewinnDurchschnittGesuchsteller1;
	}

	public BigDecimal getGeschaeftsgewinnDurchschnittGesuchsteller2() {
		return geschaeftsgewinnDurchschnittGesuchsteller2;
	}

	public void setGeschaeftsgewinnDurchschnittGesuchsteller2(BigDecimal geschaeftsgewinnDurchschnittGesuchsteller2) {
		this.geschaeftsgewinnDurchschnittGesuchsteller2 = geschaeftsgewinnDurchschnittGesuchsteller2;
	}

	public BigDecimal getEinkommenBeiderGesuchsteller() {
		return einkommenBeiderGesuchsteller;
	}

	public void setEinkommenBeiderGesuchsteller(BigDecimal einkommenBeiderGesuchsteller) {
		this.einkommenBeiderGesuchsteller = einkommenBeiderGesuchsteller;
	}

	public BigDecimal getNettovermoegenFuenfProzent() {
		return nettovermoegenFuenfProzent;
	}

	public void setNettovermoegenFuenfProzent(BigDecimal nettovermoegenFuenfProzent) {
		this.nettovermoegenFuenfProzent = nettovermoegenFuenfProzent;
	}

	public BigDecimal getAnrechenbaresEinkommen() {
		return anrechenbaresEinkommen;
	}

	public void setAnrechenbaresEinkommen(BigDecimal anrechenbaresEinkommen) {
		this.anrechenbaresEinkommen = anrechenbaresEinkommen;
	}

	public BigDecimal getAbzuegeBeiderGesuchsteller() {
		return abzuegeBeiderGesuchsteller;
	}

	public void setAbzuegeBeiderGesuchsteller(BigDecimal abzuegeBeiderGesuchsteller) {
		this.abzuegeBeiderGesuchsteller = abzuegeBeiderGesuchsteller;
	}

	public BigDecimal getMassgebendesEinkVorAbzFamGr() {
		return massgebendesEinkVorAbzFamGr;
	}

	public void setMassgebendesEinkVorAbzFamGr(BigDecimal massgebendesEinkVorAbzFamGr) {
		this.massgebendesEinkVorAbzFamGr = massgebendesEinkVorAbzFamGr;
	}
}

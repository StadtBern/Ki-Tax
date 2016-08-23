package ch.dvbern.ebegu.dto;

import ch.dvbern.ebegu.util.MathUtil;

import java.math.BigDecimal;

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
	private BigDecimal abzugAufgrundFamiliengroesse = BigDecimal.ZERO;
	private BigDecimal totalAbzuege = BigDecimal.ZERO;
	private BigDecimal massgebendesEinkommen = BigDecimal.ZERO;
	private Double familiengroesse;


	public FinanzielleSituationResultateDTO(double familiengroesse, BigDecimal famGroesseAbz) {
		this.familiengroesse = familiengroesse;
		this.abzugAufgrundFamiliengroesse = famGroesseAbz;
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
		abzugAufgrundFamiliengroesse = MathUtil.positiveNonNullAndRound(abzugAufgrundFamiliengroesse);
		totalAbzuege = MathUtil.positiveNonNullAndRound(totalAbzuege);
		massgebendesEinkommen = MathUtil.positiveNonNullAndRound(massgebendesEinkommen);
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

	public BigDecimal getAbzugAufgrundFamiliengroesse() {
		return abzugAufgrundFamiliengroesse;
	}

	public void setAbzugAufgrundFamiliengroesse(BigDecimal abzugAufgrundFamiliengroesse) {
		this.abzugAufgrundFamiliengroesse = abzugAufgrundFamiliengroesse;
	}

	public BigDecimal getTotalAbzuege() {
		return totalAbzuege;
	}

	public void setTotalAbzuege(BigDecimal totalAbzuege) {
		this.totalAbzuege = totalAbzuege;
	}

	public BigDecimal getMassgebendesEinkommen() {
		return massgebendesEinkommen;
	}

	public void setMassgebendesEinkommen(BigDecimal massgebendesEinkommen) {
		this.massgebendesEinkommen = massgebendesEinkommen;
	}

	public Double getFamiliengroesse() {
		return familiengroesse;
	}

	public void setFamiliengroesse(Double familiengroesse) {
		this.familiengroesse = familiengroesse;
	}
}

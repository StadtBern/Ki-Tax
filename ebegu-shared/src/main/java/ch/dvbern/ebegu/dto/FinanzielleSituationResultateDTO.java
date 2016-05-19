package ch.dvbern.ebegu.dto;

import ch.dvbern.ebegu.entities.Gesuch;

import java.math.BigDecimal;

/**
 * DTO für die Resultate der Berechnungen der Finanziellen Situation
 */
public class FinanzielleSituationResultateDTO {

	private BigDecimal geschaeftsgewinnDurchschnittGesuchsteller1;
	private BigDecimal geschaeftsgewinnDurchschnittGesuchsteller2;
	private BigDecimal einkommenBeiderGesuchsteller;
	private BigDecimal nettovermoegenFuenfProzent;
	private BigDecimal anrechenbaresEinkommen;
	private BigDecimal abzuegeBeiderGesuchsteller;
	private BigDecimal abzugAufgrundFamiliengroesse;
	private BigDecimal totalAbzuege;
	private BigDecimal massgebendesEinkommen;

	public FinanzielleSituationResultateDTO(Gesuch gesuch) {
		//TODO (hefr) weitere Berechnungen
		this.geschaeftsgewinnDurchschnittGesuchsteller1 = gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationSV().getGeschaeftsgewinnDurchschnitt();
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
}

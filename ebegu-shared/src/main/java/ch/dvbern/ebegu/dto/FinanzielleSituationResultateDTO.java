package ch.dvbern.ebegu.dto;

import ch.dvbern.ebegu.entities.FinanzielleSituation;
import ch.dvbern.ebegu.entities.Gesuch;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
	private Integer familiengroesse;


	public FinanzielleSituationResultateDTO(Gesuch gesuch, Integer familiengroesse, BigDecimal abzugAufgrundFamiliengroesse) {
		this.familiengroesse = familiengroesse;
		this.abzugAufgrundFamiliengroesse = abzugAufgrundFamiliengroesse;
		if (gesuch != null) {
			if (gesuch.getGesuchsteller1() != null) {
				this.geschaeftsgewinnDurchschnittGesuchsteller1 = calcGeschaeftsgewinnDurchschnitt(gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationSV());
				this.einkommenBeiderGesuchsteller = add(einkommenBeiderGesuchsteller, calcEinkommen(gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationSV()));
				this.nettovermoegenFuenfProzent = add(nettovermoegenFuenfProzent, calcVermoegen5Prozent(gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationSV()));
				this.abzuegeBeiderGesuchsteller = add(abzuegeBeiderGesuchsteller, gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationSV().getGeleisteteAlimente());
			}
			if (gesuch.getGesuchsteller2() != null) {
				this.geschaeftsgewinnDurchschnittGesuchsteller2 = calcGeschaeftsgewinnDurchschnitt(gesuch.getGesuchsteller2().getFinanzielleSituationContainer().getFinanzielleSituationSV());
				this.einkommenBeiderGesuchsteller = add(einkommenBeiderGesuchsteller, calcEinkommen(gesuch.getGesuchsteller2().getFinanzielleSituationContainer().getFinanzielleSituationSV()));
				this.nettovermoegenFuenfProzent = add(nettovermoegenFuenfProzent, calcVermoegen5Prozent(gesuch.getGesuchsteller2().getFinanzielleSituationContainer().getFinanzielleSituationSV()));
				this.abzuegeBeiderGesuchsteller = add(abzuegeBeiderGesuchsteller, gesuch.getGesuchsteller2().getFinanzielleSituationContainer().getFinanzielleSituationSV().getGeleisteteAlimente());
			}
			this.anrechenbaresEinkommen = add(einkommenBeiderGesuchsteller, nettovermoegenFuenfProzent);
			this.totalAbzuege = add(abzuegeBeiderGesuchsteller, abzugAufgrundFamiliengroesse);
			this.massgebendesEinkommen = subtract(anrechenbaresEinkommen, totalAbzuege);
		}
		// Alle Werte auf 0 initialisieren, falls Null
		// Wenn negativ -> 0
		geschaeftsgewinnDurchschnittGesuchsteller1 = positiveNonNull(geschaeftsgewinnDurchschnittGesuchsteller1);
		geschaeftsgewinnDurchschnittGesuchsteller2 = positiveNonNull(geschaeftsgewinnDurchschnittGesuchsteller2);
		einkommenBeiderGesuchsteller = positiveNonNull(einkommenBeiderGesuchsteller);
		nettovermoegenFuenfProzent = positiveNonNull(nettovermoegenFuenfProzent);
		anrechenbaresEinkommen = positiveNonNull(anrechenbaresEinkommen);
		abzuegeBeiderGesuchsteller = positiveNonNull(abzuegeBeiderGesuchsteller);
		abzugAufgrundFamiliengroesse = positiveNonNull(abzugAufgrundFamiliengroesse);
		totalAbzuege = positiveNonNull(totalAbzuege);
		massgebendesEinkommen = positiveNonNull(massgebendesEinkommen);
	}

	private BigDecimal calcGeschaeftsgewinnDurchschnitt(FinanzielleSituation finanzielleSituation) {
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal anzahlJahre = BigDecimal.ZERO;
		if (finanzielleSituation.getGeschaeftsgewinnBasisjahrMinus2() != null) {
			total = total.add(finanzielleSituation.getGeschaeftsgewinnBasisjahrMinus2());
			anzahlJahre = anzahlJahre.add(BigDecimal.ONE);
		}
		if (finanzielleSituation.getGeschaeftsgewinnBasisjahrMinus1() != null) {
			total = total.add(finanzielleSituation.getGeschaeftsgewinnBasisjahrMinus1());
			anzahlJahre = anzahlJahre.add(BigDecimal.ONE);
		}
		if (finanzielleSituation.getGeschaeftsgewinnBasisjahr() != null) {
			total = total.add(finanzielleSituation.getGeschaeftsgewinnBasisjahr());
			anzahlJahre = anzahlJahre.add(BigDecimal.ONE);
		}
		if (anzahlJahre.intValue() > 0) {
			return total.divide(anzahlJahre, RoundingMode.HALF_UP);
		}
		return null;
	}

	private BigDecimal calcEinkommen(FinanzielleSituation finanzielleSituation) {
		BigDecimal total = BigDecimal.ZERO;
		total = add(total, finanzielleSituation.getNettolohn());
		total = add(total, finanzielleSituation.getFamilienzulage());
		total = add(total, finanzielleSituation.getErsatzeinkommen());
		total = add(total, finanzielleSituation.getErhalteneAlimente());
		total = add(total, calcGeschaeftsgewinnDurchschnitt(finanzielleSituation));
		total = subtract(total, finanzielleSituation.getGeleisteteAlimente());
		return total;
	}

	private BigDecimal calcVermoegen5Prozent(FinanzielleSituation finanzielleSituation) {
		BigDecimal total = subtract(finanzielleSituation.getBruttovermoegen(), finanzielleSituation.getSchulden());
		total = percent(total, 5);
		return total;
	}

	private BigDecimal add(BigDecimal value1, BigDecimal value2) {
		value1 = value1 != null ? value1 : BigDecimal.ZERO;
		value2 = value2 != null ? value2 : BigDecimal.ZERO;
		return value1.add(value2);
	}

	private BigDecimal subtract(BigDecimal value1, BigDecimal value2) {
		value1 = value1 != null ? value1 : BigDecimal.ZERO;
		value2 = value2 != null ? value2 : BigDecimal.ZERO;
		return value1.subtract(value2);
	}

	private BigDecimal percent(BigDecimal value, int percent) {
		BigDecimal total = value != null ? value : BigDecimal.ZERO;
		total = total.multiply(new BigDecimal(""+percent));
		total = total.divide(new BigDecimal("100"), BigDecimal.ROUND_HALF_UP);
		return total;
	}

	private BigDecimal positiveNonNull(BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		// Returns the maximum of this BigDecimal and val.
		return value.max(BigDecimal.ZERO);
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

	public Integer getFamiliengroesse() {
		return familiengroesse;
	}

	public void setFamiliengroesse(Integer familiengroesse) {
		this.familiengroesse = familiengroesse;
	}
}

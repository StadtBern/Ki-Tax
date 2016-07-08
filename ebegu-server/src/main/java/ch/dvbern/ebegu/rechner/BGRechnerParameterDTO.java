package ch.dvbern.ebegu.rechner;

import java.math.BigDecimal;

/**
 * Kapselung aller Parameter, welche für die BG-Berechnung aller Angebote benötigt werden.
 * Diese müssen aus den EbeguParametern gelesen werden.
 */
public class BGRechnerParameterDTO {

	private BigDecimal beitragKantonProTag; 		// PARAM_ABGELTUNG_PRO_TAG_KANTON
	private BigDecimal beitragStadtProTag; 			// PARAM_FIXBETRAG_STADT_PRO_TAG_KITA

	private BigDecimal anzahlTageTagi; 				// PARAM_ANZAHL_TAGE_KANTON
	private BigDecimal anzahlTageMaximal; 			// PARAM_ANZAL_TAGE_MAX_KITA

	private BigDecimal anzahlStundenProTagTagi; 	// PARAM_STUNDEN_PRO_TAG_TAGI
	private BigDecimal anzahlStundenProTagMaximal; 	// PARAM_STUNDEN_PRO_TAG_MAX_KITA

	private BigDecimal kostenProStundeMaximalKitaTagi; // PARAM_KOSTEN_PRO_STUNDE_MAX
	private BigDecimal kostenProStundeMaximalTageseltern; // PARAM_KOSTEN_PRO_STUNDE_MAX_TAGESELTERN
	private BigDecimal kostenProStundeMinimal; 		// PARAM_KOSTEN_PRO_STUNDE_MIN

	private BigDecimal massgebendesEinkommenMaximal; // PARAM_MASSGEBENDES_EINKOMMEN_MIN
	private BigDecimal massgebendesEinkommenMinimal; // PARAM_MASSGEBENDES_EINKOMMEN_MAX

	private BigDecimal babyFaktor;					// PARAM_BABY_FAKTOR
	private int babyAlterInMonaten;					// PARAM_BABY_ALTER_IN_MONATEN



	public BigDecimal getBeitragKantonProTag() {
		return beitragKantonProTag;
	}

	public void setBeitragKantonProTag(BigDecimal beitragKantonProTag) {
		this.beitragKantonProTag = beitragKantonProTag;
	}

	public BigDecimal getBeitragStadtProTag() {
		return beitragStadtProTag;
	}

	public void setBeitragStadtProTag(BigDecimal beitragStadtProTag) {
		this.beitragStadtProTag = beitragStadtProTag;
	}

	public BigDecimal getAnzahlTageMaximal() {
		return anzahlTageMaximal;
	}

	public void setAnzahlTageMaximal(BigDecimal anzahlTageMaximal) {
		this.anzahlTageMaximal = anzahlTageMaximal;
	}

	public BigDecimal getAnzahlStundenProTagMaximal() {
		return anzahlStundenProTagMaximal;
	}

	public void setAnzahlStundenProTagMaximal(BigDecimal anzahlStundenProTagMaximal) {
		this.anzahlStundenProTagMaximal = anzahlStundenProTagMaximal;
	}

	public BigDecimal getKostenProStundeMaximalKitaTagi() {
		return kostenProStundeMaximalKitaTagi;
	}

	public void setKostenProStundeMaximalKitaTagi(BigDecimal kostenProStundeMaximalKitaTagi) {
		this.kostenProStundeMaximalKitaTagi = kostenProStundeMaximalKitaTagi;
	}

	public BigDecimal getKostenProStundeMinimal() {
		return kostenProStundeMinimal;
	}

	public void setKostenProStundeMinimal(BigDecimal kostenProStundeMinimal) {
		this.kostenProStundeMinimal = kostenProStundeMinimal;
	}

	public BigDecimal getMassgebendesEinkommenMaximal() {
		return massgebendesEinkommenMaximal;
	}

	public void setMassgebendesEinkommenMaximal(BigDecimal massgebendesEinkommenMaximal) {
		this.massgebendesEinkommenMaximal = massgebendesEinkommenMaximal;
	}

	public BigDecimal getMassgebendesEinkommenMinimal() {
		return massgebendesEinkommenMinimal;
	}

	public void setMassgebendesEinkommenMinimal(BigDecimal massgebendesEinkommenMinimal) {
		this.massgebendesEinkommenMinimal = massgebendesEinkommenMinimal;
	}

	public BigDecimal getAnzahlTageTagi() {
		return anzahlTageTagi;
	}

	public void setAnzahlTageTagi(BigDecimal anzahlTageTagi) {
		this.anzahlTageTagi = anzahlTageTagi;
	}

	public BigDecimal getAnzahlStundenProTagTagi() {
		return anzahlStundenProTagTagi;
	}

	public void setAnzahlStundenProTagTagi(BigDecimal anzahlStundenProTagTagi) {
		this.anzahlStundenProTagTagi = anzahlStundenProTagTagi;
	}

	public BigDecimal getKostenProStundeMaximalTageseltern() {
		return kostenProStundeMaximalTageseltern;
	}

	public void setKostenProStundeMaximalTageseltern(BigDecimal kostenProStundeMaximalTageseltern) {
		this.kostenProStundeMaximalTageseltern = kostenProStundeMaximalTageseltern;
	}

	public BigDecimal getBabyFaktor() {
		return babyFaktor;
	}

	public void setBabyFaktor(BigDecimal babyFaktor) {
		this.babyFaktor = babyFaktor;
	}

	public int getBabyAlterInMonaten() {
		return babyAlterInMonaten;
	}

	public void setBabyAlterInMonaten(int babyAlterInMonaten) {
		this.babyAlterInMonaten = babyAlterInMonaten;
	}
}

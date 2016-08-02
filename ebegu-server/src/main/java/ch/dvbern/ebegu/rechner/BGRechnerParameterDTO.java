package ch.dvbern.ebegu.rechner;

import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;

import static ch.dvbern.ebegu.enums.EbeguParameterKey.*;

/**
 * Kapselung aller Parameter, welche für die BG-Berechnung aller Angebote benötigt werden.
 * Diese müssen aus den EbeguParametern gelesen werden.
 */
public final class BGRechnerParameterDTO {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass().getSimpleName());

	private BigDecimal beitragKantonProTagJahr1; 		// PARAM_ABGELTUNG_PRO_TAG_KANTON
	private BigDecimal beitragKantonProTagJahr2; 		// PARAM_ABGELTUNG_PRO_TAG_KANTON

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

	public BGRechnerParameterDTO(Map<EbeguParameterKey, EbeguParameter> paramMap, Gesuchsperiode gesuchsperiode, Mandant mandant) {

		this.setBeitragStadtProTag                  (asBigDecimal(paramMap, PARAM_FIXBETRAG_STADT_PRO_TAG_KITA, gesuchsperiode, mandant));
		this.setAnzahlTageMaximal                   (asBigDecimal(paramMap, PARAM_ANZAL_TAGE_MAX_KITA, gesuchsperiode, mandant));
		this.setAnzahlStundenProTagMaximal          (asBigDecimal(paramMap, PARAM_STUNDEN_PRO_TAG_MAX_KITA, gesuchsperiode, mandant));
		this.setKostenProStundeMaximalKitaTagi      (asBigDecimal(paramMap, PARAM_KOSTEN_PRO_STUNDE_MAX, gesuchsperiode, mandant));
		this.setKostenProStundeMinimal              (asBigDecimal(paramMap, PARAM_KOSTEN_PRO_STUNDE_MIN, gesuchsperiode, mandant));
		this.setMassgebendesEinkommenMaximal        (asBigDecimal(paramMap, PARAM_MASSGEBENDES_EINKOMMEN_MAX, gesuchsperiode, mandant));
		this.setMassgebendesEinkommenMinimal        (asBigDecimal(paramMap, PARAM_MASSGEBENDES_EINKOMMEN_MIN, gesuchsperiode, mandant));
		this.setAnzahlTageTagi                      (asBigDecimal(paramMap, PARAM_ANZAHL_TAGE_KANTON, gesuchsperiode, mandant));
		this.setAnzahlStundenProTagTagi             (asBigDecimal(paramMap, PARAM_STUNDEN_PRO_TAG_TAGI, gesuchsperiode, mandant));
		this.setKostenProStundeMaximalTageseltern   (asBigDecimal(paramMap, PARAM_KOSTEN_PRO_STUNDE_MAX_TAGESELTERN, gesuchsperiode, mandant));
		this.setBabyAlterInMonaten					(asInteger(paramMap, PARAM_BABY_ALTER_IN_MONATEN, gesuchsperiode, mandant));
		this.setBabyFaktor                          (asBigDecimal(paramMap, PARAM_BABY_FAKTOR, gesuchsperiode, mandant));
	}

	public BGRechnerParameterDTO() {

	}


	private int asInteger(Map<EbeguParameterKey, EbeguParameter> paramMap, EbeguParameterKey paramKey, Gesuchsperiode gesuchsperiode, Mandant mandant) {
		EbeguParameter param = paramMap.get(paramKey);
		if (param == null) {
			LOG.error("Required calculator parameter '{}' could not be loaded for the given Mandant '{}', Gesuchsperiode '{}'", paramKey, mandant, gesuchsperiode);
			throw new EbeguEntityNotFoundException("loadCalculatorParameters", ErrorCodeEnum.ERROR_PARAMETER_NOT_FOUND, paramKey);
		}
		return param.getValueAsInteger();
	}


	private BigDecimal asBigDecimal(Map<EbeguParameterKey, EbeguParameter> paramMap, EbeguParameterKey paramKey, Gesuchsperiode gesuchsperiode, Mandant mandant) {
		EbeguParameter param = paramMap.get(paramKey);
		if (param == null) {
			LOG.error("Required calculator parameter '{}' could not be loaded for the given Mandant '{}', Gesuchsperiode '{}'", paramKey, mandant, gesuchsperiode);
			throw new EbeguEntityNotFoundException("loadCalculatorParameters", ErrorCodeEnum.ERROR_PARAMETER_NOT_FOUND, paramKey);
		}
		return param.getValueAsBigDecimal();
	}


	public BigDecimal getBeitragKantonProTagJahr1() {
		return beitragKantonProTagJahr1;
	}

	public void setBeitragKantonProTagJahr1(BigDecimal beitragKantonProTagJahr1) {
		this.beitragKantonProTagJahr1 = beitragKantonProTagJahr1;
	}

	public BigDecimal getBeitragKantonProTagJahr2() {
		return beitragKantonProTagJahr2;
	}

	public void setBeitragKantonProTagJahr2(BigDecimal beitragKantonProTagJahr2) {
		this.beitragKantonProTagJahr2 = beitragKantonProTagJahr2;
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

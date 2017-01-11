package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzDatenDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

/**
 * Setzt fuer die Zeitabschnitte das Massgebende Einkommen. Sollte der Maximalwert uebschritte werden so wird das Pensum auf 0 gesetzt
 * ACHTUNG: Diese Regel gilt nur fuer Kita und Tageseltern Kleinkinder.  Bei Tageseltern Schulkinder und Tagesstaetten
 * gibt es keine Reduktion des Anspruchs, sie bezahlen aber den Volltarif
 * Regel 16.7 Maximales Einkommen
 */
public class EinkommenCalcRule extends AbstractCalcRule {


	private BigDecimal maximalesEinkommen;


	public EinkommenCalcRule(DateRange validityPeriod, BigDecimal maximalesEinkommen) {
		super(RuleKey.EINKOMMEN, RuleType.REDUKTIONSREGEL, validityPeriod);
		this.maximalesEinkommen = maximalesEinkommen;
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {

		// Die Finanzdaten berechnen
		FinanzDatenDTO finanzDatenDTO;
		if (verfuegungZeitabschnitt.isHasSecondGesuchsteller()) {
			finanzDatenDTO = betreuung.extractGesuch().getFinanzDatenDTO_zuZweit();
			setMassgebendesEinkommen(verfuegungZeitabschnitt.isEkv1ZuZweit(), verfuegungZeitabschnitt.isEkv2ZuZweit(), finanzDatenDTO, verfuegungZeitabschnitt, betreuung);
		} else {
			finanzDatenDTO = betreuung.extractGesuch().getFinanzDatenDTO_alleine();
			setMassgebendesEinkommen(verfuegungZeitabschnitt.isEkv1Alleine(), verfuegungZeitabschnitt.isEkv2Alleine(), finanzDatenDTO, verfuegungZeitabschnitt, betreuung);
		}

		// Erst jetzt kann das Maximale Einkommen geprueft werden!
		if (betreuung.getBetreuungsangebotTyp().isJugendamt()) {
			if (verfuegungZeitabschnitt.getMassgebendesEinkommen().compareTo(maximalesEinkommen) > 0) {
				//maximales einkommen wurde ueberschritten
				verfuegungZeitabschnitt.setKategorieMaxEinkommen(true);
				if (betreuung.getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()) {
					verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(0);
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMEN_MSG);
				} else {
					verfuegungZeitabschnitt.setBezahltVollkosten(true);
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMEN_VOLLKOSTEN_MSG);
				}
			}
		}
	}

	private void setMassgebendesEinkommen(boolean isEkv1, boolean isEkv2, FinanzDatenDTO finanzDatenDTO, VerfuegungZeitabschnitt verfuegungZeitabschnitt, Betreuung betreuung) {
		int basisjahr = betreuung.extractGesuchsperiode().getBasisJahr();
		int basisjahrPlus1 = betreuung.extractGesuchsperiode().getBasisJahrPlus1();
		int basisjahrPlus2 = betreuung.extractGesuchsperiode().getBasisJahrPlus2();
		if (isEkv1) {
			if (finanzDatenDTO.isEkv1Accepted()) {
				verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjP1VorAbzFamGr());
				verfuegungZeitabschnitt.setEinkommensjahr(basisjahrPlus1);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_ACCEPT_MSG, "" + basisjahrPlus1);
			} else {
				verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjVorAbzFamGr());
				verfuegungZeitabschnitt.setEinkommensjahr(basisjahr);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_NOT_ACCEPT_MSG, "" + basisjahrPlus1);
			}

		} else if (isEkv2) {
			if (finanzDatenDTO.isEkv2Accepted()) {
				verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjP2VorAbzFamGr());
				verfuegungZeitabschnitt.setEinkommensjahr(basisjahrPlus2);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_ACCEPT_MSG, "" + basisjahrPlus2);
			} else if (verfuegungZeitabschnitt.isEkv1NotExisting()) {
				verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjVorAbzFamGr());
				verfuegungZeitabschnitt.setEinkommensjahr(basisjahr);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_NOT_ACCEPT_MSG, "" + basisjahrPlus2);
			} else {
				verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjP1VorAbzFamGr());
				verfuegungZeitabschnitt.setEinkommensjahr(basisjahrPlus1);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_NOT_ACCEPT_MSG, "" + basisjahrPlus2);
			}
		} else {
			verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjVorAbzFamGr());
			verfuegungZeitabschnitt.setEinkommensjahr(basisjahr);
		}
	}

	@Override
	public boolean isRelevantForFamiliensituation() {
		return true;
	}
}

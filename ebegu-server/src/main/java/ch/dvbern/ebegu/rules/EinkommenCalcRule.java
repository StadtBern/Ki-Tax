package ch.dvbern.ebegu.rules;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.dto.FinanzDatenDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;

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
		if (verfuegungZeitabschnitt.isHasSecondGesuchstellerForFinanzielleSituation()) {
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

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	private void setMassgebendesEinkommen(boolean isEkv1, boolean isEkv2, FinanzDatenDTO finanzDatenDTO, VerfuegungZeitabschnitt verfuegungZeitabschnitt, Betreuung betreuung) {
		int basisjahr = betreuung.extractGesuchsperiode().getBasisJahr();
		int basisjahrPlus1 = betreuung.extractGesuchsperiode().getBasisJahrPlus1();
		int basisjahrPlus2 = betreuung.extractGesuchsperiode().getBasisJahrPlus2();
		if (isEkv1) {
			if (finanzDatenDTO.isEkv1AcceptedAndNotAnnuliert()) {
				verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjP1VorAbzFamGr());
				verfuegungZeitabschnitt.setEinkommensjahr(basisjahrPlus1);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_ACCEPT_MSG, "" + basisjahrPlus1);
			} else {
				verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjVorAbzFamGr());
				verfuegungZeitabschnitt.setEinkommensjahr(basisjahr);
				// Je nachdem, ob es (manuell) annulliert war oder die 20% nicht erreicht hat, kommt eine andere Meldung
				if (finanzDatenDTO.isEkv1Annulliert()) {
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_ANNULLIERT_MSG, "" + basisjahrPlus1);
				} else {
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_NOT_ACCEPT_MSG, "" + basisjahrPlus1);
				}
			}

		} else if (isEkv2) {
			if (finanzDatenDTO.isEkv2AcceptedAndNotAnnuliert()) {
				verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjP2VorAbzFamGr());
				verfuegungZeitabschnitt.setEinkommensjahr(basisjahrPlus2);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_ACCEPT_MSG, "" + basisjahrPlus2);
			} else  {
				if (verfuegungZeitabschnitt.isEkv1NotExisting() || finanzDatenDTO.isEkv1Annulliert()) {
					verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjVorAbzFamGr());
					verfuegungZeitabschnitt.setEinkommensjahr(basisjahr);
				} else {
					verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjP1VorAbzFamGr());
					verfuegungZeitabschnitt.setEinkommensjahr(basisjahrPlus1);
				}
				if (finanzDatenDTO.isEkv2Annulliert()) {
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_ANNULLIERT_MSG, "" + basisjahrPlus2);
				} else {
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_NOT_ACCEPT_MSG, "" + basisjahrPlus2);
				}
			}
		} else {
			verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjVorAbzFamGr());
			verfuegungZeitabschnitt.setEinkommensjahr(basisjahr);
		}

		handleSpecialCases(finanzDatenDTO, verfuegungZeitabschnitt, basisjahrPlus1);

	}

	/**
	 * This method will handle all special cases that can happen with the data of EKV
	 *
	 * Sonderfall: Die EKV1 ist im Dezember und die EKV2 im "Vorjahr", d.h. ebenfalls im Dezember.
	 * In diesem Fall haben wir im selben Zeitabschnitt eigentlich 2 Einkommensverschlechterungen, die entweder
	 * beide akzeptiert oder abgelehnt oder ignoriert bzw. alle Kombinationen davon sein können.
	 * In diesen Fällen müssen die Kommentare für EKV1 noch angepasst werden
	 */
	@SuppressWarnings("PMD.CollapsibleIfStatements")
	private void handleSpecialCases(FinanzDatenDTO finanzDatenDTO, VerfuegungZeitabschnitt verfuegungZeitabschnitt,
		int basisjahrPlus1) {
		if (finanzDatenDTO.getDatumVonBasisjahrPlus1() != null && finanzDatenDTO.getDatumVonBasisjahrPlus2() != null) {
			if (finanzDatenDTO.getDatumVonBasisjahrPlus1().isEqual(finanzDatenDTO.getDatumVonBasisjahrPlus2())) {
				if (finanzDatenDTO.isEkv1AcceptedAndNotAnnuliert()) {
					// Die EKV1 kommt zum Zuge, wird aber durch die EKV2 "überschrieben"
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_ACCEPT_MSG, String.valueOf(basisjahrPlus1));
				} else if (finanzDatenDTO.isEkv1Annulliert()) {
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_ANNULLIERT_MSG, String.valueOf(basisjahrPlus1));
				} else {
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_NOT_ACCEPT_MSG, String.valueOf(basisjahrPlus1));
				}
			}
		}
	}

	@Override
	public boolean isRelevantForFamiliensituation() {
		return true;
	}
}

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

package ch.dvbern.ebegu.rules;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.dto.FinanzDatenDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.Gesuch;
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

	private final BigDecimal maximalesEinkommen;

	public EinkommenCalcRule(DateRange validityPeriod, BigDecimal maximalesEinkommen) {
		super(RuleKey.EINKOMMEN, RuleType.REDUKTIONSREGEL, validityPeriod);
		this.maximalesEinkommen = maximalesEinkommen;
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		final Gesuch gesuch = betreuung.extractGesuch();

		if (gesuch.getGesuchsperiode().isVerpflegungenActive()) {
			// Es gibt zwei Faelle, in denen die Finanzielle Situation nicht bekannt ist:
			// - Sozialhilfeempfaenger: Wir rechnen mit Einkommen = 0
			// - Keine Vergünstigung gewünscht: Wir rechnen mit dem Maximalen Einkommen
			Familiensituation familiensituation = gesuch.extractFamiliensituation();
			if (familiensituation != null) {
				int basisjahr = betreuung.extractGesuchsperiode().getBasisJahr();
				if (Boolean.TRUE.equals(familiensituation.getSozialhilfeBezueger())) {
					verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(BigDecimal.ZERO);
					verfuegungZeitabschnitt.setAbzugFamGroesse(BigDecimal.ZERO);
					verfuegungZeitabschnitt.setEinkommensjahr(basisjahr);
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMEN_SOZIALHILFEEMPFAENGER_MSG);
					return;
				}
				if (Boolean.FALSE.equals(familiensituation.getVerguenstigungGewuenscht())) {
					verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(maximalesEinkommen);
					verfuegungZeitabschnitt.setAbzugFamGroesse(BigDecimal.ZERO);
					verfuegungZeitabschnitt.setEinkommensjahr(basisjahr);
					verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(0);
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMEN_MSG);
					return;
				}
			}
		}

		// Die Finanzdaten berechnen
		FinanzDatenDTO finanzDatenDTO;
		if (verfuegungZeitabschnitt.isHasSecondGesuchstellerForFinanzielleSituation()) {
			finanzDatenDTO = gesuch.getFinanzDatenDTO_zuZweit();
			setMassgebendesEinkommen(verfuegungZeitabschnitt.isEkv1ZuZweit(), verfuegungZeitabschnitt.isEkv2ZuZweit(), finanzDatenDTO, verfuegungZeitabschnitt, betreuung);
		} else {
			finanzDatenDTO = gesuch.getFinanzDatenDTO_alleine();
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
				verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_ACCEPT_MSG, String.valueOf(basisjahrPlus1));
			} else {
				verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjVorAbzFamGr());
				verfuegungZeitabschnitt.setEinkommensjahr(basisjahr);
				// Je nachdem, ob es (manuell) annulliert war oder die 20% nicht erreicht hat, kommt eine andere Meldung
				if (finanzDatenDTO.isEkv1Annulliert()) {
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey
						.EINKOMMENSVERSCHLECHTERUNG_ANNULLIERT_MSG, String.valueOf(basisjahrPlus1));
				} else {
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey
						.EINKOMMENSVERSCHLECHTERUNG_NOT_ACCEPT_MSG, String.valueOf(basisjahrPlus1));
				}
			}

		} else if (isEkv2) {
			if (finanzDatenDTO.isEkv2AcceptedAndNotAnnuliert()) {
				// EKV 1 accepted -> basisjahr + 2
				verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjP2VorAbzFamGr());
				verfuegungZeitabschnitt.setEinkommensjahr(basisjahrPlus2);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey.EINKOMMENSVERSCHLECHTERUNG_ACCEPT_MSG,
					String.valueOf(basisjahrPlus2));
			} else {
				if (finanzDatenDTO.isEkv1AcceptedAndNotAnnuliert()) {
					verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjP1VorAbzFamGr());
					verfuegungZeitabschnitt.setEinkommensjahr(basisjahrPlus1);
				} else {
					verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(finanzDatenDTO.getMassgebendesEinkBjVorAbzFamGr());
					verfuegungZeitabschnitt.setEinkommensjahr(basisjahr);
				}
				if (finanzDatenDTO.isEkv2Annulliert()) {
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey
						.EINKOMMENSVERSCHLECHTERUNG_ANNULLIERT_MSG, String.valueOf(basisjahrPlus2));
				} else {
					verfuegungZeitabschnitt.addBemerkung(RuleKey.EINKOMMEN, MsgKey
						.EINKOMMENSVERSCHLECHTERUNG_NOT_ACCEPT_MSG, String.valueOf(basisjahrPlus2));
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

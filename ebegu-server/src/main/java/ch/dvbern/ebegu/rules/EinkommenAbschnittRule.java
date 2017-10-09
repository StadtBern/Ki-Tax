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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.dto.FinanzDatenDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfo;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

/**
 * Setzt das massgebende Einkommen in die benoetigten Zeitabschnitte
 * ACHTUNG: Die Regel fuer Einkommensverschlechterung besagt (aktuell), dass die Veraenderung ab dem Folgemonat gilt
 * Dies wird nicht hier in der Regel bestimmt, sondern bereits im FinanzielleSituationRechner entsprechend abgelegt.
 * Siehe auch {@link EinkommensverschlechterungInfo#getStichtagGueltigFuerBasisJahrPlus1()}
 */
public class EinkommenAbschnittRule extends AbstractAbschnittRule {

	public EinkommenAbschnittRule(DateRange validityPeriod) {
		super(RuleKey.EINKOMMEN, RuleType.GRUNDREGEL_DATA, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> einkommensAbschnitte = new ArrayList<>();
		// Nur ausführen wenn Finanzdaten gesetzt
		// Der {@link FinanzielleSituationRechner} wurde verwendet um das jeweils geltende  Einkommen auszurechnen. Das heisst im DTO ist schon
		// jeweils das zu verwendende Einkommen gesetzt
		FinanzDatenDTO finanzDatenDTOAlleine = betreuung.extractGesuch().getFinanzDatenDTO_alleine();
		FinanzDatenDTO finanzDatenDTOZuZweit = betreuung.extractGesuch().getFinanzDatenDTO_zuZweit();

		if (finanzDatenDTOAlleine != null && finanzDatenDTOZuZweit != null) {
			VerfuegungZeitabschnitt lastAbschnitt;

			// Abschnitt Finanzielle Situation (Massgebendes Einkommen fuer die Gesuchsperiode)
			VerfuegungZeitabschnitt abschnittFinanzielleSituation = new VerfuegungZeitabschnitt(betreuung.extractGesuchsperiode().getGueltigkeit());
			einkommensAbschnitte.add(abschnittFinanzielleSituation);
			lastAbschnitt = abschnittFinanzielleSituation;
			boolean hasEKV1 = false;

			// Einkommensverschlechterung 1: In mind. 1 Kombination eingegeben
			if (finanzDatenDTOAlleine.getDatumVonBasisjahrPlus1() != null || finanzDatenDTOZuZweit.getDatumVonBasisjahrPlus1() != null) {
				LocalDate startEKV1 = finanzDatenDTOAlleine.getDatumVonBasisjahrPlus1() != null ? finanzDatenDTOAlleine.getDatumVonBasisjahrPlus1() : finanzDatenDTOZuZweit.getDatumVonBasisjahrPlus1();
				DateRange rangeEKV1 = new DateRange(startEKV1, betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis());
				VerfuegungZeitabschnitt abschnittEinkommensverschlechterung1 = new VerfuegungZeitabschnitt(rangeEKV1);

				if (finanzDatenDTOAlleine.getDatumVonBasisjahrPlus1() != null) {
					// EKV1 fuer alleine erfasst
					abschnittEinkommensverschlechterung1.setEkv1Alleine(true);
				}
				if (finanzDatenDTOZuZweit.getDatumVonBasisjahrPlus1() != null) {
					// EKV1 fuer zu Zweit erfasst
					abschnittEinkommensverschlechterung1.setEkv1ZuZweit(true);
				}
				einkommensAbschnitte.add(abschnittEinkommensverschlechterung1);
				// Den vorherigen Zeitabschnitt beenden
				lastAbschnitt.getGueltigkeit().endOnDayBefore(abschnittEinkommensverschlechterung1.getGueltigkeit());
				lastAbschnitt = abschnittEinkommensverschlechterung1;
				hasEKV1 = true;
			}

			// Einkommensverschlechterung 2: In mind. 1 Kombination akzeptiert
			if (finanzDatenDTOAlleine.getDatumVonBasisjahrPlus2() != null || finanzDatenDTOZuZweit.getDatumVonBasisjahrPlus2() != null) {
				LocalDate startEKV2 = finanzDatenDTOAlleine.getDatumVonBasisjahrPlus2() != null ? finanzDatenDTOAlleine.getDatumVonBasisjahrPlus2() : finanzDatenDTOZuZweit.getDatumVonBasisjahrPlus2();
				DateRange rangeEKV2 = new DateRange(startEKV2, betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis());
				VerfuegungZeitabschnitt abschnittEinkommensverschlechterung2 = new VerfuegungZeitabschnitt(rangeEKV2);
				abschnittEinkommensverschlechterung2.setEkv1NotExisting(!hasEKV1);

				if (finanzDatenDTOAlleine.getDatumVonBasisjahrPlus2() != null) {
					// EKV2 fuer alleine erfasst
					abschnittEinkommensverschlechterung2.setEkv2Alleine(true);
				}
				if (finanzDatenDTOZuZweit.getDatumVonBasisjahrPlus2() != null) {
					// EKV2 fuer zu Zweit erfasst
					abschnittEinkommensverschlechterung2.setEkv2ZuZweit(true);
				}
				einkommensAbschnitte.add(abschnittEinkommensverschlechterung2);
				// Den vorherigen Zeitabschnitt beenden
				lastAbschnitt.getGueltigkeit().endOnDayBefore(abschnittEinkommensverschlechterung2.getGueltigkeit());
			}
		}
		return einkommensAbschnitte;
	}

	@Override
	public boolean isRelevantForFamiliensituation() {
		return true;
	}

}

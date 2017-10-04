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

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * Sonderregel die nach der eigentlich Berechnung angewendet wird und welche die Zeitabschnitte auf Monate begrenzt
 */
public class MonatsRule extends AbstractEbeguRule {


	public MonatsRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.NO_RULE, RuleType.NO_RULE, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> monatsSchritte = new ArrayList<>();
		for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnitte) {
			LocalDate gueltigAb = zeitabschnitt.getGueltigkeit().getGueltigAb();
			LocalDate gueltigBis = zeitabschnitt.getGueltigkeit().getGueltigBis();
			while (!gueltigAb.isAfter(gueltigBis)) {
				LocalDate endOfMoth = gueltigAb.with(TemporalAdjusters.lastDayOfMonth());
				LocalDate enddate = endOfMoth.isAfter(gueltigBis) ? gueltigBis : endOfMoth;
				VerfuegungZeitabschnitt monatsSchritt = new VerfuegungZeitabschnitt(new DateRange(gueltigAb, enddate));
				monatsSchritt.add(zeitabschnitt);
				monatsSchritte.add(monatsSchritt);
				gueltigAb = monatsSchritt.getGueltigkeit().getGueltigBis().plusDays(1);
			}
		}
		return monatsSchritte;
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		// Keine Regel
	}
}

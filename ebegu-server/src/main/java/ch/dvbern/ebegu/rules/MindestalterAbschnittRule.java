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
import ch.dvbern.ebegu.entities.Betreuungspensum;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Regel für Mindestalter des Kindes:
 * - Erst ab dem 3. Monat besteht ein Anspruch. Ein Kita-Platz kann aber schon vor dem dritten Monat
 * 		beansprucht werden. In diesem Fall wird die Zeit vor dem 3. Monat zum Privattarif berechnet.
 *
 * 	Verweis 16.12.1 Mindestalter
 */
public class MindestalterAbschnittRule extends AbstractAbschnittRule {


	public MindestalterAbschnittRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.MINDESTALTER, RuleType.GRUNDREGEL_DATA, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> betreuungspensumAbschnitte = new ArrayList<>();
		LocalDate geburtsdatumKind = betreuung.getKind().getKindJA().getGeburtsdatum();
		if (geburtsdatumKind != null) {
			Set<BetreuungspensumContainer> betreuungspensen = betreuung.getBetreuungspensumContainers();
			LocalDate geburtsdatumKindStichtag = geburtsdatumKind.plusMonths(3);
			for (BetreuungspensumContainer betreuungspensumContainer : betreuungspensen) {
				Betreuungspensum betreuungspensum = betreuungspensumContainer.getBetreuungspensumJA();
				LocalDate stichtagBetreuung = betreuungspensum.getGueltigkeit().getGueltigAb();
				if (geburtsdatumKindStichtag.isAfter(stichtagBetreuung)) {
					// Kind ist zum Betreuungsbeginn weniger als 3 Moante alt
					VerfuegungZeitabschnitt abschnittZuJung = new VerfuegungZeitabschnitt(new DateRange(stichtagBetreuung, geburtsdatumKindStichtag));
					abschnittZuJung.setKindMinestalterUnterschritten(true);
					betreuungspensumAbschnitte.add(abschnittZuJung);
				}

			}
		}
		return betreuungspensumAbschnitte;
	}
}

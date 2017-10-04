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
import java.util.Set;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungspensum;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

/**
 * Regel bezüglich der Einreichungsfrist des Gesuchs:
 * - Wird ein Gesuch zu spät eingereicht, entfällt der Anspruch auf den Monaten vor dem Einreichen des Gesuchs.
 * - Beispiel: Ein Gesuch wird am 5. September 2017 eingereicht. In diesem Fall ist erst per 1. September 2017
 * ein Anspruch verfügbar.
 * D.h. für die Angebote „Kita“ und „Tageseltern – Kleinkinder“ ist im August kein Anspruch verfügbar.
 * Falls sie einen Platz haben, wird dieser zum privaten Tarif der Kita berechnet.
 * - Für die Angebote Tageseltern–Schulkinder und Tagesstätten entspricht der Anspruch dem gewünschten Pensum.
 * Ihnen wird für den Monat August aber der Volltarif verrechnet.
 * Verweis 16.11 Gesuch zu Speat
 */
public class EinreichungsfristAbschnittRule extends AbstractAbschnittRule {

	public EinreichungsfristAbschnittRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.EINREICHUNGSFRIST, RuleType.GRUNDREGEL_DATA, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> einreichungsfristAbschnitte = new ArrayList<>();
		Gesuch gesuch = betreuung.extractGesuch();
		LocalDate eingangsdatum = gesuch.getEingangsdatum();
		if (gesuch.getTyp().isGesuch() && eingangsdatum != null) {
			Set<BetreuungspensumContainer> betreuungspensen = betreuung.getBetreuungspensumContainers();
			LocalDate firstOfMonthDesEinreichungsMonats = LocalDate.of(eingangsdatum.getYear(), eingangsdatum.getMonth(), 1);
			for (BetreuungspensumContainer betreuungspensumContainer : betreuungspensen) {
				Betreuungspensum betreuungspensum = betreuungspensumContainer.getBetreuungspensumJA();
				if (betreuungspensum.getGueltigkeit().getGueltigAb().isBefore(firstOfMonthDesEinreichungsMonats)) {
					VerfuegungZeitabschnitt verfuegungZeitabschnitt = new VerfuegungZeitabschnitt(betreuungspensum.getGueltigkeit());
					// Der Anspruch beginnt erst am 1. des Monats der Einreichung
					verfuegungZeitabschnitt.getGueltigkeit().setGueltigAb(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb());
					verfuegungZeitabschnitt.getGueltigkeit().setGueltigBis(firstOfMonthDesEinreichungsMonats.minusDays(1));
					verfuegungZeitabschnitt.setZuSpaetEingereicht(true);
					// Sicherstellen, dass nicht der ganze Zeitraum vor dem Einreichungsdatum liegt
					if (verfuegungZeitabschnitt.getGueltigkeit().getGueltigBis().isAfter(verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb())) {
						einreichungsfristAbschnitte.add(verfuegungZeitabschnitt);
					}
				}
			}
		}
		return einreichungsfristAbschnitte;
	}
}

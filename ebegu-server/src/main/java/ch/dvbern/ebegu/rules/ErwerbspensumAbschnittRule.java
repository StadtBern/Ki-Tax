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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Erwerbspensum;
import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

/**
 * Berechnet die hoehe des ErwerbspensumRule eines bestimmten Erwerbspensums
 * Diese Rule muss immer am Anfang kommen, d.h. sie setzt den initialen Anspruch
 * Die weiteren Rules müssen diesen Wert gegebenenfalls korrigieren.
 * Verweis 16.9.2
 */
public class ErwerbspensumAbschnittRule extends AbstractAbschnittRule {

	public ErwerbspensumAbschnittRule(DateRange validityPeriod) {
		super(RuleKey.ERWERBSPENSUM, RuleType.GRUNDREGEL_DATA, validityPeriod);
	}

	@Override
	@Nonnull
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> erwerbspensumAbschnitte = new ArrayList<>();
		Gesuch gesuch = betreuung.extractGesuch();
		if (gesuch.getGesuchsteller1() != null) {
			erwerbspensumAbschnitte.addAll(getErwerbspensumAbschnittForGesuchsteller(gesuch, gesuch.getGesuchsteller1(), false));
		}
		if (gesuch.getGesuchsteller2() != null) {
			erwerbspensumAbschnitte.addAll(getErwerbspensumAbschnittForGesuchsteller(gesuch, gesuch.getGesuchsteller2(), true));
		}
		return erwerbspensumAbschnitte;
	}

	/**
	 * geht durch die Erwerpspensen des Gesuchstellers und gibt Abschnitte zurueck
	 *
	 * @param gesuchsteller Der Gesuchsteller dessen Erwerbspensumcontainers zu Abschnitte konvertiert werden
	 * @param gs2 handelt es sich um gesuchsteller1 -> false oder gesuchsteller2 -> true
	 */
	@Nonnull
	private List<VerfuegungZeitabschnitt> getErwerbspensumAbschnittForGesuchsteller(@Nonnull Gesuch gesuch, @Nonnull GesuchstellerContainer gesuchsteller, boolean gs2) {
		List<VerfuegungZeitabschnitt> ewpAbschnitte = new ArrayList<>();
		Set<ErwerbspensumContainer> ewpContainers = gesuchsteller.getErwerbspensenContainersNotEmpty();
		for (ErwerbspensumContainer erwerbspensumContainer : ewpContainers) {
			Erwerbspensum erwerbspensumJA = erwerbspensumContainer.getErwerbspensumJA();
			final VerfuegungZeitabschnitt zeitabschnitt = toVerfuegungZeitabschnitt(gesuch, erwerbspensumJA, gs2);
			if (zeitabschnitt != null) {
				ewpAbschnitte.add(zeitabschnitt);
			}
		}
		return ewpAbschnitte;
	}

	/**
	 * Konvertiert ein Erwerbspensum in einen Zeitabschnitt von entsprechender dauer und erwerbspensumGS1 (falls gs2=false)
	 * oder erwerpspensuGS2 (falls gs2=true)
	 */
	@Nullable
	private VerfuegungZeitabschnitt toVerfuegungZeitabschnitt(@Nonnull Gesuch gesuch, @Nonnull Erwerbspensum erwerbspensum, boolean gs2) {
		final DateRange gueltigkeit = new DateRange(erwerbspensum.getGueltigkeit());

		// Wir merken uns hier den eingegebenen Wert, auch wenn dieser (mit Zuschlag) über 100% liegt
		if (gs2 && gesuch.isMutation() && gesuch.extractFamiliensituationErstgesuch() != null && gesuch.extractFamiliensituation() != null) {
			if (!gesuch.extractFamiliensituationErstgesuch().hasSecondGesuchsteller() && gesuch.extractFamiliensituation().hasSecondGesuchsteller()) {
				// 1GS to 2GS
				if (gueltigkeit.getGueltigBis().isAfter(gesuch.extractFamiliensituation().getAenderungPer())
					&& gueltigkeit.getGueltigAb().isBefore(gesuch.extractFamiliensituation().getAenderungPer())) {
					gueltigkeit.setGueltigAb(gesuch.extractFamiliensituation().getAenderungPer());
				}
			} else if (gesuch.extractFamiliensituationErstgesuch().hasSecondGesuchsteller() && !gesuch.extractFamiliensituation().hasSecondGesuchsteller()
				&& gueltigkeit.getGueltigAb().isBefore(gesuch.extractFamiliensituation().getAenderungPer())
				&& gueltigkeit.getGueltigBis().isAfter(gesuch.extractFamiliensituation().getAenderungPer())) {
				// 2GS to 1GS
				gueltigkeit.setGueltigBis(gesuch.extractFamiliensituation().getAenderungPer().minusDays(1));
			}
			VerfuegungZeitabschnitt zeitabschnitt = createZeitAbschnittForGS2(gueltigkeit, erwerbspensum.getPensum(), erwerbspensum.getZuschlagsprozent());
			setKategorieZuschlagZumErwerbspensum(erwerbspensum, zeitabschnitt);  //fuer statistik
			return zeitabschnitt;
		}
		if (gs2 && !gesuch.isMutation()) {
			VerfuegungZeitabschnitt zeitabschnitt = createZeitAbschnittForGS2(gueltigkeit, erwerbspensum.getPensum(), erwerbspensum.getZuschlagsprozent());
			setKategorieZuschlagZumErwerbspensum(erwerbspensum, zeitabschnitt);
			return zeitabschnitt;
		}
		if (!gs2) {
			VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(gueltigkeit);
			zeitabschnitt.setErwerbspensumGS1(erwerbspensum.getPensum());
			zeitabschnitt.setZuschlagErwerbspensumGS1(erwerbspensum.getZuschlagsprozent());
			setKategorieZuschlagZumErwerbspensum(erwerbspensum, zeitabschnitt);
			return zeitabschnitt;
		}

		return null;
	}

	private void setKategorieZuschlagZumErwerbspensum(@Nonnull Erwerbspensum erwerbspensum, VerfuegungZeitabschnitt zeitabschnitt) {
		if (erwerbspensum.getZuschlagsprozent() != null && erwerbspensum.getZuschlagsprozent() > 0) {
			zeitabschnitt.setKategorieZuschlagZumErwerbspensum(true);
		}
	}

	@Nonnull
	private VerfuegungZeitabschnitt createZeitAbschnittForGS2(DateRange gueltigkeit, Integer erwerbspensumValue, Integer zuschlag) {
		VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(gueltigkeit);
		zeitabschnitt.setErwerbspensumGS1(null);
		zeitabschnitt.setZuschlagErwerbspensumGS1(null);
		zeitabschnitt.setErwerbspensumGS2(erwerbspensumValue);
		zeitabschnitt.setZuschlagErwerbspensumGS2(zuschlag);
		return zeitabschnitt;
	}
}

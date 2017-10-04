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

import ch.dvbern.ebegu.dto.VerfuegungsBemerkung;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Umsetzung der ASIV Revision: Finanzielle Situation bei Mutation der Familiensituation anpassen
 * <p>
 * Gem. neuer ASIV Verordnung muss bei einem Wechsel von einem auf zwei Gesuchsteller oder umgekehrt die
 * finanzielle Situation ab dem Folgemonat angepasst werden.
 * </p>
 */
public class ZivilstandsaenderungAbschnittRule extends AbstractAbschnittRule {

	public ZivilstandsaenderungAbschnittRule(DateRange validityPeriod) {
		super(RuleKey.ZIVILSTANDSAENDERUNG, RuleType.GRUNDREGEL_DATA, validityPeriod);
	}

	@Override
	@Nonnull
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {

		Gesuch gesuch = betreuung.extractGesuch();
		final List<VerfuegungZeitabschnitt> zivilstandsaenderungAbschnitte = new ArrayList<>();

		// Ueberpruefen, ob die Gesuchsteller-Kardinalität geändert hat. Nur dann muss evt. anders berechnet werden!
		if (gesuch.extractFamiliensituation() != null && gesuch.extractFamiliensituation().getAenderungPer() != null &&
			gesuch.extractFamiliensituation().hasSecondGesuchsteller() != gesuch.extractFamiliensituationErstgesuch().hasSecondGesuchsteller()) {

			// Die Zivilstandsaenderung gilt ab anfang nächstem Monat, die Bemerkung muss aber "per Heirat/Trennung" erfolgen
			final LocalDate ereignistag = gesuch.extractFamiliensituation().getAenderungPer();
			final LocalDate stichtag = ereignistag.plusMonths(1).withDayOfMonth(1);
			// Bemerkung erstellen
			VerfuegungsBemerkung bemerkungContainer;
			if (gesuch.extractFamiliensituation().hasSecondGesuchsteller()) {
				// Heirat
				bemerkungContainer = new VerfuegungsBemerkung(RuleKey.ZIVILSTANDSAENDERUNG, MsgKey.FAMILIENSITUATION_HEIRAT_MSG);
			} else {
				// Trennung
				bemerkungContainer = new VerfuegungsBemerkung(RuleKey.ZIVILSTANDSAENDERUNG, MsgKey.FAMILIENSITUATION_TRENNUNG_MSG);
			}

			VerfuegungZeitabschnitt abschnittVorMutation = new VerfuegungZeitabschnitt(new DateRange(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb(), ereignistag.minusDays(1)));
			abschnittVorMutation.setHasSecondGesuchstellerForFinanzielleSituation(gesuch.extractFamiliensituationErstgesuch().hasSecondGesuchsteller());
			zivilstandsaenderungAbschnitte.add(abschnittVorMutation);

			VerfuegungZeitabschnitt abschnittVorStichtag = new VerfuegungZeitabschnitt(new DateRange(ereignistag, stichtag.minusDays(1)));
			abschnittVorStichtag.setHasSecondGesuchstellerForFinanzielleSituation(gesuch.extractFamiliensituationErstgesuch().hasSecondGesuchsteller());
			abschnittVorStichtag.addBemerkung(bemerkungContainer);
			zivilstandsaenderungAbschnitte.add(abschnittVorStichtag);

			VerfuegungZeitabschnitt abschnittNachMutation = new VerfuegungZeitabschnitt(new DateRange(stichtag, gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis()));
			abschnittNachMutation.setHasSecondGesuchstellerForFinanzielleSituation(gesuch.extractFamiliensituation().hasSecondGesuchsteller());
			abschnittNachMutation.addBemerkung(bemerkungContainer);
			zivilstandsaenderungAbschnitte.add(abschnittNachMutation);
		} else {
			VerfuegungZeitabschnitt abschnittOhneMutation = new VerfuegungZeitabschnitt(gesuch.getGesuchsperiode().getGueltigkeit());
			abschnittOhneMutation.setHasSecondGesuchstellerForFinanzielleSituation(gesuch.extractFamiliensituation().hasSecondGesuchsteller());
			zivilstandsaenderungAbschnitte.add(abschnittOhneMutation);
		}
		return zivilstandsaenderungAbschnitte;
	}

	@Override
	public boolean isRelevantForFamiliensituation() {
		return true;
	}
}

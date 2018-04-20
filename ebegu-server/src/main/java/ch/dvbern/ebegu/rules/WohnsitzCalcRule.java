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

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;

/**
 * Regel für Wohnsitz in Bern (Zuzug und Wegzug):
 * - Durch Adresse definiert
 * - Anspruch vom ersten Tag des Zuzugs
 * - Anspruch bis 2 Monate nach Wegzug, auf Ende Monat
 * Verweis 16.8 Der zivilrechtliche Wohnsitz
 */
public class WohnsitzCalcRule extends AbstractCalcRule {

	public WohnsitzCalcRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.WOHNSITZ, RuleType.REDUKTIONSREGEL, validityPeriod);
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		if (betreuung.getBetreuungsangebotTyp().isJugendamt()) {
			if (areNotInBern(betreuung, verfuegungZeitabschnitt)) {
				verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(0);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.WOHNSITZ, MsgKey.WOHNSITZ_MSG);
			}

		}
	}

	/**
	 * Zuerst schaut ob es eine Aenderung in der Familiensituation gab. Dementsprechend nimmt es die richtige Familiensituation
	 * um zu wissen ob es ein GS2 gibt, erst dann wird es geprueft ob die Adressen von GS1 oder GS2 in Bern sind
	 */
	private boolean areNotInBern(Betreuung betreuung, VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		boolean hasSecondGesuchsteller = false;
		final Gesuch gesuch = betreuung.extractGesuch();
		if (!gesuch.isMutation() || (gesuch.extractFamiliensituation() != null && gesuch.extractFamiliensituation().getAenderungPer() != null
			&& !gesuch.extractFamiliensituation().getAenderungPer().isAfter(verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb()))) {

			hasSecondGesuchsteller = gesuch.extractFamiliensituation().hasSecondGesuchsteller();
		} else if (gesuch.extractFamiliensituationErstgesuch() != null) {
			hasSecondGesuchsteller = gesuch.extractFamiliensituationErstgesuch().hasSecondGesuchsteller();
		}
		return (hasSecondGesuchsteller
			&& verfuegungZeitabschnitt.isWohnsitzNichtInGemeindeGS1()
			&& verfuegungZeitabschnitt.isWohnsitzNichtInGemeindeGS2())
			|| (!hasSecondGesuchsteller
			&& verfuegungZeitabschnitt.isWohnsitzNichtInGemeindeGS1());

	}

}

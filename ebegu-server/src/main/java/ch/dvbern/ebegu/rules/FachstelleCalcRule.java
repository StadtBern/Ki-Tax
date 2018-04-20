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
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.MathUtil;

/**
 * Regel für die Betreuungspensen. Sie beachtet:
 * - Anspruch aus Betreuungspensum darf nicht höher sein als Erwerbspensum
 * - Nur relevant für Kita, Tageseltern-Kleinkinder, die anderen bekommen so viel wie sie wollen
 * - Falls Kind eine Fachstelle hat, gilt das Pensum der Fachstelle
 * Verweis 16.9.3
 */
public class FachstelleCalcRule extends AbstractCalcRule {

	public FachstelleCalcRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.FACHSTELLE, RuleType.GRUNDREGEL_CALC, validityPeriod);
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		// Ohne Fachstelle: Wird in einer separaten Rule behandelt
		if (betreuung.getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()) {
			int pensumFachstelle = verfuegungZeitabschnitt.getFachstellenpensum();
			int roundedPensumFachstelle = MathUtil.roundIntToTens(pensumFachstelle);
			if (roundedPensumFachstelle > 0) {
				// Anspruch ist immer genau das Pensum der Fachstelle, ausser das Restpensum lässt dies nicht mehr zu
				verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(roundedPensumFachstelle);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.FACHSTELLE, MsgKey.FACHSTELLE_MSG);
			}
		}
	}
}

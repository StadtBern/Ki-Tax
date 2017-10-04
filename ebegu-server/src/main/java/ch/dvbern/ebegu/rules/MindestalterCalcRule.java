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
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;

/**
 * Regel für Mindestalter des Kindes:
 * - Erst ab dem 3. Monat besteht ein Anspruch. Ein Kita-Platz kann aber schon vor dem dritten Monat
 * 		beansprucht werden. In diesem Fall wird die Zeit vor dem 3. Monat zum Privattarif berechnet.
 *
 * 	Verweis 16.12.1 Mindestalter
 */
public class MindestalterCalcRule extends AbstractCalcRule {


	public MindestalterCalcRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.MINDESTALTER, RuleType.REDUKTIONSREGEL, validityPeriod);
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		if (betreuung.getBetreuungsangebotTyp().isJugendamt()) {
			if (verfuegungZeitabschnitt.isKindMinestalterUnterschritten()) {
				verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(0);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.MINDESTALTER, MsgKey.MINDESTALTER_MSG);
			}
		}
	}
}

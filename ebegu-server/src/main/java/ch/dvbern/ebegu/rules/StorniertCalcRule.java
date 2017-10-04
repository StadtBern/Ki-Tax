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
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;

/**
 * Regel für die Kuendigung vor Eintritt in die Institution. Sie beachtet:
 * - Bemerkung wird hinzugefügt wenn Status STORNIERT ist
 * - Pensum ist schon richtig gesezt (0) bei Kita
 */
public class StorniertCalcRule extends AbstractCalcRule {

	public StorniertCalcRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.STORNIERT, RuleType.GRUNDREGEL_CALC, validityPeriod);
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		// Bei Betreuungen mit status STORNIERT wird Bemerkung hinzugefügt
		if (Betreuungsstatus.STORNIERT.equals(betreuung.getBetreuungsstatus())) {
			verfuegungZeitabschnitt.addBemerkung(RuleKey.STORNIERT, MsgKey.STORNIERT_MSG);
		}
	}
}

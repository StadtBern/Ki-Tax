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
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.MathUtil;

import javax.annotation.Nonnull;

/**
 * Regel: Es wird geprueft ob das Kind im gleichen Haushalt wie die Eltern wohnt. Sollte es der Fall sein, wird
 * die eingegebene Prozentzahl als maximaler Wert fuer das Betreuungspensum gesetzt.
 */
public class WohnhaftImGleichenHaushaltCalcRule extends AbstractCalcRule {

	public WohnhaftImGleichenHaushaltCalcRule(DateRange validityPeriod) {
		super(RuleKey.WOHNHAFT_IM_GLEICHEN_HAUSHALT, RuleType.REDUKTIONSREGEL, validityPeriod);
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		if (betreuung.getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()) {
			if (betreuung.getKind() != null && betreuung.getKind().getKindJA() != null) {
				final Kind kindJA = betreuung.getKind().getKindJA();
				if (kindJA.getWohnhaftImGleichenHaushalt() != null) {
					int pensumGleicherHaushalt = MathUtil.roundIntToTens(kindJA.getWohnhaftImGleichenHaushalt());
					int anspruch = verfuegungZeitabschnitt.getAnspruchberechtigtesPensum();
					if (pensumGleicherHaushalt < anspruch) {
						anspruch = pensumGleicherHaushalt;
						verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(anspruch);
						verfuegungZeitabschnitt.addBemerkung(RuleKey.WOHNHAFT_IM_GLEICHEN_HAUSHALT, MsgKey.WOHNHAFT_MSG, kindJA.getWohnhaftImGleichenHaushalt());
					}
				}
			}
		}
	}
}

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

package ch.dvbern.ebegu.vorlagen.finanziellesituation;

import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.util.EbeguUtil;
import org.apache.commons.lang3.Validate;

public final class FinanzSituationPrintGesuchstellerHelper {

	/**
	 * Konstruktor
	 */
	private FinanzSituationPrintGesuchstellerHelper() {
		// NOP
	}

	/**
	 * Erstellt das FinanzSituationGesuchsteller fuer Gesuchsteller 1
	 *
	 * @return FinanzSituationGesuchsteller
	 */
	@Nullable
	public static FinanzSituationPrintGesuchsteller getFinanzSituationGesuchsteller1(Gesuch gesuch) {
		if (EbeguUtil.isFinanzielleSituationRequired(gesuch)) {
			GesuchstellerContainer gesuchsteller1 = gesuch.getGesuchsteller1();
			Validate.notNull(gesuchsteller1);
			Validate.notNull(gesuchsteller1.getFinanzielleSituationContainer());
			FinanzSituationPrintGesuchsteller finanzSituationPrintGesuchsteller = new FinanzSituationPrintGesuchsteller(gesuchsteller1.getFinanzielleSituationContainer().getFinanzielleSituationJA(),
				gesuchsteller1.getEinkommensverschlechterungContainer() != null ? gesuchsteller1.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1() : null,
				gesuchsteller1.getEinkommensverschlechterungContainer() != null ? gesuchsteller1.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2() : null,
				gesuch.extractEinkommensverschlechterungInfo());

			return finanzSituationPrintGesuchsteller;
		}
		return null;
	}

	/**
	 * Erstellt das FinanzSituationGesuchsteller fuer Gesuchsteller 2
	 *
	 * @return FinanzSituationGesuchsteller
	 */
	@Nullable
	public static FinanzSituationPrintGesuchsteller getFinanzSituationGesuchsteller2(Gesuch gesuch) {

		GesuchstellerContainer gesuchsteller2 = gesuch.getGesuchsteller2();
		if (gesuchsteller2 != null && EbeguUtil.isFinanzielleSituationRequired(gesuch)) {
			Validate.notNull(gesuchsteller2.getFinanzielleSituationContainer());
			FinanzSituationPrintGesuchsteller finanzSituationPrintGesuchsteller2 = new FinanzSituationPrintGesuchsteller(
				gesuchsteller2.getFinanzielleSituationContainer().getFinanzielleSituationJA(), //
				gesuchsteller2.getEinkommensverschlechterungContainer() != null ? gesuchsteller2.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1() : null, //
				gesuchsteller2.getEinkommensverschlechterungContainer() != null ? gesuchsteller2.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2() : null, //
				gesuch.extractEinkommensverschlechterungInfo());
			return finanzSituationPrintGesuchsteller2;
		}
		return null;
	}
}

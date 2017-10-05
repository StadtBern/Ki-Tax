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

import ch.dvbern.ebegu.entities.Einkommensverschlechterung;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfo;
import ch.dvbern.ebegu.entities.FinanzielleSituation;

/**
 * Klasse um die relevante Daten des Gesuch fuer Berechnung und Darstellung festzuhalten
 */
public class FinanzSituationPrintGesuchsteller {

	private final FinanzielleSituation finanzielleSituation;
	private final Einkommensverschlechterung einkommensverschlechterung1;
	private final Einkommensverschlechterung einkommensverschlechterung2;
	private final EinkommensverschlechterungInfo einkommensverschlechterungInfo;

	public FinanzSituationPrintGesuchsteller(FinanzielleSituation finanzielleSituation, Einkommensverschlechterung einkommensverschlechterung1,
		Einkommensverschlechterung einkommensverschlechterung2, EinkommensverschlechterungInfo einkommensverschlechterungInfo) {
		this.finanzielleSituation = finanzielleSituation;
		this.einkommensverschlechterung1 = einkommensverschlechterung1;
		this.einkommensverschlechterung2 = einkommensverschlechterung2;
		this.einkommensverschlechterungInfo = einkommensverschlechterungInfo;
	}

	public EinkommensverschlechterungInfo getEinkommensverschlechterungInfo() {

		return einkommensverschlechterungInfo;
	}

	/**
	 * @return FinanzielleSituation
	 */
	public FinanzielleSituation getFinanzielleSituation() {

		return finanzielleSituation;
	}

	/**
	 * @return Einkommensverschlechterung erste einkommensverschlechterung
	 */
	public Einkommensverschlechterung getEinkommensverschlechterung1() {

		return einkommensverschlechterung1;
	}

	/**
	 * @return Einkommensverschlechterung  zweite einkommensverschlechterung
	 */
	public Einkommensverschlechterung getEinkommensverschlechterung2() {

		return einkommensverschlechterung2;
	}

}

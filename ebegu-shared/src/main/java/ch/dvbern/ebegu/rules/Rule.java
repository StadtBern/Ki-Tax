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
import java.util.List;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;

/**
 * Interface für alle Berechnungs-Regeln in Ki-Tax.
 */
public interface Rule {

	/**
	 * @return Datum von dem an die Regel gilt
	 */
	@Nonnull
	LocalDate validFrom();

	/**
	 * @return Datum bis zu dem die Regel gilt
	 */
	@Nonnull
	LocalDate validTo();

	/**
	 * @return true wenn die Regel am Strichtag gueltig sit
	 */
	boolean isValid(@Nonnull LocalDate stichtag);

	/**
	 * @return den {@link RuleType} Enumwert dieser Regel
	 */
	@Nonnull
	RuleType getRuleType();

	/**
	 * @return einzigartiger Key fuer diese Regel
	 */
	@Nonnull
	RuleKey getRuleKey();

	/**
	 * Diese Methode fuehrt die eigentliche Berechnung durch die von der Regel abgebildet wird
	 *
	 * @param betreuung Die Betreuung fuer die Berechnet wird
	 * @param zeitabschnitte Die Zeitabschnitte die bereits ermittelt wurden
	 * @return gemergete Liste von bestehenden und neu berechneten Zeitabschnitten
	 */
	@Nonnull
	List<VerfuegungZeitabschnitt> calculate(Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte);

	/**
	 * Gibt zurueck, ob die Regel fuer die Berechnung der Familiensituation (Fam-Groesse, Einkommen, Abzug fuer Fam-Groesse)
	 * relevant ist
	 */
	boolean isRelevantForFamiliensituation();
}

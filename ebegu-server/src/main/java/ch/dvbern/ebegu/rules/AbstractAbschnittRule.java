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
import ch.dvbern.ebegu.types.DateRange;

/**
 * Wir teilen die Regeln noch auf so dass eine einzelne Regel grundsaetzlich entweder nur neue Abschnitte macht oder
 * nur Daten berechnet und setzt. Dadurch bekommen wir mehr Kontrolle wann was gemacht wird.
 * Die AbstractEbeguRule definiert aber jeweils beide Schritte. Daher machen wir jeweils noch eine Abstract rule die nichts macht
 * fuer den nicht benoetigten Schritt
 */
public abstract class AbstractAbschnittRule extends AbstractEbeguRule {

	public AbstractAbschnittRule(@Nonnull RuleKey ruleKey, @Nonnull RuleType ruleType, @Nonnull DateRange validityPeriod) {
		super(ruleKey, ruleType, validityPeriod);
	}

	//Subklassen dieser Abstrakten Klasse benoetigen diese Methode nicht da sie nur Abschnitte erstellen. Daher hier NOP
	@Override
	protected final void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		//NOP
	}
}

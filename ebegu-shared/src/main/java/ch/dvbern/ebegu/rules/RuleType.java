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

/**
 * Die Regeln lassen sich in verschiedene Typen einteilen,
 */
public enum RuleType {

	/**
	 * Typ fuer Regeln die nicht im normalen Regelsystem laufen sondern ausserhalb angestossen werden
	 */
	NO_RULE,
	/**
	 * Regel die dazu verwendet wird die Daten die fuer die Anspruch-Berechnung relevant sind zu setzen
	 */
	GRUNDREGEL_DATA,
	GRUNDREGEL_CALC,
	/**
	 * Typ fuer Regeln die keine neuen Daten berechnen oder Zeitabschnitte einfuegen sondern nur
	 * Reduktionen des Arbeitspensums durchfuehren.
	 */
	REDUKTIONSREGEL

}

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
 * Dieses Enum definiert die moeglichen Regeln. Grundsaetzlich gibt es fuer jede Dokumentierte Regel
 * einen solchen RuleeKey. Dieser wird dann verwendet um allfaellige Abschnitt und Calc rules dieser Regel
 * zuzuordnen.
 */
public enum RuleKey {

	NO_RULE, // Verwendet für diverse Verarbeitungsschritte (z.B. Monatsschnitte machen)

	ERWERBSPENSUM,
	BETREUUNGSPENSUM,
	FACHSTELLE,
	/**
	 * Regel 4.2 definiert dass kein Anspruch besteht wenn das massgebende Einkommen zu hoch ist
	 */
	EINKOMMEN,

	ABWESENHEIT,
	BETREUUNGSANGEBOT_TYP,
	MINDESTALTER,
	WOHNSITZ,
	EINREICHUNGSFRIST,
	FAMILIENSITUATION,
	WOHNHAFT_IM_GLEICHEN_HAUSHALT,
	RESTANSPRUCH,
	ANSPRUCHSBERECHNUNGSREGELN_MUTATIONEN,
	STORNIERT,
	ZIVILSTANDSAENDERUNG

}

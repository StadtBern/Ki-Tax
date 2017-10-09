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

package ch.dvbern.ebegu.enums;

/**
 * Keys fuer die Application Properties die wir in der DB speichern
 */
public enum ApplicationPropertyKey {

	/**
	 * Wenn true gibt der Evaluator seine Debugmeldungen in das log aus.
	 */
	EVALUATOR_DEBUG_ENABLED,

	/**
	 * Damit wir Test/Produktion leichter unterscheiden koennen kann man die Hintergrundfarbe einstellen
	 */
	BACKGROUND_COLOR,

	/**
	 * Wenn eine Nachricht zu einem Fall eintrifft, welcher noch keinen Verantwortlichen hat, so soll diese an den
	 * Default-Verantwortlichen geschickt werden.
	 */
	DEFAULT_VERANTWORTLICHER,

	/**
	 * <Dbtr><Nm> Name des Zahlungspflichtigen
	 */
	DEBTOR_NAME,

	/**
	 * <IBAN> IBAN des Zahlungspflichtigen?
	 */
	DEBTOR_IBAN,

	/**
	 * <BIC> BIC des Zahlungspflichtigen?
	 */
	DEBTOR_BIC,

	/**
	 * <ChrgsAcct> <IBAN> IBAN Belastungskonto Gebühren?
	 */
	DEBTOR_IBAN_GEBUEHREN,

	/**
	 * Anzahl Tage nach Erstellungsdatum bis der GS gewarnt wird, wenn er nicht freigibt
	 */
	ANZAHL_TAGE_BIS_WARNUNG_FREIGABE,

	/**
	 * Anzahl Tage nach Freigabe bis der GS gewarnt wird, wenn er Quittung nicht schickt
	 */
	ANZAHL_TAGE_BIS_WARNUNG_QUITTUNG,

	/**
	 * Anzahl Tage nach Warnung bis Gesuch geloescht wird, wenn er nicht freigibt
	 */
	ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_FREIGABE,

	/**
	 * Anzahl Tage nach Warnung bis Gesuch geloescht wird, wenn er Quittung nicht schickt
	 */
	ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_QUITTUNG;
}

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

package ch.dvbern.ebegu.services;

/**
 * Service fuer diverse Admin-Aufgaben.
 * Im Moment nur fuer internen Gebrauch, d.h. die Methoden werden nirgends im Code aufgerufen, koennen aber bei Bedarf
 * schnell irgendwo angehaengt werden.
 */
public interface AdministrationService {

	String MANDANT_ID_BERN = "e3736eb8-6eef-40ef-9e52-96ab48d8f220";

	int COL_TRAEGERSCHAFT_ID = 0;
	int COL_TRAEGERSCHAFT_NAME = 1;
	int COL_TRAEGERSCHAFT_MAIL = 2;
	int COL_INSTITUTION_ID = 3;
	int COL_INSTITUTION_NAME = 4;
	int COL_STRASSE = 5;
	int COL_HAUSNUMMER = 6;
	int COL_PLZ = 7;
	int COL_ORT = 8;
	int COL_ZUSATZZEILE = 9;
	int COL_INSTITUTION_MAIL = 10;
	int COL_STAMMDATEN_ID = 11;
	int COL_ANGEBOT = 12;
	int COL_IBAN = 13;
	int COL_OEFFNUNGSSTUNDEN = 14;
	int COL_OEFFNUNGSTAGE = 15;


	/**
	 * Erstellt ein SQL-Skript mit Inserts/Updates.
	 * Input ist ein Excel, welches mit den aktuell vorhandenen Daten verglichen wird
	 */
	void createSQLSkriptInstutionsstammdaten();

	/**
	 * Erstellt ein Excel der aktuell vorhandenen Daten.
	 */
	void exportInstitutionsstammdaten();
}

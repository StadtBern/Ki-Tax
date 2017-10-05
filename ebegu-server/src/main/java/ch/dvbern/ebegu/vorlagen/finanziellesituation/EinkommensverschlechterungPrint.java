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

public interface EinkommensverschlechterungPrint extends FinanzDatenPrint {
	// ****************************************************************************************************************
	// Achtung, die Methodennamen in dieser Klassen duerfen nicht veraendert werden. Sie muessen identisch sein
	// mit den Platzhaltern im Word-Template!
	// ****************************************************************************************************************

	/**
	 * @return Einkommenverschlescherungsjahr
	 */
	String getEinkommensverschlechterungJahr();

	/**
	 * @return das Ereingis eintritt
	 */
	String getEreigniseintritt();

	/**
	 * @return Grund des Einkommensverschlechterung
	 */
	String getGrund();

	/**
	 * @return true wenn der Ereigniseintritt nicht null oder empty ist
	 */
	boolean isExistEreigniseintritt();

	/**
	 * @return true wenn der Grund nicht null oder empty ist
	 */
	boolean isExistGrund();
}

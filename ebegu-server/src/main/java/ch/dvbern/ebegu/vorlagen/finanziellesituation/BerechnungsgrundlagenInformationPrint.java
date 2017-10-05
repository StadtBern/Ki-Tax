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

import java.util.List;

public interface BerechnungsgrundlagenInformationPrint {
	// ****************************************************************************************************************
	// Achtung, die Methodennamen in dieser Klassen duerfen nicht veraendert werden. Sie muessen identisch sein
	// mit den Platzhaltern im Word-Template!
	// ****************************************************************************************************************

	/**
	 * @return Name des Gesuchsteller1
	 */
	String getGesuchsteller1Name();

	/**
	 * @return Name des Gesuchsteller2
	 */
	String getGesuchsteller2Name();

	/**
	 * @return true falls das Gesuchsteller 2 vorhanden ist
	 */
	boolean isExistGesuchsteller2();

	/**
	 * @return true das Einkommenverschleschterung 1 existiert
	 */
	boolean isExistEv1();

	/**
	 * @return true das Einkommenverschleschterung 2 existiert
	 */
	boolean isExistEv2();

	/**
	 * @return die Finanzsituation {@link FinanzielleSituationPrint}
	 */
	FinanzielleSituationPrint getFinanz();

	/**
	 * @return das Einkommenverschleschterung {@link EinkommensverschlechterungPrint}
	 */
	EinkommensverschlechterungPrint getEv1();

	/**
	 * @return das Einkommenverschleschterung {@link EinkommensverschlechterungPrint}
	 */
	EinkommensverschlechterungPrint getEv2();

	/**
	 * @return die Berechnungsblaetter
	 */
	List<BerechnungsblattPrint> getBerechnungsblatt();

	/**
	 * @return true falls Berechnungsblatt ausgedruchkt werden muss
	 */
	boolean isPrintBerechnungsBlaetter();
}

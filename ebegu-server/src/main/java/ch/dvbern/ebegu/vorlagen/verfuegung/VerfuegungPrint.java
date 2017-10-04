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

package ch.dvbern.ebegu.vorlagen.verfuegung;

import java.util.List;

import ch.dvbern.ebegu.vorlagen.AufzaehlungPrint;

/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 12.08.2016
*/
public interface VerfuegungPrint {

	// ****************************************************************************************************************
	// Achtung, die Methodennamen in dieser Klassen duerfen nicht veraendert werden. Sie muessen identisch sein
	// mit den Platzhaltern im Word-Template!
	// ****************************************************************************************************************

	String getAngebot();

	String getInstitution();

	String getReferenznummer();

	String getVerfuegungsdatum();

	String getKindNameVorname();

	String getKindGeburtsdatum();

	String getKitaBezeichnung();

	String getAnspruchAb();

	String getAnspruchBis();

	List<VerfuegungZeitabschnittPrint> getVerfuegungZeitabschnitt();

	/**
	 * @return die Bemerkung
	 */
	List<AufzaehlungPrint> getManuelleBemerkungen();

	/**
	 * @return true falls Pensum groesser 0 ist
	 */
	boolean isPensumGrosser0();

	/**
	 * @return true wenn Pensum gleich 0 ist
	 */
	boolean isPensumIst0();

	/**
	 * @return true falls eine Vorgänger-Verfügung besteht
	 */
	boolean isVorgaengerVerfuegt();

	/**
	 * @return true ob die Bermerkungen ausgedruckt werden muessen
	 */
	boolean isPrintManuellebemerkung();

}

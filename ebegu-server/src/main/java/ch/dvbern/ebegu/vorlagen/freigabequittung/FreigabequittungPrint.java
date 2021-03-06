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

package ch.dvbern.ebegu.vorlagen.freigabequittung;

import java.io.IOException;
import java.util.List;

import ch.dvbern.ebegu.vorlagen.AufzaehlungPrint;
import ch.dvbern.lib.doctemplate.docx.DocxImage;

public interface FreigabequittungPrint {

	// ****************************************************************************************************************
	// Achtung, die Methodennamen in dieser Klassen duerfen nicht veraendert werden. Sie muessen identisch sein
	// mit den Platzhaltern im Word-Template!
	// ****************************************************************************************************************

	DocxImage getBarcodeImage() throws IOException;

	String getAdresseGS1();

	boolean isAddresseGS2();

	String getAdresseGS2();

	List<BetreuungsTabellePrint> getBetreuungen();

	List<AufzaehlungPrint> getUnterlagen();

	boolean isWithoutUnterlagen();

	String getFristverlaengerung();

	String getFristverlaengerungTitle();
}

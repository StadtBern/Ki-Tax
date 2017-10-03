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

package ch.dvbern.ebegu.tests.util;

import org.junit.rules.TemporaryFolder;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 25/11/2016.
 */
public class UnitTestTempFolder extends TemporaryFolder {

	/**
	 * Erstellt das byte Dokument in einem temp File in einem temp folder
	 * <p>
	 * Standardmassig werden die temp files werden nach dem Test <b>sofort wieder geloescht</b>, oder ein Pfad kann mit
	 * System Property 'persistTestDateienPfad' beim Test start definiert
	 * <p>
	 * Test Dateien werden geöffnet wenn System Property 'testDateienOeffnen' = true ist.
	 *
	 * @param data
	 * @param fileName
	 * @return das Temp file oder <code>null</code>
	 * @throws IOException
	 */
	public File writeToTempDir(final byte[] data, final String fileName) throws IOException {

		String persistPfad = System.getProperty("persistTestDateienPfad");
		File tempFile = null;

		FileOutputStream fos = null;
		try {
			// create temp file in junit temp folder
			if (persistPfad == null)
				tempFile = newFile(fileName);
			else
				tempFile = new File(persistPfad, fileName);

			System.out.println("Writing tempfile to: " + tempFile);
			fos = new FileOutputStream(tempFile);
			fos.write(data);
			fos.close();

			// File external oeffnen
			if (Boolean.getBoolean("testDateienOeffnen"))
				openPDF(tempFile);
		} finally {
			if (fos != null) {
				fos.close();
			}

		}
		return tempFile;
	}

	private static void openPDF(File file) {

		try {
			Desktop.getDesktop().open(file);
		} catch (IOException ex) {
			// no application registered for PDFs
		}
	}

}

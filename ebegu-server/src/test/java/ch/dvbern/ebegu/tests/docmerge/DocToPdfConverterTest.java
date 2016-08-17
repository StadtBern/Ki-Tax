package ch.dvbern.ebegu.tests.docmerge;

import ch.dvbern.ebegu.services.vorlagen.GeneratePDFDocumentHelper;
import ch.dvbern.ebegu.services.vorlagen.VerfuegungPrintMergeSource;
import ch.dvbern.lib.doctemplate.docx.DOCXMergeEngine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class DocToPdfConverterTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	/**
	 * Sets the default locale.
	 */
	@Before
	public void setLocale() {
		Locale.setDefault(new Locale("de", "CH"));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test_PensumGroesser0Ist() throws Exception {
		DOCXMergeEngine docxME = new DOCXMergeEngine("Verfuegungsmuster");
		InputStream is = this.getClass().getResourceAsStream("/vorlagen/Verfuegungsmuster.docx");
		byte[] pdf = new GeneratePDFDocumentHelper().generatePDFDocument((docxME.getDocument(is, new VerfuegungPrintMergeSource(new VerfuegungsmusterDummyImp()))));
		Assert.assertNotNull(pdf);
		is.close();
		File file = writeToTempDir(pdf, "testdokument.pdf");
		Assert.assertNotNull(file);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test_Pensum0Ist() throws Exception {
		DOCXMergeEngine docxME = new DOCXMergeEngine("Verfuegungsmuster");
		InputStream is = this.getClass().getResourceAsStream("/vorlagen/Verfuegungsmuster.docx");
		byte[] pdf = new GeneratePDFDocumentHelper().generatePDFDocument((docxME.getDocument(is, new VerfuegungPrintMergeSource(new VerfuegungsmusterPensum0IstDummyImp()))));
		Assert.assertNotNull(pdf);
		is.close();
		File file = writeToTempDir(pdf, "testdokument.pdf");
		Assert.assertNotNull(file);
	}

	/**
	 * Erstellt das byte Dokument in einem temp File in einem temp folder
	 * <p/>
	 * <b>ACHTUNG: </b> die temp files werden nach dem Test <b>sofort wieder geloescht</b>
	 *
	 * @return das Temp file oder <code>null</code>
	 * @throws IOException
	 */
	protected final File writeToTempDir(final byte[] data, final String fileName) throws IOException {
		File tempFile = null;
		FileOutputStream fos = null;
		try {
			// create temp file in junit temp folder
			tempFile = tempFolder.newFile(fileName);
			System.out.println("Writing tempfile to: " + tempFile);
			fos = new FileOutputStream(tempFile);
			fos.write(data);
			fos.close();
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
		return tempFile;
	}
}

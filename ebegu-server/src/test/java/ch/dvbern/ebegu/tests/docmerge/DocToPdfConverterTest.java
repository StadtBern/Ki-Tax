package ch.dvbern.ebegu.tests.docmerge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import ch.dvbern.ebegu.services.vorlagen.GeneratePDFDocumentHelper;
import ch.dvbern.ebegu.services.vorlagen.VerfuegungsmusterMergeSource;
import ch.dvbern.lib.doctemplate.docx.DOCXMergeEngine;

@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class DocToPdfConverterTest {

	private static final String CHARSET = "ISO-8859-1";

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

		byte[] pdf = new GeneratePDFDocumentHelper().generatePDFDocument((docxME.getDocument(is, new VerfuegungsmusterMergeSource(new VerfuegungsmusterDummyImp()))));

		is.close();

		writeToTempDir(pdf, "testdokument.pdf");

	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test_Pensum0Ist() throws Exception {

		DOCXMergeEngine docxME = new DOCXMergeEngine("Verfuegungsmuster");

		InputStream is = this.getClass().getResourceAsStream("/vorlagen/Verfuegungsmuster.docx");

		byte[] pdf = new GeneratePDFDocumentHelper().generatePDFDocument((docxME.getDocument(is, new VerfuegungsmusterMergeSource(new VerfuegungsmusterPensum0IstDummyImp()))));

		is.close();

		writeToTempDir(pdf, "testdokument.pdf");

	}

	/**
	 * Erstellt das byte Dokument in einem temp File in einem temp folder
	 * <p/>
	 * <b>ACHTUNG: </b> die temp files werden nach dem Test <b>sofort wieder geloescht</b>
	 *
	 * @param data
	 * @param fileName
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

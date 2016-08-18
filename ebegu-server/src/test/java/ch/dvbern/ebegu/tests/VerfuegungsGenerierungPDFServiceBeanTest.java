package ch.dvbern.ebegu.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.VerfuegungsGenerierungPDFService;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.ebegu.tets.TestDataUtil;

/**
 * Test der die vom JA gemeldeten Testfaelle ueberprueft.
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class VerfuegungsGenerierungPDFServiceBeanTest extends AbstractEbeguTest {

	protected BetreuungsgutscheinEvaluator evaluator;

	@Inject
	private VerfuegungsGenerierungPDFService verfuegungsGenerierungPDFService;

	@Inject
	private GesuchService gesuchService;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {

		return createTestArchive();
	}

	@Before
	public void setUpCalcuator() {

		evaluator = AbstractBGRechnerTest.createEvaluator();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGeneriereVerfuegungsmuster() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		Gesuch gesuch = testfall.createGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, AbstractBGRechnerTest.getParameter());

		List<byte[]> bytesList = verfuegungsGenerierungPDFService.generiereVerfuegungen(gesuch);

		int i = 0;
		for (byte[] b : bytesList) {
			writeToTempDir(b, "testdokument" + i + ".pdf");
			i++;
		}
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

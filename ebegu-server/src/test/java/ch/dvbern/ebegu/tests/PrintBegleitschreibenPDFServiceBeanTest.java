package ch.dvbern.ebegu.tests;

import java.awt.Desktop;
import java.io.File;
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
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.PrintBegleitschreibenPDFService;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.ebegu.testfaelle.Testfall02_FeutzYvonne;
import ch.dvbern.ebegu.tets.TestDataUtil;

/**
 * Test der die vom JA gemeldeten Testfaelle ueberprueft.
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class PrintBegleitschreibenPDFServiceBeanTest extends AbstractEbeguTest {

	protected BetreuungsgutscheinEvaluator evaluator;

	@Inject
	private PrintBegleitschreibenPDFService printBegleitschreibenPDFService;

	@Inject
	private GesuchService gesuchService;

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
	public void testPrintBegleitschreiben() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		Gesuch gesuch = testfall.createGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, AbstractBGRechnerTest.getParameter());

		byte[] bytes = printBegleitschreibenPDFService.printBegleitschreiben(gesuch);

		writeToTempDir(bytes, "BegleitschreibenWaelti.pdf");
		// openPDF(file);

	}

	@Test
	public void testPrintBegleitschreibenZwiGesuchsteller() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen()); // sollte tagi aareg sein
																								// ist aber hier egal

		Testfall02_FeutzYvonne testfall = new Testfall02_FeutzYvonne(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		Gesuch gesuch = testfall.createGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		// evaluator.evaluate(gesuch, AbstractBGRechnerTest.getParameter());

		byte[] bytes = printBegleitschreibenPDFService.printBegleitschreiben(gesuch);

		File file = writeToTempDir(bytes, "BegleitschreibenFeutz.pdf");
		// openPDF(file);

	}

	private void openPDF(File file) {

		try {
			Desktop.getDesktop().open(file);
		} catch (IOException ex) {
			// no application registered for PDFs
		}
	}
}

package ch.dvbern.ebegu.tests;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.PrintFinanzielleSituationPDFService;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.ebegu.tets.TestDataUtil;

/**
 * Test der die vom JA gemeldeten Testfaelle ueberprueft.
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class PrintFinanzielleSituationPDFServiceBeanTest extends AbstractEbeguTest {

	protected BetreuungsgutscheinEvaluator evaluator;

	@Inject
	private PrintFinanzielleSituationPDFService printFinanzielleSituationPDFService;

	@Inject
	private GesuchService gesuchService;

	@Deployment
	public static Archive<?> createDeploymentEnvironment() {

		return createTestArchive();
	}

	@Before
	public void setUpCalcuator() {

		Locale.setDefault(new Locale("de", "CH"));
		evaluator = AbstractBGRechnerTest.createEvaluator();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testFinanzielleSituation_EinGesuchsteller() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		Gesuch gesuch = testfall.createGesuch();

		TestDataUtil.setFinanzielleSituation(gesuch, new BigDecimal("100000"));
		TestDataUtil.setEinkommensverschlechterung(gesuch, new BigDecimal("80000"), true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, new BigDecimal("50000"), false);
		TestDataUtil.calculateFinanzDaten(gesuch);

		byte[] bytes = printFinanzielleSituationPDFService.printFinanzielleSituation(gesuch);
		Assert.assertNotNull(bytes);
		writeToTempDir(bytes, "finanzielleSituation.pdf");
		// openPDF(file);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testFinanzielleSituation_ZweiGesuchsteller() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		Gesuch gesuch = testfall.createGesuch();
		// Hack damit Dokument mit zwei Gesuchsteller dargestellt wird
		gesuch.setGesuchsteller2(gesuch.getGesuchsteller1());

		TestDataUtil.setFinanzielleSituation(gesuch, new BigDecimal("100000"));
		TestDataUtil.setEinkommensverschlechterung(gesuch, new BigDecimal("80000"), true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, new BigDecimal("50000"), false);
		TestDataUtil.calculateFinanzDaten(gesuch);

		byte[] bytes = printFinanzielleSituationPDFService.printFinanzielleSituation(gesuch);
		Assert.assertNotNull(bytes);
		File file = writeToTempDir(bytes, "finanzielleSituation.pdf");
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

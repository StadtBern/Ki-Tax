package ch.dvbern.ebegu.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.PrintVerfuegungPDFService;
import ch.dvbern.ebegu.testfaelle.AbstractTestfall;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.ebegu.tets.TestDataUtil;

/**
 * Test der die vom JA gemeldeten Testfaelle ueberprueft.
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class PrintVerfuegungPDFServiceBeanTest extends AbstractEbeguTest {

	protected BetreuungsgutscheinEvaluator evaluator;

	@Inject
	private PrintVerfuegungPDFService verfuegungsGenerierungPDFService;

	@Inject
	private GesuchService gesuchService;

	@Before
	public void setUpCalcuator() {

		Locale.setDefault(new Locale("de", "CH"));
		evaluator = AbstractBGRechnerTest.createEvaluator();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGeneriereVerfuegungKita() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdaten(AbstractTestfall.idInstitutionAaregg, BetreuungsangebotTyp.KITA));
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdaten(AbstractTestfall.idInstitutionBruennen, BetreuungsangebotTyp.KITA));
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		Gesuch gesuch = testfall.createGesuch(BetreuungsangebotTyp.KITA);
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, AbstractBGRechnerTest.getParameter());
		gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getVerfuegung()
				.setManuelleBemerkungen("Test Bemerkung1\\nTest Bemerkung2\\nTest Bemerkung3\"");

		List<byte[]> verfuegungsPDFs = verfuegungsGenerierungPDFService.printVerfuegungen(gesuch);
		int i = 0;
		for (byte[] verfDoc : verfuegungsPDFs) {
			Assert.assertNotNull(verfDoc);
			writeToTempDir(verfDoc, "TN_Verfuegung" + i + ".pdf");
			i++;
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGeneriereVerfuegungTageselternKleinkinder() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdaten(AbstractTestfall.idInstitutionAaregg, BetreuungsangebotTyp.TAGESELTERN_KLEINKIND));
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdaten(AbstractTestfall.idInstitutionBruennen, BetreuungsangebotTyp.TAGESELTERN_KLEINKIND));
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		Gesuch gesuch = testfall.createGesuch(BetreuungsangebotTyp.TAGESELTERN_KLEINKIND);
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, AbstractBGRechnerTest.getParameter());
		gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getVerfuegung()
				.setManuelleBemerkungen("Test Bemerkung1\\nTest Bemerkung2\\nTest Bemerkung3\"");

		List<byte[]> verfuegungsPDFs = verfuegungsGenerierungPDFService.printVerfuegungen(gesuch);
		int i = 0;
		for (byte[] verfDoc : verfuegungsPDFs) {
			Assert.assertNotNull(verfDoc);
			writeToTempDir(verfDoc, "TN_Verfuegung" + i + ".pdf");
			i++;
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGeneriereVerfuegung_TageselternSchulkinder() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdaten(AbstractTestfall.idInstitutionAaregg, BetreuungsangebotTyp.TAGESELTERN_SCHULKIND));
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdaten(AbstractTestfall.idInstitutionBruennen, BetreuungsangebotTyp.TAGESELTERN_SCHULKIND));
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		Gesuch gesuch = testfall.createGesuch(BetreuungsangebotTyp.TAGESELTERN_SCHULKIND);
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, AbstractBGRechnerTest.getParameter());
		gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getVerfuegung().setManuelleBemerkungen("Test Bemerkung1\nTest Bemerkung2\nTest Bemerkung3");

		List<byte[]> verfuegungsPDFs = verfuegungsGenerierungPDFService.printVerfuegungen(gesuch);
		int i = 0;
		for (byte[] verfDoc : verfuegungsPDFs) {
			Assert.assertNotNull(verfDoc);
			writeToTempDir(verfDoc, "TN_Verfuegung" + i + ".pdf");
			i++;
		}

	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGeneriereVerfuegung_TagesstatetteSchulkinder() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdaten(AbstractTestfall.idInstitutionAaregg, BetreuungsangebotTyp.TAGI));
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdaten(AbstractTestfall.idInstitutionBruennen, BetreuungsangebotTyp.TAGI));
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		Gesuch gesuch = testfall.createGesuch(BetreuungsangebotTyp.TAGI);
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());
		evaluator.evaluate(gesuch, AbstractBGRechnerTest.getParameter());
		gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next().getVerfuegung()
				.setManuelleBemerkungen("\"Test Bemerkung1\\nTest Bemerkung2\\nTest Bemerkung3\"");

		List<byte[]> verfuegungsPDFs = verfuegungsGenerierungPDFService.printVerfuegungen(gesuch);
		int i = 0;
		for (byte[] verfDoc : verfuegungsPDFs) {
			Assert.assertNotNull(verfDoc);
			writeToTempDir(verfDoc, "TN_Verfuegung" + i + ".pdf");
			i++;
		}

	}
}

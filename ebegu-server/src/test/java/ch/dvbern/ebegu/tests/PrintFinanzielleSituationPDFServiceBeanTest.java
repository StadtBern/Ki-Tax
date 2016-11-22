package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.PrintFinanzielleSituationPDFService;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.ebegu.testfaelle.Testfall02_FeutzYvonne;
import ch.dvbern.ebegu.tets.TestDataUtil;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Test der die vom JA gemeldeten Testfaelle ueberprueft.
 */
@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class PrintFinanzielleSituationPDFServiceBeanTest extends AbstractEbeguLoginTest {

	protected BetreuungsgutscheinEvaluator evaluator;

	@Inject
	private PrintFinanzielleSituationPDFService printFinanzielleSituationPDFService;

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
	public void testFinanzielleSituation_EinGesuchsteller() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		testfall.createFall(null);
		testfall.createGesuch(LocalDate.of(1980, Month.MARCH, 25));
		Gesuch gesuch = testfall.fillInGesuch();

		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), new BigDecimal("80000"), true);
		TestDataUtil.calculateFinanzDaten(gesuch);

		final Verfuegung evaluateFamiliensituation = evaluator.evaluateFamiliensituation(gesuch);

		byte[] bytes = printFinanzielleSituationPDFService.printFinanzielleSituation(gesuch, evaluateFamiliensituation);
		Assert.assertNotNull(bytes);
		writeToTempDir(bytes, "finanzielleSituation1G.pdf");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testFinanzielleSituation_ZweiGesuchsteller() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenTagiAaregg());
		Testfall02_FeutzYvonne testfall = new Testfall02_FeutzYvonne(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		testfall.createFall(null);
		testfall.createGesuch(LocalDate.of(1980, Month.MARCH, 25));
		Gesuch gesuch = testfall.fillInGesuch();
		// Hack damit Dokument mit zwei Gesuchsteller dargestellt wird

		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), new BigDecimal("80000"), true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller2(), new BigDecimal("40000"), true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), new BigDecimal("50000"), false);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller2(), new BigDecimal("30000"), false);
		TestDataUtil.calculateFinanzDaten(gesuch);

		evaluator.evaluate(gesuch, AbstractBGRechnerTest.getParameter());

		byte[] bytes = printFinanzielleSituationPDFService.printFinanzielleSituation(gesuch, null);
		Assert.assertNotNull(bytes);
		writeToTempDir(bytes, "finanzielleSituation1G2G.pdf");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPrintFamilienSituation1() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaAaregg());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		testfall.createFall(null);
		testfall.createGesuch(LocalDate.of(1980, Month.MARCH, 25));
		Gesuch gesuch = testfall.fillInGesuch();

		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), new BigDecimal("80000"), true);
		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), new BigDecimal("50000"), false);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());

		TestDataUtil.calculateFinanzDaten(gesuch);
		evaluator.evaluate(gesuch, AbstractBGRechnerTest.getParameter());

		byte[] bytes = printFinanzielleSituationPDFService.printFinanzielleSituation(gesuch, null);

		writeToTempDir(bytes, "TN_FamilienStituation1.pdf");
	}

}

package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.rules.anlageverzeichnis.DokumentenverzeichnisEvaluator;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.ebegu.testfaelle.Testfall02_FeutzYvonne;
import ch.dvbern.ebegu.tests.util.UnitTestTempFolder;
import ch.dvbern.ebegu.tets.TestDataUtil;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import de.akquinet.jbosscc.needle.annotation.InjectIntoMany;
import de.akquinet.jbosscc.needle.annotation.ObjectUnderTest;
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 16/11/2016.
 */
@SuppressWarnings("unused")
public class PDFServiceBeanTest {

	@Rule
	public UnitTestTempFolder unitTestTempfolder = new UnitTestTempFolder();

	@Rule
	public NeedleRule needleRule = new NeedleRule();

	@ObjectUnderTest
	private PDFServiceBean pdfService;

	@InjectIntoMany
	EbeguVorlageService vorlageService = new EbeguVorlageServiceMock();

	@InjectIntoMany
	DokumentGrundService dokumentGrundService = new DokumentGrundServiceMock();

	@InjectIntoMany
	DokumentenverzeichnisEvaluator dokumentenverzeichnisEvaluator = new DokumentenverzeichnisEvaluator();

	private Gesuch gesuch;

	protected BetreuungsgutscheinEvaluator evaluator;

	@Before
	public void setupTestData() {

		Locale.setDefault(new Locale("de", "CH"));
		evaluator = AbstractBGRechnerTest.createEvaluator();

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenTagiWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());

		Testfall02_FeutzYvonne testfall = new Testfall02_FeutzYvonne(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		testfall.createFall(TestDataUtil.createDefaultBenutzer());
		testfall.createGesuch(LocalDate.of(2016, Month.DECEMBER, 12));

		gesuch = testfall.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch);
		gesuch.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());

		gesuch.addDokumentGrund(new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE, DokumentTyp.STEUERERKLAERUNG));
		gesuch.addDokumentGrund(new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE, DokumentTyp.NACHWEIS_AUSBILDUNG));
		gesuch.addDokumentGrund(new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE, DokumentTyp.NACHWEIS_FAMILIENZULAGEN));
	}

	@Test
	public void	testGenerateFreigabequittungJugendamt() throws Exception {

		byte[] bytes = pdfService.generateFreigabequittung(gesuch, Zustelladresse.JUGENDAMT);
		assertNotNull(bytes);
		unitTestTempfolder.writeToTempDir(bytes, "Freigabequittung_Jugendamt(" + gesuch.getAntragNummer() + ").pdf");

	}

	@Test
	public void	testGenerateFreigabequittungSchulamt() throws Exception {

		byte[] bytes = pdfService.generateFreigabequittung(gesuch, Zustelladresse.SCHULAMT);
		assertNotNull(bytes);
		unitTestTempfolder.writeToTempDir(bytes, "Freigabequittung_Schulamt(" + gesuch.getAntragNummer() + ").pdf");

	}

	@Test
	public void testPrintNichteintreten() throws Exception {

		Optional<Betreuung> betreuung = gesuch.extractAllBetreuungen().stream()
			.filter(b -> b.getBetreuungsangebotTyp() == BetreuungsangebotTyp.KITA)
			.findFirst();

		if (betreuung.isPresent()) {
			byte[] bytes = pdfService.generateNichteintreten(betreuung.get());
			Assert.assertNotNull(bytes);
			unitTestTempfolder.writeToTempDir(bytes, "Nichteintreten(" + betreuung.get().getBGNummer() + ").pdf");
		} else {
			throw new Exception(String.format("%s", "testPrintNichteintreten()"));
		}

	}

	@Test
	public void testPrintInfoschreiben() throws Exception {

		Optional<Betreuung> betreuung = gesuch.extractAllBetreuungen().stream()
			.filter(b -> b.getBetreuungsangebotTyp() == BetreuungsangebotTyp.TAGI)
			.findFirst();

		if (betreuung.isPresent()) {
			byte[] bytes = pdfService.generateNichteintreten(betreuung.get());
			Assert.assertNotNull(bytes);
			unitTestTempfolder.writeToTempDir(bytes, "Infoschreiben(" + betreuung.get().getBGNummer() + ").pdf");
		} else {
			throw new Exception(String.format("%s", "testPrintInfoschreiben()"));
		}

	}

	@Test
	public void testPrintErsteMahnungSinglePage() throws Exception {

		Mahnung mahnung = TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch, LocalDate.now().plusWeeks(2), 3);

		byte[] bytes = pdfService.generateMahnung(mahnung, null);

		assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "1_Mahnung_Single_Page.pdf");

		PdfReader pdfRreader = new PdfReader(bytes);
		pdfRreader.getNumberOfPages();
		assertEquals("PDF should be one page long.",1, pdfRreader.getNumberOfPages());
		pdfRreader.close();
	}

	@Test
	public void testPrintErsteMahnungTwoPages() throws Exception {

		Mahnung mahnung = TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch, LocalDate.now().plusWeeks(2), 10);

		byte[] bytes = pdfService.generateMahnung(mahnung, null);

		assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "1_Mahnung_Two_Pages.pdf");

		PdfReader pdfRreader = new PdfReader(bytes);
		pdfRreader.getNumberOfPages();
		assertEquals("PDF should be two pages long.",2, pdfRreader.getNumberOfPages());

		PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(pdfRreader);
		assertTrue("Second page should begin with this text.",
			pdfTextExtractor.getTextFromPage(2).startsWith("Erst nach Eingang dieser"));

		pdfRreader.close();
	}

	@Test
	public void testPrintZweiteMahnungSinglePage() throws Exception {

		Mahnung ersteMahnung = TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch, LocalDate.now().plusWeeks(2), 3);
		Mahnung zweiteMahnung = TestDataUtil.createMahnung(MahnungTyp.ZWEITE_MAHNUNG, gesuch, LocalDate.now().plusWeeks(2), 3);
		zweiteMahnung.setVorgaengerId(ersteMahnung.getId());

		byte[] bytes = pdfService.generateMahnung(zweiteMahnung, Optional.of(ersteMahnung));
		assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "2_Mahnung_Single_Page.pdf");

		PdfReader pdfRreader = new PdfReader(bytes);
		pdfRreader.getNumberOfPages();
		assertEquals(1, pdfRreader.getNumberOfPages());

		pdfRreader.close();
	}

	@Test
	public void testPrintZweiteMahnungTwoPages() throws Exception {

		Mahnung ersteMahnung = TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch, LocalDate.now().plusWeeks(2), 10);
		Mahnung zweiteMahnung = TestDataUtil.createMahnung(MahnungTyp.ZWEITE_MAHNUNG, gesuch, LocalDate.now().plusWeeks(2), 10);
		zweiteMahnung.setVorgaengerId(ersteMahnung.getId());

		byte[] bytes = pdfService.generateMahnung(zweiteMahnung, Optional.of(ersteMahnung));
		assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "2_Mahnung_Two_Pages.pdf");

		PdfReader pdfRreader = new PdfReader(bytes);
		pdfRreader.getNumberOfPages();
		assertEquals("PDF should be two pages long.",2, pdfRreader.getNumberOfPages());

		PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(pdfRreader);
		assertTrue("Second page should begin with this text.",
			pdfTextExtractor.getTextFromPage(2).startsWith("Wenn Sie die geforderten Angaben"));

		pdfRreader.close();
	}

	@Test
	public void testFinanzielleSituation_EinGesuchsteller() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());
		Testfall01_WaeltiDagmar testfall = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		testfall.createFall(null);
		testfall.createGesuch(LocalDate.of(1980, Month.MARCH, 25));
		Gesuch gesuch = testfall.fillInGesuch();

		TestDataUtil.setEinkommensverschlechterung(gesuch, gesuch.getGesuchsteller1(), new BigDecimal("80000"), true);
		TestDataUtil.calculateFinanzDaten(gesuch);

		final Verfuegung evaluateFamiliensituation = evaluator.evaluateFamiliensituation(gesuch);

		byte[] bytes = pdfService.generateFinanzielleSituation(gesuch, evaluateFamiliensituation);
		Assert.assertNotNull(bytes);
		unitTestTempfolder.writeToTempDir(bytes, "finanzielleSituation1G.pdf");
	}

	@Test
	public void testFinanzielleSituation_ZweiGesuchsteller() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenTagiWeissenstein());
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

		byte[] bytes = pdfService.generateFinanzielleSituation(gesuch, null);
		Assert.assertNotNull(bytes);
		unitTestTempfolder.writeToTempDir(bytes, "finanzielleSituation1G2G.pdf");
	}

	@Test
	public void testPrintFamilienSituation1() throws Exception {

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
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

		byte[] bytes = pdfService.generateFinanzielleSituation(gesuch, null);

		unitTestTempfolder.writeToTempDir(bytes, "TN_FamilienStituation1.pdf");
	}

	@Test
	public void testGeneriereVerfuegungKita() throws Exception {

		gesuch.extractAllBetreuungen().get(0).getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.KITA);

		evaluator.evaluate(gesuch, AbstractBGRechnerTest.getParameter());

		Betreuung testBetreuung = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();
		testBetreuung.getVerfuegung().setManuelleBemerkungen("Test Bemerkung1\nTest Bemerkung2\nTest Bemerkung3");

		byte[] verfuegungsPDF = pdfService.generateVerfuegungForBetreuung(testBetreuung, null);
		Assert.assertNotNull(verfuegungsPDF);
		unitTestTempfolder.writeToTempDir(verfuegungsPDF, "Verfuegung_KITA.pdf");
	}

	@Test
	public void testGeneriereVerfuegungTageselternKleinkinder() throws Exception {

		gesuch.extractAllBetreuungen().get(0).getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGESELTERN_KLEINKIND);

		evaluator.evaluate(gesuch, AbstractBGRechnerTest.getParameter());

		Betreuung testBetreuung = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();
		testBetreuung.getVerfuegung().setManuelleBemerkungen("Test Bemerkung1\nTest Bemerkung2\nTest Bemerkung3");

		byte[] verfuegungsPDF = pdfService.generateVerfuegungForBetreuung(testBetreuung, null);
		Assert.assertNotNull(verfuegungsPDF);
		unitTestTempfolder.writeToTempDir(verfuegungsPDF, "Verfuegung_TageselternKleinkinder.pdf");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGeneriereVerfuegung_TageselternSchulkinder() throws Exception {

		gesuch.extractAllBetreuungen().get(0).getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGESELTERN_SCHULKIND);

		evaluator.evaluate(gesuch, AbstractBGRechnerTest.getParameter());

		Betreuung testBetreuung = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();
		testBetreuung.getVerfuegung().setManuelleBemerkungen("Test Bemerkung1\nTest Bemerkung2\nTest Bemerkung3");

		byte[] verfuegungsPDF = pdfService.generateVerfuegungForBetreuung(testBetreuung, null);
		Assert.assertNotNull(verfuegungsPDF);
		unitTestTempfolder.writeToTempDir(verfuegungsPDF, "Verfuegung_TageselternSchulkinder.pdf");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGeneriereVerfuegung_TagesstatetteSchulkinder() throws Exception {

		gesuch.extractAllBetreuungen().get(0).getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGI);

		evaluator.evaluate(gesuch, AbstractBGRechnerTest.getParameter());

		Betreuung testBetreuung = gesuch.getKindContainers().iterator().next().getBetreuungen().iterator().next();
		testBetreuung.getVerfuegung().setManuelleBemerkungen("Test Bemerkung1\nTest Bemerkung2\nTest Bemerkung3");

		byte[] verfuegungsPDF = pdfService.generateVerfuegungForBetreuung(testBetreuung, null);
		Assert.assertNotNull(verfuegungsPDF);
		unitTestTempfolder.writeToTempDir(verfuegungsPDF, "Verfuegung_TagesstatetteSchulkinder.pdf");
	}

}

package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.rules.anlageverzeichnis.DokumentenverzeichnisEvaluator;
import ch.dvbern.ebegu.services.DokumentGrundService;
import ch.dvbern.ebegu.services.EbeguVorlageService;
import ch.dvbern.ebegu.services.PDFServiceBean;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.Assert.*;

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

	private Gesuch gesuch_1GS, gesuch_2GS;

	private boolean writeProtectPDF = false;

	protected BetreuungsgutscheinEvaluator evaluator;

	@Before
	public void setupTestData() {

		Locale.setDefault(new Locale("de", "CH"));
		evaluator = AbstractBGRechnerTest.createEvaluator();

		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenTagiWeissenstein());
		institutionStammdatenList.add(TestDataUtil.createInstitutionStammdatenKitaBruennen());

		//setup gesuch with one Gesuchsteller
		Testfall01_WaeltiDagmar testfall_1GS = new Testfall01_WaeltiDagmar(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		testfall_1GS.createFall(TestDataUtil.createDefaultBenutzer());
		testfall_1GS.createGesuch(LocalDate.of(2016, Month.DECEMBER, 12));

		gesuch_1GS = testfall_1GS.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch_1GS);
		gesuch_1GS.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());

		gesuch_1GS.addDokumentGrund(new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE, DokumentTyp.STEUERERKLAERUNG));
		gesuch_1GS.addDokumentGrund(new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE, DokumentTyp.NACHWEIS_AUSBILDUNG));
		gesuch_1GS.addDokumentGrund(new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE, DokumentTyp.NACHWEIS_FAMILIENZULAGEN));

		//setup gesuch with two Gesuchstellers
		Testfall02_FeutzYvonne testfall_2GS = new Testfall02_FeutzYvonne(TestDataUtil.createGesuchsperiode1617(), institutionStammdatenList);
		testfall_2GS.createFall(TestDataUtil.createDefaultBenutzer());
		testfall_2GS.createGesuch(LocalDate.of(2016, Month.DECEMBER, 12));

		gesuch_2GS = testfall_2GS.fillInGesuch();
		TestDataUtil.calculateFinanzDaten(gesuch_2GS);
		gesuch_2GS.setGesuchsperiode(TestDataUtil.createGesuchsperiode1617());

		gesuch_2GS.addDokumentGrund(new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE, DokumentTyp.STEUERERKLAERUNG));
		gesuch_2GS.addDokumentGrund(new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE, DokumentTyp.NACHWEIS_AUSBILDUNG));
		gesuch_2GS.addDokumentGrund(new DokumentGrund(DokumentGrundTyp.SONSTIGE_NACHWEISE, DokumentTyp.NACHWEIS_FAMILIENZULAGEN));
	}

	@Test
	public void testGenerateFreigabequittungJugendamt() throws Exception {

		byte[] bytes = pdfService.generateFreigabequittung(gesuch_2GS, Zustelladresse.JUGENDAMT, writeProtectPDF);
		assertNotNull(bytes);
		unitTestTempfolder.writeToTempDir(bytes, "Freigabequittung_Jugendamt(" + gesuch_2GS.getAntragNummer() + ").pdf");

	}

	@Test
	public void testGenerateFreigabequittungSchulamt() throws Exception {

		byte[] bytes = pdfService.generateFreigabequittung(gesuch_2GS, Zustelladresse.SCHULAMT, writeProtectPDF);
		assertNotNull(bytes);
		unitTestTempfolder.writeToTempDir(bytes, "Freigabequittung_Schulamt(" + gesuch_2GS.getAntragNummer() + ").pdf");

	}

	@Test
	public void testPrintNichteintreten() throws Exception {

		Optional<Betreuung> betreuung = gesuch_2GS.extractAllBetreuungen().stream()
			.filter(b -> b.getBetreuungsangebotTyp() == BetreuungsangebotTyp.KITA)
			.findFirst();

		if (betreuung.isPresent()) {
			byte[] bytes = pdfService.generateNichteintreten(betreuung.get(), writeProtectPDF);
			Assert.assertNotNull(bytes);
			unitTestTempfolder.writeToTempDir(bytes, "Nichteintreten(" + betreuung.get().getBGNummer() + ").pdf");
		} else {
			throw new Exception(String.format("%s", "testPrintNichteintreten()"));
		}

	}

	@Test
	public void testPrintInfoschreiben() throws Exception {

		Optional<Betreuung> betreuung = gesuch_2GS.extractAllBetreuungen().stream()
			.filter(b -> b.getBetreuungsangebotTyp() == BetreuungsangebotTyp.TAGI)
			.findFirst();

		if (betreuung.isPresent()) {
			byte[] bytes = pdfService.generateNichteintreten(betreuung.get(), writeProtectPDF);
			Assert.assertNotNull(bytes);
			unitTestTempfolder.writeToTempDir(bytes, "Infoschreiben(" + betreuung.get().getBGNummer() + ").pdf");
		} else {
			throw new Exception(String.format("%s", "testPrintInfoschreiben()"));
		}

	}

	@Test
	public void testPrintErsteMahnungSinglePage() throws Exception {

		Mahnung mahnung = TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch_2GS, LocalDate.now().plusWeeks(2), 3);

		byte[] bytes = pdfService.generateMahnung(mahnung, null, writeProtectPDF);

		assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "1_Mahnung_Single_Page.pdf");

		PdfReader pdfRreader = new PdfReader(bytes);
		pdfRreader.getNumberOfPages();
		assertEquals("PDF should be one page long.", 1, pdfRreader.getNumberOfPages());
		pdfRreader.close();
	}

	@Test
	public void testPrintErsteMahnungTwoPages() throws Exception {

		Mahnung mahnung = TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch_2GS, LocalDate.now().plusWeeks(2), 10);

		byte[] bytes = pdfService.generateMahnung(mahnung, null, writeProtectPDF);

		assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "1_Mahnung_Two_Pages.pdf");

		PdfReader pdfRreader = new PdfReader(bytes);
		pdfRreader.getNumberOfPages();
		assertEquals("PDF should be two pages long.", 2, pdfRreader.getNumberOfPages());

		PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(pdfRreader);
		assertTrue("Second page should begin with this text.",
			pdfTextExtractor.getTextFromPage(2).startsWith("Erst nach Eingang dieser"));

		pdfRreader.close();
	}

	@Test
	public void testPrintZweiteMahnungSinglePage() throws Exception {

		Mahnung ersteMahnung = TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch_2GS, LocalDate.now().plusWeeks(2), 3);
		Mahnung zweiteMahnung = TestDataUtil.createMahnung(MahnungTyp.ZWEITE_MAHNUNG, gesuch_2GS, LocalDate.now().plusWeeks(2), 3);
		zweiteMahnung.setVorgaengerId(ersteMahnung.getId());

		byte[] bytes = pdfService.generateMahnung(zweiteMahnung, Optional.of(ersteMahnung), writeProtectPDF);
		assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "2_Mahnung_Single_Page.pdf");

		PdfReader pdfRreader = new PdfReader(bytes);
		pdfRreader.getNumberOfPages();
		assertEquals(1, pdfRreader.getNumberOfPages());

		pdfRreader.close();
	}

	@Test
	public void testPrintZweiteMahnungTwoPages() throws Exception {

		Mahnung ersteMahnung = TestDataUtil.createMahnung(MahnungTyp.ERSTE_MAHNUNG, gesuch_2GS, LocalDate.now().plusWeeks(2), 10);
		Mahnung zweiteMahnung = TestDataUtil.createMahnung(MahnungTyp.ZWEITE_MAHNUNG, gesuch_2GS, LocalDate.now().plusWeeks(2), 10);
		zweiteMahnung.setVorgaengerId(ersteMahnung.getId());

		byte[] bytes = pdfService.generateMahnung(zweiteMahnung, Optional.of(ersteMahnung), writeProtectPDF);
		assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "2_Mahnung_Two_Pages.pdf");

		PdfReader pdfRreader = new PdfReader(bytes);
		pdfRreader.getNumberOfPages();
		assertEquals("PDF should be two pages long.", 2, pdfRreader.getNumberOfPages());

		PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(pdfRreader);
		assertTrue("Second page should begin with this text.",
			pdfTextExtractor.getTextFromPage(2).startsWith("Wenn Sie die geforderten Unterlagen"));

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

		byte[] bytes = pdfService.generateFinanzielleSituation(gesuch, evaluateFamiliensituation, writeProtectPDF);
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

		byte[] bytes = pdfService.generateFinanzielleSituation(gesuch, null, writeProtectPDF);
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

		byte[] bytes = pdfService.generateFinanzielleSituation(gesuch, null, writeProtectPDF);

		unitTestTempfolder.writeToTempDir(bytes, "TN_FamilienStituation1.pdf");
	}

	@Test
	public void testPrintBegleitschreiben() throws Exception {

		evaluator.evaluate(gesuch_1GS, AbstractBGRechnerTest.getParameter());
		byte[] bytes = pdfService.generateBegleitschreiben(gesuch_1GS, writeProtectPDF);
		Assert.assertNotNull(bytes);
		unitTestTempfolder.writeToTempDir(bytes, "BegleitschreibenWaelti.pdf");
	}

	@Test
	public void testPrintBegleitschreibenTwoGesuchsteller() throws Exception {

		gesuch_2GS.getGesuchsteller1().getAdressen().stream().forEach(gesuchstellerAdresse -> gesuchstellerAdresse.getGesuchstellerAdresseJA().setZusatzzeile("Test zusatztzeile"));
		evaluator.evaluate(gesuch_2GS, AbstractBGRechnerTest.getParameter());
		byte[] bytes = pdfService.generateBegleitschreiben(gesuch_2GS, writeProtectPDF);
		Assert.assertNotNull(bytes);
		unitTestTempfolder.writeToTempDir(bytes, "BegleitschreibenFeutz.pdf");
	}

	@Test
	public void testGeneriereVerfuegungKita() throws Exception {

		gesuch_2GS.extractAllBetreuungen().get(0).getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.KITA);

		evaluator.evaluate(gesuch_2GS, AbstractBGRechnerTest.getParameter());

		Betreuung testBetreuung = gesuch_2GS.getKindContainers().iterator().next().getBetreuungen().iterator().next();
		testBetreuung.getVerfuegung().setManuelleBemerkungen("Test Bemerkung 1\nTest Bemerkung 2\nTest Bemerkung 3");

		byte[] verfuegungsPDF = pdfService.generateVerfuegungForBetreuung(testBetreuung, LocalDate.now().minusDays(183), writeProtectPDF);
		Assert.assertNotNull(verfuegungsPDF);
		unitTestTempfolder.writeToTempDir(verfuegungsPDF, "Verfuegung_KITA.pdf");
	}

	@Test
	public void testGeneriereVerfuegungTageselternKleinkinder() throws Exception {

		gesuch_2GS.extractAllBetreuungen().get(0).getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGESELTERN_KLEINKIND);

		evaluator.evaluate(gesuch_2GS, AbstractBGRechnerTest.getParameter());

		Betreuung testBetreuung = gesuch_2GS.getKindContainers().iterator().next().getBetreuungen().iterator().next();
		testBetreuung.getVerfuegung().setManuelleBemerkungen("Test Bemerkung 1\nTest Bemerkung 2\nTest Bemerkung 3");

		byte[] verfuegungsPDF = pdfService.generateVerfuegungForBetreuung(testBetreuung, LocalDate.now().minusDays(183), writeProtectPDF);
		Assert.assertNotNull(verfuegungsPDF);
		unitTestTempfolder.writeToTempDir(verfuegungsPDF, "Verfuegung_TageselternKleinkinder.pdf");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGeneriereVerfuegung_TageselternSchulkinder() throws Exception {

		gesuch_2GS.extractAllBetreuungen().get(0).getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGESELTERN_SCHULKIND);

		evaluator.evaluate(gesuch_2GS, AbstractBGRechnerTest.getParameter());

		Betreuung testBetreuung = gesuch_2GS.getKindContainers().iterator().next().getBetreuungen().iterator().next();
		testBetreuung.getVerfuegung().setManuelleBemerkungen("Test Bemerkung 1\nTest Bemerkung 2\nTest Bemerkung 3");

		byte[] verfuegungsPDF = pdfService.generateVerfuegungForBetreuung(testBetreuung, LocalDate.now().minusDays(183), writeProtectPDF);
		Assert.assertNotNull(verfuegungsPDF);
		unitTestTempfolder.writeToTempDir(verfuegungsPDF, "Verfuegung_TageselternSchulkinder.pdf");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGeneriereVerfuegung_TagesstatetteSchulkinder() throws Exception {

		gesuch_2GS.extractAllBetreuungen().get(0).getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGI);

		evaluator.evaluate(gesuch_2GS, AbstractBGRechnerTest.getParameter());

		Betreuung testBetreuung = gesuch_2GS.getKindContainers().iterator().next().getBetreuungen().iterator().next();
		testBetreuung.getVerfuegung().setManuelleBemerkungen("Test Bemerkung 1\nTest Bemerkung 2\nTest Bemerkung 3");

		byte[] verfuegungsPDF = pdfService.generateVerfuegungForBetreuung(testBetreuung, LocalDate.now().minusDays(183), writeProtectPDF);
		Assert.assertNotNull(verfuegungsPDF);
		unitTestTempfolder.writeToTempDir(verfuegungsPDF, "Verfuegung_TagesstatetteSchulkinder.pdf");
	}

}

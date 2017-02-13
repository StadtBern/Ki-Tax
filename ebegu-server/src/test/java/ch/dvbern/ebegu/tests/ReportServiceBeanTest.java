package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GesuchStichtagDataRow;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GesuchZeitraumDataRow;
import ch.dvbern.ebegu.reporting.lib.DateUtil;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.ReportService;
import ch.dvbern.ebegu.services.WizardStepService;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.ebegu.tests.util.UnitTestTempFolder;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class ReportServiceBeanTest extends AbstractEbeguLoginTest {

	@Rule
	public UnitTestTempFolder unitTestTempfolder = new UnitTestTempFolder();

//	@Rule
//	public NeedleRule needleRule = new NeedleRule().withOuter(new DatabaseRule());

	@Inject
	private ReportService reportService;

	@Inject
	private Persistence<Gesuch> persistence;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private GesuchService gesuchService;
	@Inject
	private WizardStepService wizardStepService;


	private Gesuch gesuch_1GS;

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


	}

	@Test
	public void testGetReportDataGesuchStichtag() throws Exception {


		Gesuch gesuch = TestDataUtil.createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		gesuch.setStatus(AntragStatus.IN_BEARBEITUNG_GS);
		gesuch.setEingangsart(Eingangsart.PAPIER);
		wizardStepService.createWizardStepList(gesuch);
		gesuch.getKindContainers().stream()
			.flatMap(kindContainer -> kindContainer.getBetreuungen().stream())
			.forEach((betreuung)
				-> {
				betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGESSCHULE);
				persistence.merge(betreuung.getInstitutionStammdaten());
			});
		gesuch = persistence.merge(gesuch);
		gesuchService.updateGesuch(gesuch,true);

		//review dieses querie faengt beim kind an, wenn kein keind da ist wird nix gefunden
		List<GesuchStichtagDataRow> reportData = reportService.getReportDataGesuchStichtag(
			LocalDateTime.now().plusDays(1),
			gesuch.getGesuchsperiode().getId());

		assertNotNull(reportData);
		assertEquals(2, reportData.size());

	}

	@Test
	@Ignore
	public void testGetReportDataGesuchZeitraumTest() throws Exception {

		//todo sinnvolle testdaten finden
		Gesuch gesuch = TestDataUtil.createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));
		gesuch.setStatus(AntragStatus.IN_BEARBEITUNG_GS);
		gesuch.setEingangsart(Eingangsart.PAPIER);
		wizardStepService.createWizardStepList(gesuch);
		gesuch.getKindContainers().stream()
			.flatMap(kindContainer -> kindContainer.getBetreuungen().stream())
			.forEach((betreuung)
				-> {
				betreuung.getInstitutionStammdaten().setBetreuungsangebotTyp(BetreuungsangebotTyp.TAGESSCHULE);
				persistence.merge(betreuung.getInstitutionStammdaten());
			});

		gesuchService.updateGesuch(gesuch,true);
//		Gesuch gesuch = TestDataUtil.createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.of(1980, Month.MARCH, 25));

		List<GesuchZeitraumDataRow> reportData = reportService.getReportDataGesuchZeitraum(
			LocalDateTime.parse("2015-12-12 00:00:00", DateUtil.SQL_DATETIME_FORMAT),
			LocalDateTime.parse("2099-12-12 00:00:00", DateUtil.SQL_DATETIME_FORMAT),
			gesuch.getGesuchsperiode().getId());

		assertNotNull(reportData);
		assertEquals(1, reportData.size());

	}

	@Test
	public void generateExcelReportGesuchStichtag() throws Exception {

		byte[] bytes = reportService.generateExcelReportGesuchStichtag(
			LocalDateTime.parse("2017-01-25 00:00:00", DateUtil.SQL_DATETIME_FORMAT),
			"0621fb5d-a187-5a91-abaf-8a813c4d263a");

		assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "ExcelReportGesuchStichtag.xlsx");

	}


	@Test
	public void getReportDataGesuchZeitraum() throws Exception {

		List<GesuchZeitraumDataRow> reportData = reportService.getReportDataGesuchZeitraum(
			LocalDateTime.parse("2017-01-25 00:00:00", DateUtil.SQL_DATETIME_FORMAT),
			LocalDateTime.parse("2017-01-26 23:59:59", DateUtil.SQL_DATETIME_FORMAT),
			"0621fb5d-a187-5a91-abaf-8a813c4d263a");

		assertNotNull(reportData);

	}

	@Test
	public void generateExcelReportGesuchZeitraum() throws Exception {

		byte[] bytes = reportService.generateExcelReportGesuchZeitraum(
			LocalDateTime.parse("2017-01-25 00:00:00", DateUtil.SQL_DATETIME_FORMAT),
			LocalDateTime.parse("2017-01-26 23:59:59", DateUtil.SQL_DATETIME_FORMAT),
			"0621fb5d-a187-5a91-abaf-8a813c4d263a");

		assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "ExcelReportGesuchZeitraum.xlsx");

	}


}

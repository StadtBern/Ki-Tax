package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GesuchStichtagDataRow;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GesuchZeitraumDataRow;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.ebegu.tests.util.UnitTestTempFolder;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@SuppressWarnings({"InstanceMethodNamingConvention", "MethodParameterNamingConvention", "InstanceVariableNamingConvention"})
@RunWith(Arquillian.class)
@UsingDataSet("datasets/reportTestData.xml")
@Transactional(TransactionMode.DISABLED)
public class ReportServiceBeanTest extends AbstractEbeguLoginTest {

	@Rule
	public UnitTestTempFolder unitTestTempfolder = new UnitTestTempFolder();

	@Inject
	private ReportService reportService;

	@Inject
	private Persistence<?> persistence;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private WizardStepService wizardStepService;

	@Inject
	private ZahlungService zahlungService;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Before
	public void init() {
		// Aufbau der Testdaten:
		// 7 Fälle, 10 Gesuche (d.h. 3 Gesuch mit Mutation)
		// Je einmal Mahnung, Beschwerde, Nicht-Freigegeben, Nicht-Eintreten, Abwesenheit
		Collection<Mandant> allMandant = criteriaQueryHelper.getAll(Mandant.class);
		Collection<Gesuchsperiode> allGesuchsperiode = criteriaQueryHelper.getAll(Gesuchsperiode.class);
		Collection<Fall> allFall = criteriaQueryHelper.getAll(Fall.class);
		Collection<Gesuch> allGesuch = criteriaQueryHelper.getAll(Gesuch.class);

		Assert.assertEquals("1 Mandant aus File, einer aus Superklasse", 2, allMandant.size());
		Assert.assertEquals(1, allGesuchsperiode.size());
		Assert.assertEquals(7, allFall.size());
		Assert.assertEquals(10, allGesuch.size());
	}

	@Test
	public void testGetReportDataGesuchStichtag() throws Exception {
		List<GesuchStichtagDataRow> reportData = reportService.getReportDataGesuchStichtag(LocalDateTime.now(), null);

		List<GesuchStichtagDataRow> rowsSorted = reportData
			.stream()
			.sorted(Comparator.comparing(GesuchStichtagDataRow::getBgNummer).thenComparing(GesuchStichtagDataRow::getGesuchLaufNr))
			.collect(Collectors.toList());

		assertNotNull(rowsSorted);
		// Wir haben je 1 Gesuch mit Mahnung, 1 Gesuch mit Beschwerde
		assertEquals(2, rowsSorted.size());
		// Fall 103: Mahnung
		assertGesuchStichtagDataRow(rowsSorted.get(0), "17.000103.1.1", 0, 1, 0);
		// Fall 104: Beschwerde
		assertGesuchStichtagDataRow(rowsSorted.get(1), "17.000104.1.1", 0, 0, 1);
	}

	@Test
	public void testGetReportDataGesuchZeitraumTest() throws Exception {
		List<GesuchZeitraumDataRow> reportData = reportService.getReportDataGesuchZeitraum(
			LocalDateTime.of(2016, Month.JANUARY, 1, 0, 0, 0),
			LocalDateTime.of(2017, Month.DECEMBER, 31, 0, 0, 0),
			null);

		List<GesuchZeitraumDataRow> rowsSorted = reportData
			.stream()
			.sorted(Comparator.comparing(GesuchZeitraumDataRow::getBgNummer).thenComparing(GesuchZeitraumDataRow::getGesuchLaufNr))
			.collect(Collectors.toList());

		assertNotNull(rowsSorted);
		assertEquals(15, rowsSorted.size());

		// 101 Verfuegt mit Nicht eintreten
		// 102 Normal Verfuegt
		// 103 Mahnung
		// 104: Verfuegt mit Beschwerde
		// 105: Neues Kind mit Betreuung
		// 106: Abwesenheit
		// 107: Kind Name

		// Fall 101: Verfuegt mit nichtEintreten
		// Betreuung 1: Verfuegt mit Nicht-Eintreten
		assertGesuchZeitraumDataRow(rowsSorted.get(0), "17.000101.1.1", 0, 0, 0, 0, 0, 1, 0, 1);
		// Betreuung 2: Verfuegt normal
		assertGesuchZeitraumDataRow(rowsSorted.get(1), "17.000101.1.2", 0, 0, 0,0, 0, 1, 0, 0);

		// Fall 102: Normal verfuegt, 2 Kinder
		assertGesuchZeitraumDataRow(rowsSorted.get(2), "17.000102.1.1", 0, 0, 0,0, 0, 1, 1, 0);
		assertGesuchZeitraumDataRow(rowsSorted.get(3), "17.000102.2.1", 0, 0, 0,0, 0, 1, 1, 0);

		// Fall 103: Mahnung
		assertGesuchZeitraumDataRow(rowsSorted.get(4), "17.000103.1.1", 0, 0, 0,1, 0, 0, 0, 0);

		// Fall 104: Verfuegt, mit Beschwerde
		assertGesuchZeitraumDataRow(rowsSorted.get(5), "17.000104.1.1", 0, 0, 0,0, 1, 1, 0, 0);

		// Fall 105:
		// Erstgesuch
		assertGesuchZeitraumDataRow(rowsSorted.get(6), "17.000105.1.1", 0, 0, 0,0, 0, 1, 1, 0);
		// Mutation
		// Kind 1: Schon im Erstgesuch vorhanden, in Mutation unverändert
		assertGesuchZeitraumDataRow(rowsSorted.get(7), "17.000105.1.1", 0, 0, 0,0, 0, 1, 1, 0);
		// Kind 2: Neu auf Mutation inkl. Betreuung
		assertGesuchZeitraumDataRow(rowsSorted.get(8), "17.000105.2.1", 0, 1, 1,0, 0, 1, 1, 0);

		// Fall 106: Mutation mit Abwesenheit
		// Erstgesuch
		assertGesuchZeitraumDataRow(rowsSorted.get(9), "17.000106.1.1", 0, 0, 0,0, 0, 1, 1, 0);
		// Mutation
		assertGesuchZeitraumDataRow(rowsSorted.get(10), "17.000106.1.1", 1, 0, 0,0, 0, 1, 1, 0);

		// Fall 107: Mutation Kind Name
		// Betreuung 1
		assertGesuchZeitraumDataRow(rowsSorted.get(11), "17.000107.1.1", 0, 0, 0,0, 0, 1, 0, 0);
		assertGesuchZeitraumDataRow(rowsSorted.get(12), "17.000107.1.1", 0, 0, 1,0, 0, 1, 0, 0);
		// Betreuung 2
		assertGesuchZeitraumDataRow(rowsSorted.get(13), "17.000107.1.2", 0, 0, 0,0, 0, 1, 0, 0);
		assertGesuchZeitraumDataRow(rowsSorted.get(14), "17.000107.1.2", 0, 0, 1,0, 0, 1, 0, 0);
	}

	private void assertGesuchStichtagDataRow(GesuchStichtagDataRow row, String bgNummer, Integer nichtFreigegeben, Integer mahnung, Integer beschwerde) {
		assertNotNull(row);
		assertEquals(bgNummer, row.getBgNummer());
		assertEquals(nichtFreigegeben, row.getNichtFreigegeben());
		assertEquals(mahnung, row.getMahnungen());
		assertEquals(beschwerde, row.getBeschwerde());
	}

	private void assertGesuchZeitraumDataRow(GesuchZeitraumDataRow row, String bgNummer,
											 Integer anzahlMutationAbwesenheit, Integer anzahlMutationBetreuung, Integer anzahlMutationKind,
											 Integer anzahlMahnungen, Integer anzahlBeschwerde, Integer anzahlVerfuegungen,
											 Integer anzahlVerfuegungenNormal, Integer anzahlVerfuegungenNichtEintreten) {
		assertNotNull(row);
		assertEquals(bgNummer, row.getBgNummer());
		assertEquals(anzahlMutationAbwesenheit, row.getAnzahlMutationAbwesenheit());
		assertEquals(anzahlMutationBetreuung, row.getAnzahlMutationBetreuung());
		assertEquals(anzahlMutationKind, row.getAnzahlMutationKinder());
		assertEquals(anzahlMahnungen, row.getAnzahlMahnungen());
		assertEquals(anzahlBeschwerde, row.getAnzahlBeschwerde());
		assertEquals(anzahlVerfuegungen, row.getAnzahlVerfuegungen());
		assertEquals(anzahlVerfuegungenNormal, row.getAnzahlVerfuegungenNormal());
		assertEquals(anzahlVerfuegungenNichtEintreten, row.getAnzahlVerfuegungenNichtEintreten());
	}

	@Test
	public void generateExcelReportGesuchStichtag() throws Exception {
		UploadFileInfo uploadFileInfo = reportService.generateExcelReportGesuchStichtag(
			LocalDateTime.now(),
			null);

		assertNotNull(uploadFileInfo.getBytes());
		unitTestTempfolder.writeToTempDir(uploadFileInfo.getBytes(), "ExcelReportGesuchStichtag.xlsx");
	}

	@Test
	public void generateExcelReportGesuchZeitraum() throws Exception {
		UploadFileInfo uploadFileInfo = reportService.generateExcelReportGesuchZeitraum(
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(1),
			null);

		assertNotNull(uploadFileInfo.getBytes());
		unitTestTempfolder.writeToTempDir(uploadFileInfo.getBytes(), "ExcelReportGesuchZeitraum.xlsx");
	}

	@Test
	public void generateExcelReportZahlungAuftrag() throws Exception {
		UploadFileInfo uploadFileInfo = reportService.generateExcelReportZahlungAuftrag("5a1fde7d-991a-4aef-8de2-43387db4f87d");

		assertNotNull(uploadFileInfo);
		unitTestTempfolder.writeToTempDir(uploadFileInfo.getBytes(), "ExcelReportZahlungAuftrag.xlsx");
	}

	@Test
	public void generateExcelReportKanton() throws Exception {
		Collection<Gesuch> allGesuche = gesuchService.getAllGesuche();
		for (Gesuch gesuch : allGesuche) {
			if (gesuch.getStatus().isAnyStatusOfVerfuegt()) {
				System.out.println("verfuegtes Gesuch: " + gesuch.getId());
			}
		}
		UploadFileInfo uploadFileInfo = reportService.generateExcelReportKanton(TestDataUtil.START_PERIODE, TestDataUtil.ENDE_PERIODE);

		assertNotNull(uploadFileInfo.getBytes());
		unitTestTempfolder.writeToTempDir(uploadFileInfo.getBytes(), "ExcelReportKanton.xlsx");
	}
}

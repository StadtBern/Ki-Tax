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
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
		// 5 Fälle, 6 Gesuche (d.h. 1 Gesuch mit Mutation)
		// Je einmal Mahnung, Beschwerde, Nicht-Freigegeben, Nicht-Eintreten, Abwesenheit
		Collection<Mandant> allMandant = criteriaQueryHelper.getAll(Mandant.class);
		Collection<Gesuchsperiode> allGesuchsperiode = criteriaQueryHelper.getAll(Gesuchsperiode.class); //1
		Collection<Fall> allFall = criteriaQueryHelper.getAll(Fall.class);//6
		Collection<Gesuch> allGesuch = criteriaQueryHelper.getAll(Gesuch.class);//7

		Assert.assertEquals("1 Mandant aus File, einer aus Superklasse", 2, allMandant.size());
		Assert.assertEquals(1, allGesuchsperiode.size());
		Assert.assertEquals(6, allFall.size());
		Assert.assertEquals(7, allGesuch.size());
	}

	@Test
	@Ignore //TODO (medu): Test laeuft im Moment nicht durch, da Mutationen Betreuung und Abwesenheit immer fuer alle Betreuungen des Gesuchs gezaehlt werden!
	public void testGetReportDataGesuchStichtag() throws Exception {
		List<GesuchStichtagDataRow> reportData = reportService.getReportDataGesuchStichtag(LocalDateTime.now(), null);

		assertNotNull(reportData);
		// Wir haben je 1 Gesuch mit Mahnung, 1 Gesuch mit Beschwerde und 1 nicht freigegebenes Gesuch
		assertEquals(4, reportData.size());
		// Fall 105 mit 2 Betreuungen: Mahnung
		assertGesuchStichtagDataRow(reportData.get(0), "17.000105.2.1", 0, 1, 0);
		assertGesuchStichtagDataRow(reportData.get(1), "17.000105.1.1", 0, 1, 0);
		// Fall 107 mit 1 Betreuung: Nicht freigegeben
		assertGesuchStichtagDataRow(reportData.get(2), "17.000107.1.1", 1, 0, 0);
		// Fall 104 mit 1 Betreuung: Beschwerde
		assertGesuchStichtagDataRow(reportData.get(3), "17.000104.1.1", 0, 0, 1);
	}

	@Test
	@Ignore //TODO (medu): Test laeuft im Moment nicht durch, da Mutationen Betreuung und Abwesenheit immer fuer alle Betreuungen des Gesuchs gezaehlt werden!
	public void testGetReportDataGesuchZeitraumTest() throws Exception {
		List<GesuchZeitraumDataRow> reportData = reportService.getReportDataGesuchZeitraum(
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(1),
			null);

		assertNotNull(reportData);
		assertEquals(7, reportData.size());
		// Fall 102 mit2 Betreuungen, Erstgesuch plus Mutation. Die zweite Betreuung kam in der Mutation dazu
		// Fall 102: Betreuung 1: Kommt einmal im Gesuch (verfügt) und einmal in der Mutation vor (Mutation Betreuung und Abwesenheit)
		// Mutation
		assertGesuchZeitraumDataRow(reportData.get(0), "17.000102.1.1", 1, 1, 0, 0, 0, 0, 0);
		// Erstgesuch
		assertGesuchZeitraumDataRow(reportData.get(1), "17.000102.1.1", 0, 0, 0, 0, 1, 1, 0);
		// Fall 102: Betreuung 2: Erst in Mutation hinzugekommen
		assertGesuchZeitraumDataRow(reportData.get(0), "17.000102.1.2", 1, 1, 0, 0, 0, 0, 0);
		// Fall 104: Verfuegt, mit Beschwerde
		assertGesuchZeitraumDataRow(reportData.get(1), "17.000104.1.1", 0, 0, 0, 1, 1, 1, 0);
		// Fall 106: Verfuegt mit Nicht-Eintreten
		assertGesuchZeitraumDataRow(reportData.get(1), "17.000106.1.1", 0, 0, 0, 0, 1, 0, 1);
		// Fall 105 mit 2 Kindern mit je 1 Betreuung: Mahnung
		assertGesuchZeitraumDataRow(reportData.get(1), "17.000105.1.1", 0, 0, 1, 0, 0, 0, 0);
		assertGesuchZeitraumDataRow(reportData.get(1), "17.000105.2.1", 0, 0, 1, 0, 0, 0, 0);
	}

	private void assertGesuchStichtagDataRow(GesuchStichtagDataRow row, String bgNummer, Integer nichtFreigegeben, Integer mahnung, Integer beschwerde) {
		assertNotNull(row);
		assertEquals(bgNummer, row.getBgNummer());
		assertEquals(nichtFreigegeben, row.getNichtFreigegeben());
		assertEquals(mahnung, row.getMahnungen());
		assertEquals(beschwerde, row.getBeschwerde());
	}

	private void assertGesuchZeitraumDataRow(GesuchZeitraumDataRow row, String bgNummer,
											 Integer anzahlMutationAbwesenheit, Integer anzahlMutationBetreuung,
											 Integer anzahlMahnungen, Integer anzahlBeschwerde, Integer anzahlVerfuegungen,
											 Integer anzahlVerfuegungenNormal, Integer anzahlVerfuegungenNichtEintreten) {
		assertNotNull(row);
		assertEquals(bgNummer, row.getBgNummer());
		assertEquals(anzahlMutationAbwesenheit, row.getAnzahlMutationAbwesenheit());
		assertEquals(anzahlMutationBetreuung, row.getAnzahlMutationBetreuung());
		assertEquals(anzahlMahnungen, row.getAnzahlMahnungen());
		assertEquals(anzahlBeschwerde, row.getAnzahlBeschwerde());
		assertEquals(anzahlVerfuegungen, row.getAnzahlVerfuegungen());
		assertEquals(anzahlVerfuegungenNormal, row.getAnzahlVerfuegungenNormal());
		assertEquals(anzahlVerfuegungenNichtEintreten, row.getAnzahlVerfuegungenNichtEintreten());
	}

	@Test
	public void generateExcelReportGesuchStichtag() throws Exception {
		byte[] bytes = reportService.generateExcelReportGesuchStichtag(
			LocalDateTime.now(),
			null);

		assertNotNull(bytes);
		unitTestTempfolder.writeToTempDir(bytes, "ExcelReportGesuchStichtag.xlsx");
	}

	@Test
	public void generateExcelReportGesuchZeitraum() throws Exception {
		byte[] bytes = reportService.generateExcelReportGesuchZeitraum(
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(1),
			null);

		assertNotNull(bytes);
		unitTestTempfolder.writeToTempDir(bytes, "ExcelReportGesuchZeitraum.xlsx");
	}

	@Test
	public void generateExcelReportZahlungAuftrag() throws Exception {
		byte[] bytes = reportService.generateExcelReportZahlungAuftrag("8d2805ed-d123-4632-bcbb-931dd7a936ae", Optional.empty());

		assertNotNull(bytes);
		unitTestTempfolder.writeToTempDir(bytes, "ExcelReportZahlungAuftrag.xlsx");
	}
}

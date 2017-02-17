package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Eingangsart;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GesuchStichtagDataRow;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GesuchZeitraumDataRow;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.ReportService;
import ch.dvbern.ebegu.services.WizardStepService;
import ch.dvbern.ebegu.tests.util.UnitTestTempFolder;
import ch.dvbern.ebegu.tets.TestDataUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(Arquillian.class)
@UsingDataSet("datasets/empty.xml")
@Transactional(TransactionMode.DISABLED)
public class ReportServiceBeanTest extends AbstractEbeguLoginTest {

	@Rule
	public UnitTestTempFolder unitTestTempfolder = new UnitTestTempFolder();

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

	//todo sinnvolle testdaten finden
	private void setupTestData()
	{
		Gesuch gesuch = TestDataUtil.createAndPersistFeutzYvonneGesuch(institutionService, persistence, LocalDate.now());
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

	}

	@Test
	public void testGetReportDataGesuchStichtag() throws Exception {

		setupTestData();

		List<GesuchStichtagDataRow> reportData = reportService.getReportDataGesuchStichtag(
			LocalDateTime.now().plusDays(1),
			null);

		assertNotNull(reportData);
		assertEquals(2, reportData.size());

	}

	@Test
	public void testGetReportDataGesuchZeitraumTest() throws Exception {

		setupTestData();

		List<GesuchZeitraumDataRow> reportData = reportService.getReportDataGesuchZeitraum(
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(1),
			null);

		assertNotNull(reportData);
		assertEquals(2, reportData.size());

	}

	@Test
	public void generateExcelReportGesuchStichtag() throws Exception {

		setupTestData();

		byte[] bytes = reportService.generateExcelReportGesuchStichtag(
			LocalDateTime.now().plusDays(1),
			null);

		assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "ExcelReportGesuchStichtag.xlsx");

	}

	@Test
	public void generateExcelReportGesuchZeitraum() throws Exception {

		setupTestData();

		byte[] bytes = reportService.generateExcelReportGesuchZeitraum(
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(1),
			null);

		assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "ExcelReportGesuchZeitraum.xlsx");

	}

}

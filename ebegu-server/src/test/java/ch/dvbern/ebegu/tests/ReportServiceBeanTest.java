package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.rechner.AbstractBGRechnerTest;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GesuchStichtagDataRow;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GeuschStichtagExcelConverter;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GesuchZeitraumDataRow;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GeuschZeitraumExcelConverter;
import ch.dvbern.ebegu.reporting.lib.DateUtil;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.ebegu.services.ReportServiceBean;
import ch.dvbern.ebegu.testfaelle.Testfall01_WaeltiDagmar;
import ch.dvbern.ebegu.tests.util.UnitTestTempFolder;
import ch.dvbern.ebegu.tets.TestDataUtil;
import de.akquinet.jbosscc.needle.annotation.InjectIntoMany;
import de.akquinet.jbosscc.needle.annotation.ObjectUnderTest;
import de.akquinet.jbosscc.needle.junit.DatabaseRule;
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertNotNull;

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
public class ReportServiceBeanTest {

	@Rule
	public UnitTestTempFolder unitTestTempfolder = new UnitTestTempFolder();

	@Rule
	public NeedleRule needleRule = new NeedleRule().withOuter(new DatabaseRule());

	@ObjectUnderTest
	private ReportServiceBean reportService;

	@Inject
	private EntityManagerFactory entityManagerFactory;

	@Inject
	private EntityManager entityManager;

	@InjectIntoMany
	private GeuschStichtagExcelConverter geuschStichtagExcelConverter = new GeuschStichtagExcelConverter();

	@InjectIntoMany
	private GeuschZeitraumExcelConverter geuschZeitraumExcelConverter = new GeuschZeitraumExcelConverter();

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
	public void testGesuchStichtagQueryAssumptions()
	{
		Assert.assertEquals(AntragStatus.IN_BEARBEITUNG_JA.name(), "IN_BEARBEITUNG_JA");
		Assert.assertEquals(AntragStatus.FREIGABEQUITTUNG.name(), "FREIGABEQUITTUNG");
		Assert.assertEquals(AntragStatus.BESCHWERDE_HAENGIG.name(), "BESCHWERDE_HAENGIG");
		Assert.assertEquals(BetreuungsangebotTyp.TAGESSCHULE.name(), "TAGESSCHULE");
	}

	@Test
	public void getReportDataGesuchStichtag() throws Exception {

		//entityManager.merge(gesuch_1GS);

		List<GesuchStichtagDataRow> reportData = reportService.getReportDataGesuchStichtag(
			LocalDateTime.parse("2016-12-12 00:00:00", DateUtil.SQL_DATETIME_FORMAT),
			null);

		assertNotNull(reportData);

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
	public void testGesuchZeitraumQueryAssumptions()
	{
		Assert.assertEquals(Eingangsart.ONLINE.name(), "ONLINE");
		Assert.assertEquals(Eingangsart.PAPIER.name(), "PAPIER");
		Assert.assertEquals(AntragTyp.GESUCH.name(), "GESUCH");
		Assert.assertEquals(AntragTyp.MUTATION.name(), "MUTATION");

		Assert.assertEquals(WizardStepName.ABWESENHEIT.name(), "ABWESENHEIT");
		Assert.assertEquals(WizardStepName.BETREUUNG.name(), "BETREUUNG");
		Assert.assertEquals(WizardStepName.DOKUMENTE.name(), "DOKUMENTE");
		Assert.assertEquals(WizardStepName.EINKOMMENSVERSCHLECHTERUNG.name(), "EINKOMMENSVERSCHLECHTERUNG");
		Assert.assertEquals(WizardStepName.ERWERBSPENSUM.name(), "ERWERBSPENSUM");
		Assert.assertEquals(WizardStepName.FAMILIENSITUATION.name(), "FAMILIENSITUATION");
		Assert.assertEquals(WizardStepName.FINANZIELLE_SITUATION.name(), "FINANZIELLE_SITUATION");
		Assert.assertEquals(WizardStepName.FREIGABE.name(), "FREIGABE");
		Assert.assertEquals(WizardStepName.GESUCH_ERSTELLEN.name(), "GESUCH_ERSTELLEN");
		Assert.assertEquals(WizardStepName.GESUCHSTELLER.name(), "GESUCHSTELLER");
		Assert.assertEquals(WizardStepName.KINDER.name(), "KINDER");
		Assert.assertEquals(WizardStepName.UMZUG.name(), "UMZUG");
		Assert.assertEquals(WizardStepName.VERFUEGEN.name(), "VERFUEGEN");
		Assert.assertEquals(WizardStepStatus.MUTIERT.name(), "MUTIERT");

		Assert.assertEquals(AntragStatus.BESCHWERDE_HAENGIG.name(), "BESCHWERDE_HAENGIG");
		Assert.assertEquals(BetreuungsangebotTyp.TAGESSCHULE.name(), "TAGESSCHULE");
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

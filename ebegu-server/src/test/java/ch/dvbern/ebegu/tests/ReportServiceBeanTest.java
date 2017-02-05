package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.reporting.gesuchperiode.GesuchPeriodeDataRow;
import ch.dvbern.ebegu.reporting.gesuchperiode.GeuschPeriodeExcelConverter;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GesuchStichtagDataRow;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GeuschStichtagExcelConverter;
import ch.dvbern.ebegu.services.ReportServiceBean;
import ch.dvbern.ebegu.tests.util.UnitTestTempFolder;
import ch.dvbern.ebegu.reporting.lib.DateUtil;
import de.akquinet.jbosscc.needle.annotation.InjectIntoMany;
import de.akquinet.jbosscc.needle.annotation.ObjectUnderTest;
import de.akquinet.jbosscc.needle.junit.DatabaseRule;
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.List;

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

	@InjectIntoMany
	private GeuschStichtagExcelConverter geuschStichtagExcelConverter = new GeuschStichtagExcelConverter();

	@InjectIntoMany
	private GeuschPeriodeExcelConverter geuschPeriodeExcelConverter  = new GeuschPeriodeExcelConverter();

	@Before
	public void setupTestData() {

	}

	@Test
	public void getReportDataGesuchStichtag() throws Exception {

		List<GesuchStichtagDataRow> reportData = reportService.getReportDataGesuchStichtag(
			LocalDateTime.parse("2017-01-25 00:00:00", DateUtil.SQL_DATETIME_FORMAT),
			"0621fb5d-a187-5a91-abaf-8a813c4d263a");

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
	public void getReportDataGesuchPeriode() throws Exception {

		List<GesuchPeriodeDataRow> reportData = reportService.getReportDataGesuchPeriode(
			LocalDateTime.parse("2017-01-25 00:00:00", DateUtil.SQL_DATETIME_FORMAT),
			LocalDateTime.parse("2017-01-26 23:59:59", DateUtil.SQL_DATETIME_FORMAT),
			"0621fb5d-a187-5a91-abaf-8a813c4d263a");

		assertNotNull(reportData);

	}

	@Test
	public void generateExcelReportGesuchPeriode() throws Exception {

		byte[] bytes = reportService.generateExcelReportGesuchPeriode(
			LocalDateTime.parse("2017-01-25 00:00:00", DateUtil.SQL_DATETIME_FORMAT),
			LocalDateTime.parse("2017-01-26 23:59:59", DateUtil.SQL_DATETIME_FORMAT),
			"0621fb5d-a187-5a91-abaf-8a813c4d263a");

		assertNotNull(bytes);

		unitTestTempfolder.writeToTempDir(bytes, "ExcelReportGesuchPeriode.xlsx");

	}


}

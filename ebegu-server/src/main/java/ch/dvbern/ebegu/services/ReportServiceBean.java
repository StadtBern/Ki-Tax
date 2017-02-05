package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.reporting.gesuchperiode.GesuchPeriodeDataRow;
import ch.dvbern.ebegu.reporting.gesuchperiode.GeuschPeriodeExcelConverter;
import ch.dvbern.ebegu.reporting.gesuchperiode.MergeFieldGesuchPeriode;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GesuchStichtagDataRow;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GeuschStichtagExcelConverter;
import ch.dvbern.ebegu.reporting.gesuchstichtag.MergeFieldGesuchStichtag;
import ch.dvbern.ebegu.reporting.lib.ExcelMergeException;
import ch.dvbern.ebegu.reporting.lib.ExcelMerger;
import ch.dvbern.ebegu.reporting.lib.ExcelMergerDTO;
import ch.dvbern.ebegu.reporting.lib.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static ch.dvbern.ebegu.services.ReportServiceBean.ReportResource.*;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 31/01/2017.
 */
@Stateless
@Local(ReportService.class)
public class ReportServiceBean extends AbstractReportServiceBean implements ReportService {

	@Inject
	private GeuschStichtagExcelConverter geuschStichtagExcelConverter;

	@Inject
	private GeuschPeriodeExcelConverter geuschPeriodeExcelConverter;

	@Override
	public List<GesuchStichtagDataRow> getReportDataGesuchStichtag(@Nonnull LocalDateTime datetime, @Nullable String gesuchPeriodeID) throws IOException, URISyntaxException {

		Objects.requireNonNull(datetime, "Das Argument 'date' darf nicht leer sein");

		EntityManager em = createEntityManager();
		Query gesuchStichtagQuery = null;
		List<GesuchStichtagDataRow> results = null;

		URL url = ReportServiceBean.class.getResource(NATIVESQL_REPORT_GESUCH_STICHTAG.getPath());

		String sqlString = new String(Files.readAllBytes(
			Paths.get(url.toURI()))
			,StandardCharsets.UTF_8);

		if (em != null) {
			gesuchStichtagQuery = em.createNativeQuery(sqlString, "GesuchStichtagDataRowMapping");
			gesuchStichtagQuery.setParameter("stichTagDate", DateUtil.SQL_DATETIME_FORMAT.format(datetime));
			gesuchStichtagQuery.setParameter("gesuchPeriodeID", gesuchPeriodeID);
			results = gesuchStichtagQuery.getResultList();
		}

		return results;
	}

	@Override
	public byte[] generateExcelReportGesuchStichtag(@Nonnull LocalDateTime datetime, @Nullable String gesuchPeriodeID) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException {

		Objects.requireNonNull(datetime, "Das Argument 'date' darf nicht leer sein");

		InputStream is = ReportServiceBean.class.getResourceAsStream(VORLAGE_REPORT_GESUCH_STICHTAG.getPath());
		Objects.requireNonNull(is, "Vorlage '" + VORLAGE_REPORT_GESUCH_STICHTAG.getPath() + "' nicht gefunden");

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet("Data");

		List<GesuchStichtagDataRow> reportData = getReportDataGesuchStichtag(datetime, gesuchPeriodeID);
		ExcelMergerDTO excelMergerDTO = geuschStichtagExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault());

		mergeData(sheet, excelMergerDTO, MergeFieldGesuchStichtag.values());
		geuschStichtagExcelConverter.applyAutoSize(sheet);

		return createWorkbook(workbook);

	}

	@Override
	public List<GesuchPeriodeDataRow> getReportDataGesuchPeriode(@Nonnull LocalDateTime datetimeVon, @Nonnull LocalDateTime datetimeBis, @Nullable String gesuchPeriodeID) throws IOException, URISyntaxException {

		Objects.requireNonNull(datetimeVon, "Das Argument 'datetimeVon' darf nicht leer sein");
		Objects.requireNonNull(datetimeBis, "Das Argument 'datetimeBis' darf nicht leer sein");

		EntityManager em = createEntityManager();
		Query gesuchPeriodeQuery = null;
		List<GesuchPeriodeDataRow> results = null;

		URL url = ReportServiceBean.class.getResource(NATIVESQL_REPORT_GESUCH_PERIODE.getPath());

		String sqlString = new String(Files.readAllBytes(
			Paths.get(url.toURI()))
			,StandardCharsets.UTF_8);

		if (em != null) {
			gesuchPeriodeQuery = em.createNativeQuery(sqlString, "GesuchPeriodeDataRowMapping");
			gesuchPeriodeQuery.setParameter("fromDate", DateUtil.SQL_DATETIME_FORMAT.format(datetimeVon));
			gesuchPeriodeQuery.setParameter("toDate", DateUtil.SQL_DATETIME_FORMAT.format(datetimeBis));
			gesuchPeriodeQuery.setParameter("gesuchPeriodeID", gesuchPeriodeID);
			results = gesuchPeriodeQuery.getResultList();
		}

		return results;
	}

	@Override
	public byte[] generateExcelReportGesuchPeriode(@Nonnull LocalDateTime datetimeVon, @Nonnull LocalDateTime datetimeBis, @Nullable String gesuchPeriodeID) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException {

		Objects.requireNonNull(datetimeVon, "Das Argument 'datetimeVon' darf nicht leer sein");
		Objects.requireNonNull(datetimeBis, "Das Argument 'datetimeBis' darf nicht leer sein");

		InputStream is = ReportServiceBean.class.getResourceAsStream(VORLAGE_REPORT_GESUCH_PERIODE.getPath());
		Objects.requireNonNull(is, "Vorlage '" + VORLAGE_REPORT_GESUCH_PERIODE.getPath() + "' nicht gefunden");

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet("Data");

		List<GesuchPeriodeDataRow> reportData = getReportDataGesuchPeriode(datetimeVon, datetimeBis, gesuchPeriodeID);
		ExcelMergerDTO excelMergerDTO = geuschPeriodeExcelConverter.toExcelMergerDTO(reportData, Locale.getDefault());

		mergeData(sheet, excelMergerDTO, MergeFieldGesuchPeriode.values());
		geuschPeriodeExcelConverter.applyAutoSize(sheet);

		return createWorkbook(workbook);

	}

	public enum ReportResource {

		// Gesuch Stichtag Report
		VORLAGE_REPORT_GESUCH_STICHTAG("/reporting/GesuchStichtag.xlsx"),
		VORLAGE_REPORT_GESUCH_PERIODE("/reporting/GesuchPeriode.xlsx"),

		NATIVESQL_REPORT_GESUCH_STICHTAG("/reporting/GesuchStichtagNativeSQLQuery.sql"),
		NATIVESQL_REPORT_GESUCH_PERIODE("/reporting/GesuchPeriodeNativeSQLQuery.sql");

		private String path;

		ReportResource(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}
	}

}

/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.batch.jobs.report;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.batch.api.AbstractBatchlet;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import ch.dvbern.ebegu.enums.WorkJobConstants;
import ch.dvbern.ebegu.enums.reporting.ReportVorlage;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.reporting.ReportMassenversandService;
import ch.dvbern.ebegu.reporting.ReportService;
import ch.dvbern.ebegu.util.DateUtil;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.oss.lib.excelmerger.ExcelMergeException;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.ebegu.enums.WorkJobConstants.REPORT_VORLAGE_TYPE_PARAM;

@SuppressWarnings("ClassNamePrefixedWithPackageName")
@Named("reportJobGeneratorBatchlet")
@Dependent
public class ReportJobGeneratorBatchlet extends AbstractBatchlet {

	private static final Logger LOG = LoggerFactory.getLogger(ReportJobGeneratorBatchlet.class);

	@Inject
	private ReportService reportService;

	@Inject
	private ReportMassenversandService reportMassenversandService;

	@Inject
	private JobContext jobCtx;

	@Inject
	private JobDataContainer jobDataContainer;


	@Override
	public String process() {
		String typeProp = getParameters().getProperty(REPORT_VORLAGE_TYPE_PARAM);
		LOG.info("processing report generation job for type {}", typeProp);
		final ReportVorlage reportType = ReportVorlage.valueOf(typeProp);
		try {
			final UploadFileInfo uploadFileInfo = triggerReportGeneration(reportType); //gespeichertes file
			jobDataContainer.setResult(uploadFileInfo);
			LOG.debug("Report File was successfully generated for workjob {}", jobCtx.getExecutionId());
			return BatchStatus.COMPLETED.toString(); // success

		} catch (ExcelMergeException | MergeDocException e) {
			LOG.error("ExcelMergeException occured while creating a report in a batch process ", e);
		} catch (URISyntaxException | IOException e) {
				LOG.error("IOException occured while creating a report in a batch process, maybe template could not be loaded?", e);
		}
		return BatchStatus.FAILED.toString();
	}

	@SuppressWarnings("UnnecessaryLocalVariable")
	@Nonnull
	private UploadFileInfo triggerReportGeneration(ReportVorlage workJobType) throws ExcelMergeException, MergeDocException, URISyntaxException, IOException {

		final String datumVonOrStichtag = getParameters().getProperty(WorkJobConstants.DATE_FROM_PARAM);
		LocalDate dateFrom = DateUtil.parseStringToDateOrReturnNow(datumVonOrStichtag);
		final String datumToStichtag = getParameters().getProperty(WorkJobConstants.DATE_TO_PARAM);
		LocalDate dateTo = DateUtil.parseStringToDateOrReturnNow(datumToStichtag);
		final String gesuchPeriodeID = getParameters().getProperty(WorkJobConstants.GESUCH_PERIODE_ID_PARAM);
		final String zahlungsauftragId = getParameters().getProperty(WorkJobConstants.ZAHLUNGSAUFTRAG_ID_PARAM);
		return generateReport(workJobType, dateFrom, dateTo, gesuchPeriodeID, zahlungsauftragId);
	}

	@Nonnull
	private UploadFileInfo generateReport(
		@Nonnull ReportVorlage workJobType,
		@Nonnull LocalDate dateFrom,
		@Nonnull LocalDate dateTo,
		@Nonnull String gesuchPeriodeID,
		@Nullable String zahlungsauftragId
	) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException {

		switch (workJobType) {

		case VORLAGE_REPORT_GESUCH_STICHTAG: {
			final UploadFileInfo uploadFileInfo = this.reportService.generateExcelReportGesuchStichtag(dateFrom, gesuchPeriodeID);
			return uploadFileInfo;
		}
		case VORLAGE_REPORT_GESUCH_ZEITRAUM: {
			final UploadFileInfo uploadFileInfo = this.reportService.generateExcelReportGesuchZeitraum(dateFrom, dateTo, gesuchPeriodeID);
			return uploadFileInfo;
		}
		case VORLAGE_REPORT_KANTON: {
			final UploadFileInfo uploadFileInfo = this.reportService.generateExcelReportKanton(dateFrom, dateTo);
			return uploadFileInfo;
		}
		case VORLAGE_REPORT_MITARBEITERINNEN: {
			final UploadFileInfo uploadFileInfo = this.reportService.generateExcelReportMitarbeiterinnen(dateFrom, dateTo);
			return uploadFileInfo;
		}
		case VORLAGE_REPORT_BENUTZER: {
			final UploadFileInfo uploadFileInfo = this.reportService.generateExcelReportBenutzer();
			return uploadFileInfo;
		}
		case VORLAGE_REPORT_ZAHLUNG_AUFTRAG: {
			Validate.notNull(zahlungsauftragId, "Zahlungsauftrag ID must be passed as param");
			final UploadFileInfo uploadFileInfo = this.reportService.generateExcelReportZahlungAuftrag(zahlungsauftragId);
			return uploadFileInfo;
		}
		case VORLAGE_REPORT_ZAHLUNG_AUFTRAG_PERIODE: {
			final UploadFileInfo uploadFileInfo = this.reportService.generateExcelReportZahlungPeriode(gesuchPeriodeID);
			return uploadFileInfo;
		}
		case VORLAGE_REPORT_GESUCHSTELLER_KINDER_BETREUUNG: {
			final UploadFileInfo uploadFileInfo = this.reportService.generateExcelReportGesuchstellerKinderBetreuung(dateFrom, dateTo, gesuchPeriodeID);
			return uploadFileInfo;
		}
		case VORLAGE_REPORT_KINDER: {
			final UploadFileInfo uploadFileInfo = this.reportService.generateExcelReportKinder(dateFrom, dateTo, gesuchPeriodeID);
			return uploadFileInfo;
		}
		case VORLAGE_REPORT_GESUCHSTELLER: {
			final UploadFileInfo uploadFileInfo = this.reportService.generateExcelReportGesuchsteller(dateFrom);
			return uploadFileInfo;
		}
		case VORLAGE_REPORT_MASSENVERSAND: {
			boolean inklBgGesuche = Boolean.valueOf(getParameters().getProperty(WorkJobConstants.INKL_BG_GESUCHE));
			boolean inklMischGesuche = Boolean.valueOf(getParameters().getProperty(WorkJobConstants.INKL_MISCH_GESUCHE));
			boolean inklTsGesuche = Boolean.valueOf(getParameters().getProperty(WorkJobConstants.INKL_TS_GESUCHE));
			boolean ohneFolgegesuche = Boolean.valueOf(getParameters().getProperty(WorkJobConstants.OHNE_ERNEUERUNGSGESUCHE));
			final String text = getParameters().getProperty(WorkJobConstants.TEXT);
			UploadFileInfo uploadFileInfo = reportMassenversandService.generateExcelReportMassenversand(
				dateFrom,
				dateTo,
				gesuchPeriodeID,
				inklBgGesuche,
				inklMischGesuche,
				inklTsGesuche,
				ohneFolgegesuche,
				text);
			return uploadFileInfo;
		}
		}
		throw new IllegalArgumentException("No Report generated: Unknown ReportType: " + workJobType);
	}

	private Properties getParameters() {
		JobOperator operator = BatchRuntime.getJobOperator();
		return operator.getParameters(jobCtx.getExecutionId());
	}
}

/*
 * Copyright © 2017 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.batch.jobs.report;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Properties;

import javax.batch.api.AbstractBatchlet;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.dvbern.ebegu.enums.WorkJobConstants;
import ch.dvbern.ebegu.enums.reporting.ReportVorlage;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.reporting.ReportService;
import ch.dvbern.ebegu.util.DateUtil;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.oss.lib.excelmerger.ExcelMergeException;

import static ch.dvbern.ebegu.enums.WorkJobConstants.REPORT_VORLAGE_TYPE_PARAM;

@SuppressWarnings("ClassNamePrefixedWithPackageName")
@Named
@Dependent
public class ReportJobGeneratorBatchlet extends AbstractBatchlet {

	private static final Logger LOG = LoggerFactory.getLogger(ReportJobGeneratorBatchlet.class);

	@Inject
	private ReportService reportService;


	@Inject
	private JobContext jobCtx;

//	@Inject
//	private StepContext stepCtx;

	@Inject
	JobDataContainer jobDataContainer;


	@Override
	public String process() {
		String typeProp = getParameters().getProperty(REPORT_VORLAGE_TYPE_PARAM);
		LOG.info("processing report generation job for type " + typeProp);
		final ReportVorlage reportType = ReportVorlage.valueOf(typeProp);
		try {
			final UploadFileInfo uploadFileInfo = triggerReportGeneration(reportType); //gespeichertes file
			jobDataContainer.setresult(uploadFileInfo);
			LOG.debug("Report File was successfully generated for workjob ", jobCtx.getExecutionId());
			return BatchStatus.COMPLETED.toString(); // success

		} catch (ExcelMergeException | MergeDocException e) {
			LOG.error("ExcelMergeException occured while creating a report in a batch process ", e);
		} catch (URISyntaxException | IOException e) {
			LOG.error("IOException occured while creating a report in a batch process, maybe template could not be loaded?", e);
		}
		return BatchStatus.FAILED.toString();

	}


	@SuppressWarnings("UnnecessaryLocalVariable")
	private UploadFileInfo triggerReportGeneration(ReportVorlage workJobType) throws ExcelMergeException, MergeDocException, URISyntaxException, IOException {

		final String datumVonOrStichtag = getParameters().getProperty(WorkJobConstants.DATE_FROM_PARAM);
		LocalDate dateFrom = DateUtil.parseStringToDateOrReturnNow(datumVonOrStichtag);
		final String datumToStichtag = getParameters().getProperty(WorkJobConstants.DATE_TO_PARAM);
		LocalDate dateTo = DateUtil.parseStringToDateOrReturnNow(datumToStichtag);
		final String gesuchPeriodeID = getParameters().getProperty(WorkJobConstants.GESUCH_PERIODE_ID_PARAM);
		final String zahlungsauftragId = getParameters().getProperty(WorkJobConstants.ZAHLUNGSAUFTRAG_ID_PARAM);

		return generateReport(workJobType, dateFrom, dateTo, gesuchPeriodeID, zahlungsauftragId);

	}

	private UploadFileInfo generateReport(ReportVorlage workJobType, LocalDate dateFrom, LocalDate dateTo, String gesuchPeriodeID, String zahlungsauftragId) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException {
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
		}

		LOG.warn("No Report generated ");
		throw new IllegalArgumentException("Unknown ReportType: " + workJobType);
	}

	private Properties getParameters() {
		JobOperator operator = BatchRuntime.getJobOperator();
		return operator.getParameters(jobCtx.getExecutionId());

	}
}

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

import java.util.Properties;

import javax.batch.api.AbstractBatchlet;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.dvbern.ebegu.entities.DownloadFile;
import ch.dvbern.ebegu.entities.Workjob;
import ch.dvbern.ebegu.enums.WorkJobConstants;
import ch.dvbern.ebegu.errors.MailException;
import ch.dvbern.ebegu.services.MailService;
import ch.dvbern.ebegu.services.WorkjobService;
import ch.dvbern.ebegu.util.UploadFileInfo;

@Named
@Dependent
public class SendEmailBatchlet extends AbstractBatchlet {

	private static final Logger LOG = LoggerFactory.getLogger(SendEmailBatchlet.class);


	@Inject
	private WorkjobService workJobService;


	@Inject
	private MailService mailService;


	@Inject
	private JobContext jobCtx;

	@Inject
	private StepContext stepCtx;

	@Override
	public String process() {
		final String receiverEmail = getParameters().getProperty(WorkJobConstants.EMAIL_OF_USER);
		LOG.info("Sending mail with file for user to {}", receiverEmail);
		Validate.notNull(receiverEmail, " Email muss gesetzt sein damit der Fertige Report an den Empfaenger geschickt werden kann");
		final DownloadFile downloadFile = createDownloadLink();
		try {
			mailService.sendDocumentCreatedEmail(receiverEmail, downloadFile);
			return BatchStatus.COMPLETED.toString();
		} catch (MailException e) {
			return BatchStatus.FAILED.toString();
		}
	}

	private DownloadFile createDownloadLink() {
		final Object transientUserData = jobCtx.getTransientUserData();

		if (transientUserData instanceof UploadFileInfo) {

			final Workjob workJob = workJobService.findWorkjobByExecutionId(jobCtx.getExecutionId());
			Validate.notNull(workJob, "Workjob muss existieren");

			UploadFileInfo fileInfo = (UploadFileInfo) transientUserData; //passed result
			return  new DownloadFile(fileInfo, workJob.getTriggeringIp());

		} else{
			LOG.error("UploadFileInfo muss uebergeben werden vom vorherigen Step");
			return null;
		}
	}

	private Properties getParameters() {
		JobOperator operator = BatchRuntime.getJobOperator();
		return operator.getParameters(jobCtx.getExecutionId());

	}
}

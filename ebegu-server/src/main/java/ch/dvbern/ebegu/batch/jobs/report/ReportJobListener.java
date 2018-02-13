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

import javax.batch.api.listener.AbstractJobListener;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.dvbern.ebegu.enums.reporting.BatchJobStatus;
import ch.dvbern.ebegu.services.WorkjobService;

@Named("reportJobListener")
@Dependent
public class ReportJobListener extends AbstractJobListener {

	private static final Logger LOG = LoggerFactory.getLogger(ReportJobListener.class);

	@Inject
	private JobContext ctx;

	@Inject
	private WorkjobService workjobService;

	@Override
	public void beforeJob() {
		LOG.debug("ReportJobListener started: {}", ctx.getExecutionId());
		workjobService.changeStateOfWorkjob(ctx.getExecutionId(), BatchJobStatus.RUNNING);
	}

	@Override
	public void afterJob() {
		LOG.debug("ReportJobListener finished: {}, status: {},{}",
			ctx.getExecutionId(), ctx.getBatchStatus(), ctx.getExitStatus());
		workjobService.changeStateOfWorkjob(ctx.getExecutionId(), BatchJobStatus.FINISHED);
	}
}

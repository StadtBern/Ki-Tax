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

import javax.batch.api.listener.AbstractStepListener;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@Dependent
public class ReportJobStepListener extends AbstractStepListener {

	private static final Logger LOG = LoggerFactory.getLogger(ReportJobStepListener.class);

	@Inject
	private JobContext jobCtx;

	@Inject
	private StepContext stepCtx;

	@Override
	public void beforeStep() {
		LOG.info("ReportJobStepListener started, job: {}, step: {}",
			jobCtx.getExecutionId(), stepCtx.getStepExecutionId());
	}

	@Override
	public void afterStep() {
		LOG.info("ReportJobStepListener finished, job: {}, step: {}",
			jobCtx.getExecutionId(), stepCtx.getStepExecutionId());
	}
}

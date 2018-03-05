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

import javax.batch.api.listener.AbstractJobListener;
import javax.batch.runtime.BatchStatus;
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
		if (ctx.getExitStatus().equals(BatchStatus.COMPLETED.name())) {
			workjobService.changeStateOfWorkjob(ctx.getExecutionId(), BatchJobStatus.FINISHED);
		} else {
			workjobService.changeStateOfWorkjob(ctx.getExecutionId(), BatchJobStatus.FAILED);
		}
	}
}

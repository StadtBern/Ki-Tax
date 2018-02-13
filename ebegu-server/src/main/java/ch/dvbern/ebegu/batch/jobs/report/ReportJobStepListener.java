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

import javax.batch.api.listener.AbstractStepListener;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("reportJobStepListener")
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

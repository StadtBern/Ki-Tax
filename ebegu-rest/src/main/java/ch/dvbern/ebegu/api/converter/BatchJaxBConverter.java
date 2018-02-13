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

package ch.dvbern.ebegu.api.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.batch.runtime.JobExecution;
import javax.enterprise.context.RequestScoped;

import ch.dvbern.ebegu.api.dtos.JaxAbstractDTO;
import ch.dvbern.ebegu.api.dtos.batch.JaxBatchJobInformation;
import ch.dvbern.ebegu.api.dtos.batch.JaxWorkJob;
import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.Workjob;
import ch.dvbern.ebegu.enums.WorkJobType;
import ch.dvbern.ebegu.enums.reporting.BatchJobStatus;

import static com.google.common.base.Preconditions.checkNotNull;

@RequestScoped
public class BatchJaxBConverter {

	@Nullable
	private LocalDateTime mangleDate(@Nullable Date date) {
		if (date == null) {
			return null;
		}
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	@Nonnull
	public JaxBatchJobInformation toBatchJobInformation(@Nonnull JobExecution information) {
		JaxBatchJobInformation jInformation = new JaxBatchJobInformation();

		jInformation.setBatchStatus(information.getBatchStatus().name());
		jInformation.setCreateTime(mangleDate(information.getCreateTime()));
		jInformation.setEndTime(mangleDate(information.getEndTime()));
		jInformation.setExecutionId(information.getExecutionId());
		jInformation.setExitStatus(information.getExitStatus());
		jInformation.setJobName(information.getJobName());
		jInformation.setLastUpdatedTime(mangleDate(information.getLastUpdatedTime()));
		jInformation.setStartTime(mangleDate(information.getStartTime()));
		jInformation.setLastUpdatedTime(mangleDate(information.getLastUpdatedTime()));

		return jInformation;
	}

	@Nonnull
	public JaxWorkJob toBatchJobInformation(@Nonnull Workjob job) {

		final WorkJobType workJobType = job.getWorkJobType();
		final String startinguser = job.getStartinguser();
		final BatchJobStatus status = job.getStatus();
		final String params = job.getParams();
		final Long executionId = job.getExecutionId();
		JaxWorkJob jaxWorkJob = new JaxWorkJob();
		convertAbstractFieldsToJAX(job, jaxWorkJob);

		jaxWorkJob.setWorkJobType(workJobType);
		jaxWorkJob.setStartinguser(startinguser);
		jaxWorkJob.setBatchJobStatus(status);
		jaxWorkJob.setParams(params);
		jaxWorkJob.setExecutionId(executionId);
		jaxWorkJob.setResultData(job.getResultData());
		jaxWorkJob.setRequestURI(job.getRequestURI());
		return jaxWorkJob;
	}

	@Nonnull
	private <T extends JaxAbstractDTO> T convertAbstractFieldsToJAX(@Nonnull final AbstractEntity abstEntity, @Nonnull final T jaxDTOToConvertTo) {
		jaxDTOToConvertTo.setTimestampErstellt(abstEntity.getTimestampErstellt());
		jaxDTOToConvertTo.setTimestampMutiert(abstEntity.getTimestampMutiert());
		jaxDTOToConvertTo.setId(checkNotNull(abstEntity.getId()));
		return jaxDTOToConvertTo;
	}
}

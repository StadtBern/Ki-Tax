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

package ch.dvbern.ebegu.api.dtos.batch;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import ch.dvbern.ebegu.api.dtos.JaxAbstractDTO;
import ch.dvbern.ebegu.enums.WorkJobType;
import ch.dvbern.ebegu.enums.reporting.BatchJobStatus;

import static com.google.common.base.Preconditions.checkNotNull;

@XmlRootElement(name = "batchJob")
public class JaxWorkJob extends JaxAbstractDTO {

	private static final long serialVersionUID = -9206171424688383096L;

	@Nonnull
	@NotNull
	private String name = "";

	private JaxBatchJobInformation execution;
	private WorkJobType workJobType;
	private String startinguser;
	private BatchJobStatus batchJobStatus;
	private String params;
	private Long executionId;
	private String resultData;
	private String requestURI;


	public JaxWorkJob(String name, JaxBatchJobInformation execution) {
		this.name = checkNotNull(name);
		this.execution = execution;
	}

	public JaxWorkJob() {
		// nop
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = checkNotNull(name);
	}

	public JaxBatchJobInformation getExecution() {
		return execution;
	}

	public void setExecution(JaxBatchJobInformation execution) {
		this.execution = execution;
	}

	public void setWorkJobType(WorkJobType workJobType) {
		this.workJobType = workJobType;
	}

	public WorkJobType getWorkJobType() {
		return workJobType;
	}

	public void setStartinguser(String startinguser) {
		this.startinguser = startinguser;
	}

	public String getStartinguser() {
		return startinguser;
	}

	public void setBatchJobStatus(BatchJobStatus batchJobStatus) {
		this.batchJobStatus = batchJobStatus;
	}

	public BatchJobStatus getBatchJobStatus() {
		return batchJobStatus;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getParams() {
		return params;
	}

	public void setExecutionId(Long executionId) {
		this.executionId = executionId;
	}

	public Long getExecutionId() {
		return executionId;
	}

	public void setResultData(String resultData) {
		this.resultData = resultData;
	}

	public String getResultData() {
		return resultData;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public String getRequestURI() {
		return requestURI;
	}
}

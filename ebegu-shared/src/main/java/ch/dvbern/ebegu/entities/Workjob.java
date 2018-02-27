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

package ch.dvbern.ebegu.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ch.dvbern.ebegu.enums.WorkJobType;
import ch.dvbern.ebegu.enums.reporting.BatchJobStatus;
import ch.dvbern.ebegu.util.Constants;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Entity that represents the Metadata of a job that may potentially  be further split into multiple workpackages. In our
 * case we use this to create reports
 */
@Entity
@NamedQuery(name = Workjob.Q_WORK_JOB_STATE_UPDATE, query = "update Workjob wj set wj.status = :status where executionId = :exId")
public class Workjob extends AbstractEntity {

	public static final String Q_WORK_JOB_STATE_UPDATE = "WORK_JOB_STATE_UPDATE";

	private static final long serialVersionUID = -1964772151498547196L;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private WorkJobType workJobType;

	@Lob
	private String metadata;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = true)
	private String resultData;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false, updatable = false)
	@NotNull
	private String startinguser;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String params;

	@Size(min = 1, max = Constants.DB_TEXTAREA_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String requestURI;

	@Column
	@Min(0)
	private Long executionId;

	@Column(length = 45, nullable = true, updatable = false)
	private String triggeringIp;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@NotNull
	private BatchJobStatus status;


	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public Long getExecutionId() {
		return executionId;
	}

	public void setExecutionId(Long executionId) {
		this.executionId = executionId;
	}

	public WorkJobType getWorkJobType() {
		return workJobType;
	}

	public void setWorkJobType(WorkJobType workJobType) {
		this.workJobType = workJobType;
	}

	public BatchJobStatus getStatus() {
		return status;
	}

	public void setStatus(BatchJobStatus status) {
		this.status = status;
	}

	@SuppressWarnings("ObjectEquality")
	@SuppressFBWarnings("BC_UNCONFIRMED_CAST")
	@Override
	public boolean isSame(AbstractEntity o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Workjob workjob = (Workjob) o;

		if (workJobType != workjob.getWorkJobType()) {
			return false;
		}

		if (!startinguser.equals(workjob.getStartinguser())) {
			return false;
		}

		if (!requestURI.equals(workjob.getRequestURI())) {
			return false;
		}
		return metadata != null ? metadata.equals(workjob.getMetadata()) : workjob.getMetadata() == null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		Workjob workjob = (Workjob) o;

		if (workJobType != workjob.workJobType) {
			return false;
		}
		if (metadata != null ? !metadata.equals(workjob.metadata) : workjob.metadata != null) {
			return false;
		}
		if (!startinguser.equals(workjob.startinguser)) {
			return false;
		}
		if (params != null ? !params.equals(workjob.params) : workjob.params != null) {
			return false;
		}
		return triggeringIp != null ? triggeringIp.equals(workjob.triggeringIp) : workjob.triggeringIp == null;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + workJobType.hashCode();
		result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
		result = 31 * result + startinguser.hashCode();
		result = 31 * result + (params != null ? params.hashCode() : 0);
		result = 31 * result + (triggeringIp != null ? triggeringIp.hashCode() : 0);
		return result;
	}

	public void setStartinguser(String startinguser) {
		this.startinguser = startinguser;
	}

	public String getStartinguser() {
		return startinguser;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getParams() {
		return params;
	}

	public void setTriggeringIp(String triggeringIp) {
		this.triggeringIp = triggeringIp;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public String getTriggeringIp() {
		return triggeringIp;
	}

	public String getResultData() {
		return resultData;
	}

	public void setResultData(String resultData) {
		this.resultData = resultData;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Workjob{");
		sb.append("workJobType=").append(workJobType);
		sb.append(", startinguser='").append(startinguser).append('\'');
		sb.append(", params='").append(params).append('\'');
		sb.append(", executionId=").append(executionId);
		sb.append(", triggeringIp='").append(triggeringIp).append('\'');
		sb.append(", status=").append(status);
		sb.append('}');
		return sb.toString();
	}
}

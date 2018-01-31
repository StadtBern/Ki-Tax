/*
 * Copyright © 2017 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.api.dtos.batch;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.lib.date.converters.LocalDateTimeXMLConverter;

@XmlRootElement(name = "batchJobInformation")
public class JaxBatchJobInformation implements Serializable {

	private static final long serialVersionUID = 8895184522508431765L;

	private long executionId;

	@NotNull
	@Nonnull
	private String jobName = "";

	@NotNull
	@Nonnull
	private String batchStatus = "";

	@Nullable
	private String exitStatus = "";

	@Nonnull
	@NotNull
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime createTime = LocalDateTime.now();

	@Nullable
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime endTime = null;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime startTime = null;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime lastUpdatedTime = null;

	@Nonnull
	public String getBatchStatus() {
		return batchStatus;
	}

	public void setBatchStatus(@Nonnull String batchStatus) {
		this.batchStatus = batchStatus;
	}

	public long getExecutionId() {
		return executionId;
	}

	public void setExecutionId(long executionId) {
		this.executionId = executionId;
	}

	@Nullable
	public String getExitStatus() {
		return exitStatus;
	}

	public void setExitStatus(@Nullable String exitStatus) {
		this.exitStatus = exitStatus;
	}

	@Nonnull
	public String getJobName() {
		return jobName;
	}

	public void setJobName(@Nonnull String jobName) {
		this.jobName = jobName;
	}

	@Nonnull
	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(@Nonnull LocalDateTime createTime) {
		this.createTime = createTime;
	}

	@Nullable
	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(@Nullable LocalDateTime endTime) {
		this.endTime = endTime;
	}

	@Nullable
	public LocalDateTime getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(@Nullable LocalDateTime lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	@Nullable
	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(@Nullable LocalDateTime startTime) {
		this.startTime = startTime;
	}
}

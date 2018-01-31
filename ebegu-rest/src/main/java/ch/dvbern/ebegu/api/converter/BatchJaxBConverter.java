/*
 * Copyright © 2017 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.api.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.batch.runtime.JobExecution;
import javax.enterprise.context.RequestScoped;

import ch.dvbern.ebegu.api.dtos.batch.JaxBatchJobInformation;

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
	public JaxBatchJobInformation batchJobInformationToResource(@Nonnull JobExecution information) {
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
}

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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import static com.google.common.base.Preconditions.checkNotNull;

@XmlRootElement(name = "batchJob")
public class JaxBatchJob {
	@Nonnull
	@NotNull
	private String name = "";

	@Nonnull
	private List<JaxBatchJobInformation> executions = new ArrayList<>();

	public JaxBatchJob(String name, List<JaxBatchJobInformation> executions) {
		this.name = checkNotNull(name);
		this.executions = checkNotNull(executions);
	}

	public JaxBatchJob() {
		// nop
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = checkNotNull(name);
	}

	@Nonnull
	public List<JaxBatchJobInformation> getExecutions() {
		return executions;
	}

	public void setExecutions(@Nonnull List<JaxBatchJobInformation> executions) {
		this.executions = checkNotNull(executions);
	}
}

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
import javax.xml.bind.annotation.XmlRootElement;

import static com.google.common.base.Preconditions.checkNotNull;

@XmlRootElement(name = "batchJobList")
public class JaxBatchJobList {

	@Nonnull
	private final List<JaxWorkJob> jobs;

	public JaxBatchJobList() {
		this(new ArrayList<>());
	}

	public JaxBatchJobList(@Nonnull List<JaxWorkJob> jobs) {
		this.jobs = checkNotNull(jobs);
	}

	@Nonnull
	public List<JaxWorkJob> getJobs() {
		return jobs;
	}
}

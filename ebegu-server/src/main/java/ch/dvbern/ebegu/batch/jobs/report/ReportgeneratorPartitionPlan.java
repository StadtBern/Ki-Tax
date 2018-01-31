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

import java.util.Properties;

import javax.batch.api.partition.PartitionPlanImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportgeneratorPartitionPlan extends PartitionPlanImpl {

	private static final Logger LOG = LoggerFactory.getLogger(ReportgeneratorPartitionPlan.class);
	private Properties jobProperties;

	public ReportgeneratorPartitionPlan(Properties jobProperties) {
		 //hier hat man die moeglichkeit den job in zu partitionieren um ihn parallel zu verarbeiten
		this.jobProperties = jobProperties;
	}


	@Override
	public int getPartitions() {
		LOG.debug("getPartitions");

		return 1;
	}

	@Override
	public int getThreads() {
		LOG.debug("getThreads");

		return 1;
	}

	@Override
	public Properties[] getPartitionProperties() {
		LOG.debug("getPartitionProperties");

		return jobProperties.stringPropertyNames().stream()
			.map(name -> {
				Properties props = new Properties();
				props.setProperty(name, jobProperties.getProperty(name));
				return props;
			}).toArray(Properties[]::new);
	}
}

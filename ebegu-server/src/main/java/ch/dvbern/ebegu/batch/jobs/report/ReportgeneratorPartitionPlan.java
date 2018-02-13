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

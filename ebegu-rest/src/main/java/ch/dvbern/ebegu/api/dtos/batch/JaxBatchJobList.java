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

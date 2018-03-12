/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
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

package ch.dvbern.ebegu.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.Workjob;
import ch.dvbern.ebegu.enums.reporting.BatchJobStatus;
import ch.dvbern.ebegu.enums.reporting.ReportVorlage;

/**
 * Service zum Verwalten von Workjobs
 */
public interface WorkjobService {

	@Nonnull
	Workjob saveWorkjob(@Nonnull Workjob workJob);

	@Nonnull
	Workjob findWorkjobByWorkjobID(@Nonnull String workJobId);

	@Nonnull
	Workjob findWorkjobByExecutionId(@Nonnull Long executionId);

	void removeOldWorkjobs();

	@Nonnull
	Workjob createNewReporting(@Nonnull Workjob workJob,
		@Nonnull ReportVorlage vorlage,
		@Nullable LocalDate datumVon,
		@Nullable LocalDate datumBis,
		@Nullable String gesuchPeriodIdParam);

	@Nonnull
	List<Workjob> findWorkjobs(@Nonnull String startingUserName, @Nonnull Set<BatchJobStatus> statesToSearch);

	@Nonnull
	List<Workjob> findUnfinishedWorkjobs();

	/**
	 * update query that changes state
	 * @param executionId
	 * @param status
	 */
	void changeStateOfWorkjob(long executionId,@Nonnull BatchJobStatus status);

	void addResultToWorkjob(@Nonnull String workjobID, @Nonnull String resultData);

	void removeWorkjob(Workjob workjob);
}

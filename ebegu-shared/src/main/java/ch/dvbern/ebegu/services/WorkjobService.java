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

//	Workjob saveWorkjob(Workjob workJob);


	Workjob findWorkjobByWorkjobID(String workJobId);

	Workjob findWorkjobByExecutionId(@Nonnull Long executionId);
	//
	//	void updateWorkPackage(@Nonnull String workJobId, int workPackageSeqNumber, @Nonnull Object[] row);
//

//	List<Workpackage> getUnfinishedWorkpackages(@Nonnull String workjobIdentifier);

	void removeOldWorkjobs();

	Workjob createNewReporting(@Nonnull Workjob workJob,
		@Nonnull ReportVorlage vorlage,
		@Nullable LocalDate datumVon,
		@Nullable LocalDate datumBis,
		@Nullable String gesuchPeriodIdParam);

	List<Workjob> findWorkjobs(String startingUserName, Set<BatchJobStatus> statesToSearch);

	/**
	 * update query that changes state
	 * @param executionId
	 * @param status
	 */
	void changeStateOfWorkjob(long executionId, BatchJobStatus status);
}

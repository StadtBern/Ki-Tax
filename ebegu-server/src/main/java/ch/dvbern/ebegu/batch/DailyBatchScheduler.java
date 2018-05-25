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

package ch.dvbern.ebegu.batch;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.security.RunAs;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.ebegu.services.DailyBatch;

@Singleton
@RunAs(UserRoleName.SUPER_ADMIN)
public class DailyBatchScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DailyBatchScheduler.class);

	@Inject
	private DailyBatch dailyBatch;

	@Schedule(second = "59", minute = "59", hour = "23", persistent = false)
	public void runBatchCleanDownloadFiles() {
		dailyBatch.runBatchCleanDownloadFiles();
	}

	@Schedule(second = "59", minute = "58", hour = "23", persistent = false)
	public void runBatchCleanWorkjobs() {
		dailyBatch.runBatchCleanWorkjobs();
	}

	@Schedule(second = "59", minute = "00", hour = "01", persistent = false)
	public void runBatchMahnungFristablauf() {
		Future<Boolean> booleanFuture = dailyBatch.runBatchMahnungFristablauf();
		try {
			Boolean resultat = booleanFuture.get();
			LOGGER.info("Batchjob MahnungFristablauf durchgefuehrt mit Resultat: {}", resultat);
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Batch-Job Mahnung Fristablauf konnte nicht durchgefuehrt werden!", e);
		}
	}

	@Schedule(second = "59", minute = "10", hour = "22", persistent = false)
	public void runBatchWarnungGesuchNichtFreigegeben() {
		dailyBatch.runBatchWarnungGesuchNichtFreigegeben();
	}

	@Schedule(second = "59", minute = "30", hour = "22", persistent = false)
	public void runBatchWarnungFreigabequittungFehlt() {
		dailyBatch.runBatchWarnungFreigabequittungFehlt();
	}

	@Schedule(second = "59", minute = "50", hour = "22", persistent = false)
	public void runBatchGesucheLoeschen() {
		dailyBatch.runBatchGesucheLoeschen();
	}

	@Schedule(second = "59", minute = "10", hour = "21", dayOfMonth = "1", month = "8", persistent = false)
	public void runBatchGesuchsperiodeLoeschen() {
		dailyBatch.runBatchGesuchsperiodeLoeschen();
	}

	@Schedule(second = "00", minute = "30", hour = "0", persistent = false)
	public void runBatchAbgelaufeneRollen() {
		dailyBatch.runBatchAbgelaufeneRollen();
	}

	@Schedule(second = "59", minute = "00", hour = "02", persistent = false)
	public void runBatchDeleteInvalidAuthTokens() {
		dailyBatch.runBatchDeleteInvalidAuthTokens();
	}
}

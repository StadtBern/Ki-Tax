package ch.dvbern.ebegu.batch;

import ch.dvbern.ebegu.services.DailyBatch;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Startup
@Singleton
public class DailyBatchScheduler {

	@Inject
	DailyBatch dailyBatch;

	@Schedule(second = "59", minute = "59", hour = "23", persistent = false)
	public void runBackgroundTasks() {
		dailyBatch.runBackgroundTasksAsync();
	}

}

package ch.dvbern.ebegu.batch;

import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.ebegu.services.DailyBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RunAs;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Singleton
@RunAs(value = UserRoleName.SUPER_ADMIN)
public class DailyBatchScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DailyBatchScheduler.class);

	@Inject
	DailyBatch dailyBatch;

	@Schedule(second = "59", minute = "59", hour = "23", persistent = false)
	public void runBatchCleanDownloadFiles() {
		dailyBatch.runBatchCleanDownloadFiles();
	}

	@Schedule(second = "0", minute = "*/15", hour = "*", persistent = false)
	public void runBatchMahnungFristablauf() {
		Future<Boolean> booleanFuture = dailyBatch.runBatchMahnungFristablauf();
		try {
			Boolean resultat = booleanFuture.get();
			LOGGER.info("Batchjob MahnungFristablauf durchgefuehrt mit Resultat: " + resultat);
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
}

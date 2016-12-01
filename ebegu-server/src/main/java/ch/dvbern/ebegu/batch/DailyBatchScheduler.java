package ch.dvbern.ebegu.batch;

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
@RunAs(value = "SUPER_ADMIN")
public class DailyBatchScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DailyBatchScheduler.class);

	@Inject
	DailyBatch dailyBatch;

	@Schedule(second = "59", minute = "59", hour = "23", persistent = false)
	public void runBatchCleanDownloadFiles() {
		dailyBatch.runBatchCleanDownloadFiles();
	}

	@Schedule(second = "0", minute = "*/2", hour = "*", persistent = false) // TODO (team) nur fuer Tests, spaeter 1x taeglich
	public void runBatchMahnungFristablauf() {
		Future<Boolean> booleanFuture = dailyBatch.runBatchMahnungFristablauf();
		try {
			Boolean resultat = booleanFuture.get();
			LOGGER.info("Batchjob MahnungFristablauf durchgefuehrt mit Resultat: " + resultat);
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Batch-Job Mahnung Fristablauf konnte nicht durchgefuehrt werden!", e);
		}
	}
}

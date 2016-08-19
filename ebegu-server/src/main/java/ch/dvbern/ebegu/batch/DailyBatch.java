package ch.dvbern.ebegu.batch;

import ch.dvbern.ebegu.services.TempDokumentService;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless

public class DailyBatch {

	@Inject
	TempDokumentService tempDokumentService;


	@Asynchronous
	void runBackgroundTasksAsync() {
		tempDokumentService.cleanUp();
	}

}

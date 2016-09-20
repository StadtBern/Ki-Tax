package ch.dvbern.ebegu.batch;

import ch.dvbern.ebegu.services.DownloadFileService;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class DailyBatch {

	@Inject
	DownloadFileService downloadFileService;


	@Asynchronous
	void runBackgroundTasksAsync() {
		downloadFileService.cleanUp();
	}

}

package ch.dvbern.ebegu.services;

import javax.ejb.Asynchronous;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Service fuer Dokument
 */
@Stateless
@Local(DailyBatch.class)
public class DailyBatchBean extends AbstractBaseService implements DailyBatch {


	private static final long serialVersionUID = -4627435482413298843L;

	@Inject
	DownloadFileService downloadFileService;


	@Override
	@Asynchronous
	public void runBackgroundTasksAsync() {
		downloadFileService.cleanUp();
	}
}

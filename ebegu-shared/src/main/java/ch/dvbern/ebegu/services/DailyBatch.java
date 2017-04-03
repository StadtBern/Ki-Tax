package ch.dvbern.ebegu.services;

import java.util.concurrent.Future;

public interface DailyBatch {


	void runBatchCleanDownloadFiles();

	Future<Boolean> runBatchMahnungFristablauf();

	void runBatchWarnungGesuchNichtFreigegeben();

	void runBatchWarnungFreigabequittungFehlt();

	void runBatchGesucheLoeschen() ;

}

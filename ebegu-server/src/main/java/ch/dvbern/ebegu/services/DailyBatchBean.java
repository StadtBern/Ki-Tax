package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ejb.*;
import javax.inject.Inject;
import java.util.concurrent.Future;

/**
 * Service fuer Batch-Jobs
 */
@PermitAll
@Stateless
@Local(DailyBatch.class)
public class DailyBatchBean implements DailyBatch {


	private static final long serialVersionUID = -4627435482413298843L;
	private static final Logger LOGGER = LoggerFactory.getLogger(DailyBatchBean.class);

	@Inject
	private DownloadFileService downloadFileService;

	@Inject
	private MahnungService mahnungService;

	@Inject
	private Persistence<AbstractEntity> persistence;

	@Inject
	private GesuchService gesuchService;


	@Override
	@Asynchronous
	public void runBatchCleanDownloadFiles() {
		try {
		    downloadFileService.cleanUp();
		} catch (RuntimeException e) {
			LOGGER.error("Batch-Job Cleanup Download-Files konnte nicht durchgefuehrt werden!", e);
		}
	}

	@Override
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Future<Boolean> runBatchMahnungFristablauf() {
		try {
			mahnungService.fristAblaufTimer();
			AsyncResult<Boolean> booleanAsyncResult = new AsyncResult<>(Boolean.TRUE);
			// Hier hat's evtl. einen Bug im Wildfly, koennte aber auch korrekt sein:
			// Ohne dieses explizite Flush wird der EM erst so spaet geflusht,
			// das der Request-Scope nicht mehr aktiv ist und somit das @RequestScoped PrincipalBean fuer die validierung
			// vom Mandant nicht mehr zur Verfuegung steht.
			persistence.getEntityManager().flush();
			return booleanAsyncResult;
		} catch (RuntimeException e) {
			LOGGER.error("Batch-Job Fristablauf konnte nicht durchgefuehrt werden!", e);
			return new AsyncResult<>(Boolean.FALSE);
		}
	}

	@Override
	@Asynchronous
	public void runBatchWarnungGesuchNichtFreigegeben() {
		try {
			final int anzahl = gesuchService.warnGesuchNichtFreigegeben();
			LOGGER.info("Es wurden " + anzahl + " Gesuche gefunden, die noch nicht freigegeben wurden");
		} catch (RuntimeException e) {
			LOGGER.error("Batch-Job WarnungGesuchNichtFreigegeben konnte nicht durchgefuehrt werden!", e);
		}
	}

	@Override
	@Asynchronous
	public void runBatchWarnungFreigabequittungFehlt() {
		try {
			final int anzahl = gesuchService.warnFreigabequittungFehlt();
			LOGGER.info("Es wurden " + anzahl + " Gesuche gefunden, bei denen die Freigabequittung fehlt");
		} catch (RuntimeException e) {
			LOGGER.error("Batch-Job WarnungFreigabequittungFehlt konnte nicht durchgefuehrt werden!", e);
		}
	}

	@Override
	@Asynchronous
	public void runBatchGesucheLoeschen() {
		try {
			final int anzahl = gesuchService.deleteGesucheOhneFreigabeOderQuittung();
			LOGGER.info("Es wurden " + anzahl + " Gesuche ohne Freigabe oder Quittung gefunden, die geloescht werden muessen");
		} catch (RuntimeException e) {
			LOGGER.error("Batch-Job GesucheLoeschen konnte nicht durchgefuehrt werden!", e);
		}
	}
}

package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ejb.*;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Service fuer Batch-Jobs.
 * All services must be called in its own transaction. The reason is that the EntityManager seems to flush the
 * queries after the method is finished, which means that the Context no longer exists and the Principalbean cannot
 * be found, this implies a rollback of the Transaction and everything gets undone. Executing the service within a
 * transaction flushes the queries before the method finishes and the context still exists.
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
	private Persistence persistence;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private MailService mailService;


	@Inject
	private GesuchsperiodeService gesuchsperiodeService;


	@Override
	@Asynchronous
	public void runBatchCleanDownloadFiles() {
		try {
			LOGGER.info("Starting Job Cleanup Download-Files...");
		    downloadFileService.cleanUp();
			LOGGER.info("... Job Cleanup Download-Files finished");
		} catch (RuntimeException e) {
			LOGGER.error("Batch-Job Cleanup Download-Files konnte nicht durchgefuehrt werden!", e);
		}
	}

	@Override
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Future<Boolean> runBatchMahnungFristablauf() {
		try {
			LOGGER.info("Starting Job Fristablauf...");
			mahnungService.fristAblaufTimer();
			AsyncResult<Boolean> booleanAsyncResult = new AsyncResult<>(Boolean.TRUE);
			// Hier hat's evtl. einen Bug im Wildfly, koennte aber auch korrekt sein:
			// Ohne dieses explizite Flush wird der EM erst so spaet geflusht,
			// das der Request-Scope nicht mehr aktiv ist und somit das @RequestScoped PrincipalBean fuer die validierung
			// vom Mandant nicht mehr zur Verfuegung steht.
			persistence.getEntityManager().flush();
			LOGGER.info("... Job Fristablauf finished");
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
			LOGGER.info("Starting Job WarnungGesuchNichtFreigegeben...");
			final int anzahl = gesuchService.warnGesuchNichtFreigegeben();
			LOGGER.info("Es wurden " + anzahl + " Gesuche gefunden, die noch nicht freigegeben wurden");
			LOGGER.info("... Job WarnungGesuchNichtFreigegeben finished");
		} catch (RuntimeException e) {
			LOGGER.error("Batch-Job WarnungGesuchNichtFreigegeben konnte nicht durchgefuehrt werden!", e);
		}
	}

	@Override
	@Asynchronous
	public void runBatchWarnungFreigabequittungFehlt() {
		try {
			LOGGER.info("Starting Job WarnungFreigabequittungFehlt...");
			final int anzahl = gesuchService.warnFreigabequittungFehlt();
			LOGGER.info("Es wurden " + anzahl + " Gesuche gefunden, bei denen die Freigabequittung fehlt");
			LOGGER.info("... Job WarnungFreigabequittungFehlt finished");
		} catch (RuntimeException e) {
			LOGGER.error("Batch-Job WarnungFreigabequittungFehlt konnte nicht durchgefuehrt werden!", e);
		}
	}

	@Override
	@Asynchronous
	public void runBatchGesucheLoeschen() {
		try {
			LOGGER.info("Starting Job GesucheLoeschen...");
			List<Betreuung> betreuungen = new ArrayList<>();
			List<Gesuch> gesuche = gesuchService.getGesuchesOhneFreigabeOderQuittung();
			for (Gesuch gesuch : gesuche) {
				betreuungen.addAll(gesuch.extractAllBetreuungen());
			}
			final int anzahl = gesuchService.deleteGesucheOhneFreigabeOderQuittung();
			mailService.sendInfoBetreuungGeloescht(betreuungen);
			LOGGER.info("Es wurden " + anzahl + " Gesuche ohne Freigabe oder Quittung gefunden, die geloescht werden muessen");
			LOGGER.info("... Job GesucheLoeschen finished");
		} catch (RuntimeException e) {
			LOGGER.error("Batch-Job GesucheLoeschen konnte nicht durchgefuehrt werden!", e);
		}
	}

	@Override
	public void runBatchGesuchsperiodeLoeschen() {
		try {
			LOGGER.info("Starting Job GesuchsperiodeLoeschen...");
			LocalDate stichtag = LocalDate.now().minusYears(10);
			LOGGER.info("Deleting Gesuchsperioden older than " + Constants.DATE_FORMATTER.format(stichtag));
			Collection<Gesuchsperiode> gesuchsperiodenBetween = gesuchsperiodeService.getGesuchsperiodenBetween(LocalDate.of(1900, Month.JANUARY, 1), stichtag);
			for (Gesuchsperiode gesuchsperiode : gesuchsperiodenBetween) {
				gesuchsperiodeService.removeGesuchsperiode(gesuchsperiode.getId());
			}
			LOGGER.info("... Job GesuchsperiodeLoeschen finished");
		} catch (RuntimeException e) {
			LOGGER.error("Batch-Job GesuchsperiodeLoeschen konnte nicht durchgefuehrt werden!", e);
		}
	}
}

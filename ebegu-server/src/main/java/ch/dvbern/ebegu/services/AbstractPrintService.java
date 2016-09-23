package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.EbeguVorlage;
import ch.dvbern.ebegu.enums.EbeguVorlageKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Abstract Service class, die von allen PrintServices erweitert werden muss. Sie enthaelt Methoden, um die Vorlagen zu laden
 */
public class AbstractPrintService extends AbstractBaseService {

	private final Logger LOG = LoggerFactory.getLogger(GesuchServiceBean.class.getSimpleName());

	@Inject
	private EbeguVorlageService ebeguVorlageService;

	// We need to pass to EbeguVorlageService a new EntityManager to avoid errors like ConcurrentModificatinoException. So we create it here
	// and pass it to the methods of EbeguVorlageService we need to call.
	//http://stackoverflow.com/questions/18267269/correct-way-to-do-an-entitymanager-query-during-hibernate-validation
	@PersistenceUnit(unitName = "ebeguPersistenceUnit")
	private EntityManagerFactory entityManagerFactory;

	/**
	 * Sucht nach der richtigen Vorlage in den Parameters. Wenn der Parameter existiert, wird die Vorlage geladen.
	 * Sollte der Parameter nicht existieren, wird das drfault-template geladen
	 * @return
	 */
	@Nonnull
	protected InputStream getVorlageStream(LocalDate dateAb, LocalDate dateBis, EbeguVorlageKey vorlageKey, String defaultVorlagePath) {
		final Optional<EbeguVorlage> vorlage = ebeguVorlageService.getEbeguVorlageByDatesAndKey(dateAb, dateBis,
			vorlageKey, createEntityManager());
		if (vorlage.isPresent() && vorlage.get().getVorlage() != null) {
			try {
				return new FileInputStream(vorlage.get().getVorlage().getFilepfad());
			} catch (final FileNotFoundException e) {
				// Wenn die Datei nicht gefunden wird, die Exception Message wird gelogt und das default-template geladen
				LOG.error("Die Datei mit der Vorlage fuer " + vorlageKey + " wurde nicht gefunden. Die default Vorlage ("
					+ defaultVorlagePath + ") wird stattdessen benutzt");
			}
		}
		return this.getClass().getResourceAsStream(defaultVorlagePath);
	}

	private EntityManager createEntityManager() {
		if (entityManagerFactory != null) {
			return  entityManagerFactory.createEntityManager(); // creates a new EntityManager
		}
		return null;
	}

}

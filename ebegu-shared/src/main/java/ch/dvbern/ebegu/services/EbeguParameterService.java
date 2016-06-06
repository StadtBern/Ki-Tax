package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.EbeguParameterKey;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;


/**
 * Service zum Verwalten von zeitabhängigen E-BEGU-Parametern.
 */
public interface EbeguParameterService {

	/**
	 * Speichert den Parameter neu in der DB falls der Key noch nicht existiert. Ansonsten wird ein neuer Parameter mit
	 * diesem Key erstellt
	 */
	@Nonnull
	EbeguParameter saveEbeguParameter(@Nonnull EbeguParameter ebeguParameter);

	/**
	 * Gibt den Parameter mit diesem Key zurück oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<EbeguParameter> findEbeguParameter(@Nonnull String id);

	/**
	 * Gibt alle Parameter zurück
	 */
	@Nonnull
	Collection<EbeguParameter> getAllEbeguParameter();

	/**
	 * Entfernt einen Parameter aus der Datenbank
	 */
	void removeEbeguParameter(@Nonnull String id);

	/**
	 * Sucht alle am Stichtag gueltigen Ebegu-Parameter
	 */
	@Nonnull
	Collection<EbeguParameter> getAllEbeguParameterByDate(@Nonnull LocalDate date);

	/**
	 * Sucht alle für die Gesuchsperiode gueltigen Ebegu-Parameter
	 */
	@Nonnull
	Collection<EbeguParameter> getEbeguParameterByGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode);

	/**
	 * Sucht alle für die Gesuchsperiode gueltigen Ebegu-Parameter
	 */
	@Nonnull
	Collection<EbeguParameter> getEbeguParameterByJahr(@Nonnull Integer jahr);

	/**
	 * Sucht den am Stichtag gueltigen Ebegu-Parameter mit dem übergebenen Key.
	 */
	@Nonnull
	Optional<EbeguParameter> getEbeguParameterByKeyAndDate(@Nonnull EbeguParameterKey key, @Nonnull LocalDate date);
}

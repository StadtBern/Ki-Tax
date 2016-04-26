package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Fall;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Fallen
 */
public interface FallService {

	/**
	 * Erstellt einen neuen Fall in der DB, falls der key noch nicht existiert
	 * @param fall der Fall als DTO
	 * @return den gespeicherten Fall
	 */
	@Nonnull
	Fall createFall(@Nonnull Fall fall);

	/**
	 * Aktualisiert den Fall in der DB
	 * @param fall der Fall als DTO
	 * @return Den aktualisierten Fall
	 */
	@Nonnull
	Fall updateFall(@Nonnull Fall fall);

	/**
	 *
	 * @param key PK (id) des Falles
	 * @return Fall mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Fall> findFall(@Nonnull String key);

	/**
	 * Gibt alle existierenden Faelle zurueck.
	 * @return Liste aller Faelle aus der DB
	 */
	@Nonnull
	Collection<Fall> getAllFalle();

	/**
	 * entfernt einen Fall aus der Database
	 * @param fall der Fall als DTO
	 */
	@Nonnull
	void removeFall(@Nonnull Fall fall);

}

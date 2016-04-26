package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Traegerschaft;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Traegerschaften
 */
public interface TraegerschaftService {

	/**
	 * Speichert die Traegerschaft neu in der DB falls der Key noch nicht existiert.
	 * @param person Die Traegerschaft als DTO
	 */
	@Nonnull
	Traegerschaft saveTraegerschaft(@Nonnull Traegerschaft person);

	/**

	 * @param traegerschaftId PK (id) der Traegerschaft
	 * @return Traegerschaft mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Traegerschaft> findTraegerschaft(@Nonnull String traegerschaftId);

	/**
	 *
	 * @return Liste aller Traegerschaften aus der DB
	 */
	@Nonnull
	Collection<Traegerschaft> getAllTraegerschaften();

	/**
	 * removes a Traegerschaft from the Databse
	 * @param traegerschaftId
	 */
	void removeTraegerschaft(@Nonnull String traegerschaftId);
}

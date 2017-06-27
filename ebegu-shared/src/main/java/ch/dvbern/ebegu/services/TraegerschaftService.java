package ch.dvbern.ebegu.services;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Traegerschaft;

/**
 * Service zum Verwalten von Traegerschaften
 */
public interface TraegerschaftService {

	/**
	 * Speichert die Traegerschaft neu in der DB falls der Key noch nicht existiert.
	 * @param traegerschaft Die Traegerschaft als DTO
	 */
	@Nonnull
	Traegerschaft saveTraegerschaft(@Nonnull Traegerschaft traegerschaft);

	/**
	 * @param traegerschaftId PK (id) der Traegerschaft
	 * @return Traegerschaft mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Traegerschaft> findTraegerschaft(@Nonnull String traegerschaftId);

	/**
	 * @return Liste aller Traegerschaften aus der DB
	 */
	@Nonnull
	Collection<Traegerschaft> getAllTraegerschaften();

	/**
	 * @return Liste aller aktiven Traegerschaften aud der DB
	 */
	@Nonnull
	Collection<Traegerschaft> getAllActiveTraegerschaften();

	/**
	 * removes a Traegerschaft from the Databse
	 */
	void removeTraegerschaft(@Nonnull String traegerschaftId);

	/**
	 * marks an Traegerschft as inactive on the Database.
	 */
	void setInactive(@Nonnull String traegerschaftId);
}

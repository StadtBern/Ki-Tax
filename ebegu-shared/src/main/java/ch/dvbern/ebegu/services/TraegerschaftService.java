package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Traegerschaft;

import javax.annotation.Nonnull;
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
	Traegerschaft createTraegerschaft(@Nonnull Traegerschaft person);

	/**

	 * @param key PK (id) der Traegerschaft
	 * @return Traegerschaft mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Traegerschaft> findTraegerschaft(@Nonnull String key);
}

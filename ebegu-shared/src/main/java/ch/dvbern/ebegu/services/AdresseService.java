package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Adresse;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Adresen
 */
public interface AdresseService {

	/**
	 * Speichert die Adresse neu in der DB falls der Key noch nicht existiert.
	 * @param adresse Die Adresse als DTO
	 */
	@Nonnull
	Adresse createAdresse(@Nonnull Adresse adresse);

	/**
	 * Aktualisiert die Adresse in der DB.
	 * @param adresse Die Adresse als DTO
	 */
	@Nonnull
	Adresse updateAdresse(@Nonnull Adresse adresse);

	/**

	 * @param key PK (id) der Adresse
	 * @return Adresse mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Adresse> findAdresse(@Nonnull String key);

	/**
	 *
	 * @return Liste aller Adressen aus der DB
	 */
	@Nonnull
	Collection<Adresse> getAllAdressen();

	/**
	 * entfernt eine Adresse aus der Databse
	 * @param key PK (id) der Adresse
	 */
	void removeAdresse(@Nonnull String key);

}

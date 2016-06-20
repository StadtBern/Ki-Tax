package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Benutzer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service fuer die Verwaltung von Benutzern
 */
public interface BenutzerService {

	/**
	 * Aktualisiert den Benutzer in der DB or erstellt ihn wenn er noch nicht existiert
	 * @param benutzer die Benutzer als DTO
	 * @return Die aktualisierte Benutzer
	 */
	@Nonnull
	Benutzer saveBenutzer(@Nonnull Benutzer benutzer);

	/**
	 *
	 * @param username PK (id) des Benutzers
	 * @return Benutzer mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Benutzer> findBenutzer(@Nonnull String username);

	/**
	 * Gibt alle existierenden Benutzer zurueck.
	 * @return Liste aller Benutzern aus der DB
	 */
	@Nonnull
	Collection<Benutzer> getAllBenutzern();

	/**
	 * entfernt die Benutzer aus der Database
	 * @param username die Benutzer als DTO
	 */
	void removeBenutzer(@Nonnull String username);

}

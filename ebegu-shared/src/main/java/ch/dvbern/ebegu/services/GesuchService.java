package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuch;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Gesuche
 */
public interface GesuchService {

	/**
	 * Erstellt ein neues Gesuch in der DB, falls der key noch nicht existiert
	 * @param gesuch der Gesuch als DTO
	 * @return das gespeicherte Gesuch
	 */
	@Nonnull
	Gesuch createGesuch(@Nonnull Gesuch gesuch);

	/**
	 * Aktualisiert das Gesuch in der DB
	 * @param gesuch das Gesuch als DTO
	 * @return Das aktualisierte Gesuch
	 */
	@Nonnull
	Gesuch updateGesuch(@Nonnull Gesuch gesuch);

	/**
	 *
	 * @param key PK (id) des Gesuches
	 * @return Gesuch mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Gesuch> findGesuch(@Nonnull String key);

	/**
	 * Gibt alle existierenden Gesuche zurueck.
	 * @return Liste aller Gesuche aus der DB
	 */
	@Nonnull
	Collection<Gesuch> getAllGesuche();

	/**
	 * Gibt alle existierenden Gesuche zurueck, deren Status nicht VERFUEGT ist
	 * @return Liste aller Gesuche aus der DB
	 */
	@Nonnull
	Collection<Gesuch> getAllActiveGesuche();

	/**
	 * entfernt ein Gesuch aus der Database
	 * @param gesuch der Gesuch zu entfernen
	 */
	@Nonnull
	void removeGesuch(@Nonnull Gesuch gesuch);

}

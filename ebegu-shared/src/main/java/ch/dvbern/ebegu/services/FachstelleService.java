package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Fachstelle;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Fachstellen
 */
public interface FachstelleService {

	/**
	 * Erstellt eine neue Fachstelle in der DB, falls der key noch nicht existiert
	 * @param fachstelle die Fachstelle als DTO
	 * @return die gespeicherte Fachstelle
	 */
	@Nonnull
	Fachstelle createFachstelle(@Nonnull Fachstelle fachstelle);

	/**
	 * Aktualisiert die Fachstelle in der DB
	 * @param fachstelle die Fachstelle als DTO
	 * @return Die aktualisierte Fachstelle
	 */
	@Nonnull
	Fachstelle updateFachstelle(@Nonnull Fachstelle fachstelle);

	/**
	 *
	 * @param key PK (id) der Fachstelle
	 * @return Fachstelle mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Fachstelle> findFachstelle(@Nonnull String key);

	/**
	 * Gibt alle existierenden Fachstellen zurueck.
	 * @return Liste aller Fachstellen aus der DB
	 */
	@Nonnull
	Collection<Fachstelle> getAllFachstellen();

	/**
	 * entfernt die Fachstelle aus der Database
	 * @param fachstelle die Fachstelle als DTO
	 */
	void removeFachstelle(@Nonnull Fachstelle fachstelle);

}

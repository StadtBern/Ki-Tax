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
	 * Aktualisiert die Fachstelle in der DB
	 * @param fachstelle die Fachstelle als DTO
	 * @return Die aktualisierte Fachstelle
	 */
	@Nonnull
	Fachstelle saveFachstelle(@Nonnull Fachstelle fachstelle);

	/**
	 *
	 * @param fachstelleId PK (id) der Fachstelle
	 * @return Fachstelle mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Fachstelle> findFachstelle(@Nonnull String fachstelleId);

	/**
	 * Gibt alle existierenden Fachstellen zurueck.
	 * @return Liste aller Fachstellen aus der DB
	 */
	@Nonnull
	Collection<Fachstelle> getAllFachstellen();

	/**
	 * entfernt die Fachstelle aus der Database
	 * @param fachstelleId die Fachstelle als DTO
	 */
	void removeFachstelle(@Nonnull String fachstelleId);

}

package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.PensumFachstelle;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Service zum Verwalten von PensumFachstellen
 */
public interface PensumFachstelleService {

	/**
	 * Aktualisiert die PensumFachstelle in der DB
	 * @param pensumFachstelle die PensumFachstelle als DTO
	 * @return Die aktualisierte PensumFachstelle
	 */
	@Nonnull
	PensumFachstelle savePensumFachstelle(@Nonnull PensumFachstelle pensumFachstelle);

	/**
	 *
	 * @param pensumFachstelleId PK (id) der PensumFachstelle
	 * @return PensumFachstelle mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<PensumFachstelle> findPensumFachstelle(@Nonnull String pensumFachstelleId);

	/**
	 * entfernt die PensumFachstelle aus der Database
	 * @param pensumFachstelleId die Fachstelle als DTO
	 */
	void removePensumFachstelle(@Nonnull String pensumFachstelleId);

}

package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Familiensituation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Familiensituation
 */
public interface FamiliensituationService {

	/**
	 * Aktualisiert idn Familiensituation in der DB oder erstellt sie wenn sie noch nicht existiert
	 * @param familiensituation die Familiensituation als DTO
	 * @return Die aktualisierte Familiensituation
	 */
	@Nonnull
	Familiensituation saveFamiliensituation(@Nonnull Familiensituation familiensituation);

	/**
	 *
	 * @param key PK (id) der Familiensituation
	 * @return Familiensituation mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Familiensituation> findFamiliensituation(@Nonnull String key);

	/**
	 * Gibt alle existierenden Familiensituationen zurueck.
	 * @return Liste aller Familiensituationen aus der DB
	 */
	@Nonnull
	Collection<Familiensituation> getAllFamiliensituatione();

	/**
	 * entfernt eine Familiensituation aus der Database
	 * @param familiensituation die Familiensituation als DTO
	 */
	@Nonnull
	void removeFamiliensituation(@Nonnull Familiensituation familiensituation);

}

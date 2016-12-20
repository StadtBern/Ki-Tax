package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.FamiliensituationContainer;
import ch.dvbern.ebegu.entities.Gesuch;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Familiensituation
 */
public interface FamiliensituationService {

	/**
	 * Aktualisiert idn Familiensituation in der DB oder erstellt sie wenn sie noch nicht existiert
	 */
	FamiliensituationContainer saveFamiliensituation(Gesuch gesuch, FamiliensituationContainer familiensituationContainer, Familiensituation loadedFamiliensituation);

	/**
	 * @param key PK (id) der Familiensituation
	 * @return Familiensituation mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<FamiliensituationContainer> findFamiliensituation(@Nonnull String key);

	/**
	 * Gibt alle existierenden Familiensituationen zurueck.
	 *
	 * @return Liste aller Familiensituationen aus der DB
	 */
	@Nonnull
	Collection<FamiliensituationContainer> getAllFamiliensituatione();

	/**
	 * entfernt eine Familiensituation aus der Database
	 *
	 * @param familiensituation die Familiensituation als DTO
	 */
	void removeFamiliensituation(@Nonnull FamiliensituationContainer familiensituation);

}

package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.FinanzielleSituationContainer;
import ch.dvbern.ebegu.entities.Gesuchsteller;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Finanziellen Situationen
 */
public interface FinanzielleSituationService {

	/**
	 * Speichert die FinanzielleSituation neu in der DB falls der Key noch nicht existiert.
	 * @param finanzielleSituation Die FinanzielleSituation als DTO
	 */
	@Nonnull
	FinanzielleSituationContainer saveFinanzielleSituation(@Nonnull FinanzielleSituationContainer finanzielleSituation);

	/**
	 * @param key PK (id) der FinanzielleSituation
	 * @return FinanzielleSituation mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<FinanzielleSituationContainer> findFinanzielleSituation(@Nonnull String key);

	/**
	 * @return Liste aller FinanzielleSituationContainer aus der DB
	 */
	@Nonnull
	Collection<FinanzielleSituationContainer> getAllFinanzielleSituationen();

	/**
	 * entfernt eine FinanzielleSituation aus der Databse
	 * @param finanzielleSituation FinanzielleSituation zu entfernen
	 */
	void removeFinanzielleSituation(@Nonnull FinanzielleSituationContainer finanzielleSituation);

}

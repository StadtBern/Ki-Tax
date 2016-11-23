package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.FinanzielleSituationContainer;
import ch.dvbern.ebegu.entities.Gesuch;

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
	 * @param gesuchId
	 */
	@Nonnull
	FinanzielleSituationContainer saveFinanzielleSituation(@Nonnull FinanzielleSituationContainer finanzielleSituation, String gesuchId);

	/**
	 * @param id PK (id) der FinanzielleSituation
	 * @return FinanzielleSituation mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<FinanzielleSituationContainer> findFinanzielleSituation(@Nonnull String id);

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

	/**
	 * Berechnet die Finanzielle Situation beider Gesuchsteller
     */
	@Nonnull
	FinanzielleSituationResultateDTO calculateResultate(@Nonnull Gesuch gesuch);

	/**
	 * Berechnet die Finanzdaten für die Verfügung, d.h. inklusive allfälliger Einkommensverschlechterungen
	 * Das Resultat wird direkt dem Gesuch angehängt
     */
	void calculateFinanzDaten(@Nonnull Gesuch gesuch);
}

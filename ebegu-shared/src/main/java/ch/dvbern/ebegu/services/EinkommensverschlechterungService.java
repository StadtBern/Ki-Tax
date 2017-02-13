package ch.dvbern.ebegu.services;


import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;
import ch.dvbern.ebegu.entities.Gesuch;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von EinkommensverschlechterungContainerService
 */
public interface EinkommensverschlechterungService {

	/**
	 * Aktualisiert idn EinkommensverschlechterungContainer in der DB
	 *
	 * @param einkommensverschlechterungContainer die EinkommensverschlechterungContainer als DTO
	 * @param gesuchId
	 * @return Die aktualisierte EinkommensverschlechterungContainer
	 */
	@Nonnull
	EinkommensverschlechterungContainer saveEinkommensverschlechterungContainer(@Nonnull EinkommensverschlechterungContainer einkommensverschlechterungContainer, String gesuchId);

	/**
	 * @param key PK (id) der EinkommensverschlechterungContainer
	 * @return EinkommensverschlechterungContainer mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<EinkommensverschlechterungContainer> findEinkommensverschlechterungContainer(@Nonnull String key);

	/**
	 * Gibt alle existierenden EinkommensverschlechterungContainer zurueck.
	 *
	 * @return Liste aller EinkommensverschlechterungContainer aus der DB
	 */
	@Nonnull
	Collection<EinkommensverschlechterungContainer> getAllEinkommensverschlechterungContainer();

	/**
	 * entfernt eine EinkommensverschlechterungContaine aus der Database
	 *
	 * @param einkommensverschlechterungContainer die EinkommensverschlechterungContainer als DTO
	 */
	void removeEinkommensverschlechterungContainer(@Nonnull EinkommensverschlechterungContainer einkommensverschlechterungContainer);

	/**
	 * Berechnet die Einkomensverschlechterung beider Gesuchsteller für das entsprechende BasisJahr 1 oder 2
	 */
	@Nonnull
	FinanzielleSituationResultateDTO calculateResultate(@Nonnull Gesuch gesuch, int basisJahrPlus);
}

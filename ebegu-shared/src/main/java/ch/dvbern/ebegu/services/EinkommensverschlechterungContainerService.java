package ch.dvbern.ebegu.services;


import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von EinkommensverschlechterungContainerService
 */
public interface EinkommensverschlechterungContainerService {

	/**
	 * Aktualisiert idn EinkommensverschlechterungContainer in der DB
	 *
	 * @param einkommensverschlechterungContainer die EinkommensverschlechterungContainer als DTO
	 * @return Die aktualisierte EinkommensverschlechterungContainer
	 */
	@Nonnull
	EinkommensverschlechterungContainer saveEinkommensverschlechterungContainer(@Nonnull EinkommensverschlechterungContainer einkommensverschlechterungContainer);

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

}

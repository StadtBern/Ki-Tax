package ch.dvbern.ebegu.services;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.entities.Gesuch;

/**
 * Service zum Verwalten von EinkommensverschlechterungInfoContainer
 */
public interface EinkommensverschlechterungInfoService {

	/**
	 * Erstellt eine neue EinkommensverschlechterungInfoContainer in der DB, falls der key noch nicht existiert
	 *
	 * @param einkommensverschlechterungInfo die EinkommensverschlechterungInfoContainer als DTO
	 * @return die gespeicherte EinkommensverschlechterungInfoContainer
	 */
	@Nonnull
	Optional<EinkommensverschlechterungInfoContainer> createEinkommensverschlechterungInfo(
		@Nonnull EinkommensverschlechterungInfoContainer einkommensverschlechterungInfo);

	/**
	 * Aktualisiert idn EinkommensverschlechterungInfoContainer in der DB
	 *
	 * @param einkommensverschlechterungInfo die EinkommensverschlechterungInfoContainer als DTO
	 * @return Die aktualisierte EinkommensverschlechterungInfoContainer
	 */
	@Nonnull
	EinkommensverschlechterungInfoContainer updateEinkommensverschlechterungInfo(
		@Nonnull EinkommensverschlechterungInfoContainer einkommensverschlechterungInfo);

	/**
	 * Aktualisiert idn EinkommensverschlechterungInfoContainer in der DB
	 */
	@Nonnull
	EinkommensverschlechterungInfoContainer updateEinkommensVerschlechterungInfoAndGesuch(Gesuch gesuch, EinkommensverschlechterungInfoContainer oldEVData,
																				 EinkommensverschlechterungInfoContainer convertedEkvi);

	/**
	 * @param key PK (id) der EinkommensverschlechterungInfoContainer
	 * @return EinkommensverschlechterungInfoContainer mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<EinkommensverschlechterungInfoContainer> findEinkommensverschlechterungInfo(@Nonnull String key);

	/**
	 * Gibt alle existierenden EinkommensverschlechterungInfoen zurueck.
	 * @return Liste aller EinkommensverschlechterungInfoen aus der DB
	 */
	@Nonnull
	Collection<EinkommensverschlechterungInfoContainer> getAllEinkommensverschlechterungInfo();

	/**
	 * entfernt eine EinkommensverschlechterungInfoContainer aus der Database
	 * @param einkommensverschlechterungInfo die EinkommensverschlechterungInfoContainer als DTO
	 */
	void removeEinkommensverschlechterungInfo(@Nonnull EinkommensverschlechterungInfoContainer einkommensverschlechterungInfo);

}

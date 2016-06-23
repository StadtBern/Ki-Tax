package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfo;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von EinkommensverschlechterungInfo
 */
public interface EinkommensverschlechterungInfoService {

	/**
	 * Erstellt eine neue EinkommensverschlechterungInfo in der DB, falls der key noch nicht existiert
	 *
	 * @param einkommensverschlechterungInfo die EinkommensverschlechterungInfo als DTO
	 * @return die gespeicherte EinkommensverschlechterungInfo
	 */
	@Nonnull
	Optional<EinkommensverschlechterungInfo> createEinkommensverschlechterungInfo(@Nonnull EinkommensverschlechterungInfo einkommensverschlechterungInfo);

	/**
	 * Aktualisiert idn EinkommensverschlechterungInfo in der DB
	 *
	 * @param einkommensverschlechterungInfo die EinkommensverschlechterungInfo als DTO
	 * @return Die aktualisierte EinkommensverschlechterungInfo
	 */
	@Nonnull
	EinkommensverschlechterungInfo updateEinkommensverschlechterungInfo(@Nonnull EinkommensverschlechterungInfo einkommensverschlechterungInfo);

	/**
	 * @param key PK (id) der EinkommensverschlechterungInfo
	 * @return EinkommensverschlechterungInfo mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<EinkommensverschlechterungInfo> findEinkommensverschlechterungInfo(@Nonnull String key);

	/**
	 * Gibt alle existierenden EinkommensverschlechterungInfoen zurueck.
	 *
	 * @return Liste aller EinkommensverschlechterungInfoen aus der DB
	 */
	@Nonnull
	Collection<EinkommensverschlechterungInfo> getAllEinkommensverschlechterungInfo();

	/**
	 * entfernt eine EinkommensverschlechterungInfo aus der Database
	 *
	 * @param einkommensverschlechterungInfo die EinkommensverschlechterungInfo als DTO
	 */
	void removeEinkommensverschlechterungInfo(@Nonnull EinkommensverschlechterungInfo einkommensverschlechterungInfo);

}

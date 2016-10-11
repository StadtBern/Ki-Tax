package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Mutationsdaten;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Service zum Verwalten von Mutationsdaten
 */
public interface MutationsdatenService {

	/**
	 * @param key PK (id) der Mutationdaten
	 * @return Mutationsdaten mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Mutationsdaten> findMutationsdaten(@Nonnull String key);

}

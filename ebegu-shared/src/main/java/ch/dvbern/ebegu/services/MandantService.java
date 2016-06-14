package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Mandant;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Created by imanol on 25.04.16.
 */
public interface MandantService {

	/**
	 * @param id PK (id) des Mandanten
	 * @return Mandant mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Mandant> findMandant(@Nonnull final String id);

	@Nonnull
	Mandant getFirst();
}

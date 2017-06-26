package ch.dvbern.ebegu.services;

import java.util.Optional;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Mandant;

/**
 * Service fuer Mandant
 */
public interface MandantService {

	/**
	 * @param id PK (id) des Mandanten
	 * @return Mandant mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Mandant> findMandant(@Nonnull final String id);

	/**
	 * Gibt den ersten (und aktuell einzigen) Mandanten aus der DB zurueck
	 */
	@Nonnull
	Mandant getFirst();
}

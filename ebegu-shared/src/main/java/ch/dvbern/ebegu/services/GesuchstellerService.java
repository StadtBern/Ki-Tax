package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Gesuchstellern
 */
public interface GesuchstellerService {

	/**
	 * Aktualisiert die Gesuchsteller in der DB.
	 * @param gesuchsteller Die Gesuchsteller als DTO
	 * @param gsNumber
	 */
	@Nonnull
	Gesuchsteller saveGesuchsteller(@Nonnull Gesuchsteller gesuchsteller, final Gesuch gesuch, Integer gsNumber);

	/**

	 * @param key PK (id) der Gesuchsteller
	 * @return Gesuchsteller mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Gesuchsteller> findGesuchsteller(@Nonnull String key);

	/**
	 *
	 * @return Liste aller Gesuchsteller aus der DB
	 */
	@Nonnull
	Collection<Gesuchsteller> getAllGesuchsteller();

	/**
	 * entfernt eine Gesuchsteller aus der Databse
	 * @param gesuchsteller Gesuchsteller zu entfernen
	 */
	void removeGesuchsteller(@Nonnull Gesuchsteller gesuchsteller);

}

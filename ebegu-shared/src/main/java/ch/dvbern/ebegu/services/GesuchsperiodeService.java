package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuchsperiode;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Gesuchsperiode
 */
public interface GesuchsperiodeService {

	/**
	 * Erstellt eine neue Gesuchsperiode in der DB, falls der key noch nicht existiert
	 * @param gesuchsperiode die Gesuchsperiode als DTO
	 * @return die gespeicherte Gesuchsperiode
	 */
	@Nonnull
	Gesuchsperiode saveGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode);

	/**
	 *
	 * @param key PK (id) der Gesuchsperiode
	 * @return Gesuchsperiode mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Gesuchsperiode> findGesuchsperiode(@Nonnull String key);

	/**
	 * Gibt alle existierenden Gesuchsperioden zurueck.
	 * @return Liste aller Gesuchsperiodeen aus der DB
	 */
	@Nonnull
	Collection<Gesuchsperiode> getAllGesuchsperioden();

	/**
	 * entfernt eine Gesuchsperiode aus der Database
	 * @param gesuchsperiode die Gesuchsperiode als DTO
	 */
	void removeGesuchsperiode(@Nonnull String gesuchsperiode);

	/**
	 * Gibt alle aktiven Gesuchsperioden zurueck.
	 * @return Liste aller Gesuchsperiodeen aus der DB
	 */
	@Nonnull
	Collection<Gesuchsperiode> getAllActiveGesuchsperioden();

	/**
	 * Gibt alle Gesuchsperioden zurueck, deren Ende-Datum noch nicht erreicht ist.
     */
	@Nonnull
	Collection<Gesuchsperiode> getAllNichtAbgeschlosseneGesuchsperioden();
}

package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Gesuchsperiode
 */
public interface GesuchsperiodeService {

	/**
	 * Erstellt eine neue Gesuchsperiode in der DB, falls der key noch nicht existiert
	 */
	@Nonnull
	Gesuchsperiode saveGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode);

	/**
	 * Erstellt eine neue Gesuchsperiode in der DB, falls der key noch nicht existiert.
	 * Aufgrund des letzten Status wird geprüft, ob der Statusübergang zulässig ist und ob
	 * evt. weitere Aktionen durchgeführt werden müssen (z.B. E-Mails etc.)
	 */
	@Nonnull
	Gesuchsperiode saveGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode, @Nonnull GesuchsperiodeStatus statusBisher);

	/**
	 * @param key PK (id) der Gesuchsperiode
	 * @return Gesuchsperiode mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Gesuchsperiode> findGesuchsperiode(@Nonnull String key);

	/**
	 * Gibt alle existierenden Gesuchsperioden zurueck.
	 *
	 * @return Liste aller Gesuchsperiodeen aus der DB
	 */
	@Nonnull
	Collection<Gesuchsperiode> getAllGesuchsperioden();

	/**
	 * Loescht alle Gesuchsperioden inkl. Gesuche und Dokumente, wenn die Gesuchsperiode mehr als 10 Jahre alt ist.
	 */
	void removeGesuchsperiode(@Nonnull String gesuchsPeriodeId);

	/**
	 * Gibt alle aktiven Gesuchsperioden zurueck.
	 *
	 * @return Liste aller Gesuchsperiodeen aus der DB
	 */
	@Nonnull
	Collection<Gesuchsperiode> getAllActiveGesuchsperioden();

	/**
	 * Gibt alle Gesuchsperioden zurueck, deren Ende-Datum noch nicht erreicht ist.
	 */
	@Nonnull
	Collection<Gesuchsperiode> getAllNichtAbgeschlosseneGesuchsperioden();

	/**
	 * Gibt die Gesuchsperiode zurueck, welche am uebergebenen Stichtag aktuell war/ist
	 */
	@Nonnull
	Optional<Gesuchsperiode> getGesuchsperiodeAm(@Nonnull LocalDate stichtag);

	/**
	 * Gibt alle Gesuchsperioden zurueck, welche im angegebenen Zeitraum liegen (nicht zwingend vollständig)
	 */
	@Nonnull
	Collection<Gesuchsperiode> getGesuchsperiodenBetween(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis);
}

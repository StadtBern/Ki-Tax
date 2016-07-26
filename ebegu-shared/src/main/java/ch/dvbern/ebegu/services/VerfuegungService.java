package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Verfuegung;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum berechnen und speichern der Verfuegung
 */
public interface VerfuegungService {

	/**
	 * Speichert die Verfuegung neu in der DB falls der Key noch nicht existiert.
	 * @param verfuegung Die Verfuegung als DTO
	 */
	@Nonnull
	Verfuegung saveVerfuegung(@Nonnull Verfuegung verfuegung);

	/**
	 * @param id PK (id) der Verfuegung
	 * @return Verfuegung mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Verfuegung> findVerfuegung(@Nonnull String id);

	/**
	 * @return Liste aller Verfuegung aus der DB
	 */
	@Nonnull
	Collection<Verfuegung> getAllVerfuegungen();

	/**
	 * entfernt eine Verfuegung aus der Databse
	 * @param verfuegung Verfuegung zu entfernen
	 */
	void removeVerfuegung(@Nonnull Verfuegung verfuegung);

	/**
	 * Berechnet die Verfuegung fuer eine einezelne Betreuung
	 * @return gibt die Betreuung mit der berechneten angehangten Verfuegung zurueck
     */
	@Nonnull
	Betreuung calculateVerfuegung(@Nonnull Betreuung betreuung);

	@Nonnull
	Gesuch calculateVerfuegung(@Nonnull Gesuch gesuch);


}

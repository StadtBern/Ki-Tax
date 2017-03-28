package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.Betreuungsstatus;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service zum berechnen und speichern der Verfuegung
 */
public interface VerfuegungService {

	/**
	 * Speichert die Verfuegung neu in der DB falls der Key noch nicht existiert.
	 * Die Betreuung erhaelt den Status VERFUEGT
	 * @param verfuegung Die Verfuegung als DTO
	 * @param betreuungId Id der Betreuung auf die die verfuegung gespeichet werden soll
	 * @param ignorieren true wenn die ausbezahlten Zeitabschnitte nicht neu berechnet werden muessen
	 */
	@Nonnull
	Verfuegung verfuegen(@Nonnull Verfuegung verfuegung, @Nonnull String betreuungId, boolean ignorieren);

	/**
	 * Speichert die Verfuegung neu in der DB falls der Key noch nicht existiert.
	 * Die Betreuung erhaelt den Status NICHT_EINGETRETEN
	 * @param verfuegung Die Verfuegung als DTO
	 * @param betreuungId Id der Betreuung auf die die verfuegung gespeichet werden soll
	 */
	@Nonnull
	Verfuegung nichtEintreten(@Nonnull Verfuegung verfuegung, @Nonnull String betreuungId);

	/**
	 * Speichert die Verfuegung und setzt die Betreuung in den uebergebenen Status
     */
	@Nonnull
	Verfuegung persistVerfuegung(@Nonnull Verfuegung verfuegung, @Nonnull String betreuungId, @Nonnull Betreuungsstatus betreuungsstatus);

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
	 * Berechnet die Verfuegung fuer ein Gesuch
	 * @return gibt die Betreuung mit der berechneten angehangten Verfuegung zurueck
     */
	@Nonnull
	Gesuch calculateVerfuegung(@Nonnull Gesuch gesuch);


	/**
	 * gibt die Verfuegung der vorherigen verfuegten Betreuung zurueck.
	 * kann null sein
	 * @param betreuung
	 * @return Verfuegung oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Verfuegung> findVorgaengerVerfuegung(@Nonnull Betreuung betreuung);

	/**
	 * genau wie findVorgaengerVerfuegung gibt aber nur deren TimestampErstellt zurueck wenn vorhanden
	 */
	Optional<LocalDate> findVorgaengerVerfuegungDate(@Nonnull Betreuung betreuung);

	/**
	 * Sucht den Zeitabschnitt / die Zeitabschnitte mit demselben Zeitraum auf der Vorgängerverfügung,
	 * und die verrechnet oder ignoriert sind
	 */
	@Nonnull
	List<VerfuegungZeitabschnitt> findVerrechnetenZeitabschnittOnVorgaengerVerfuegung(@Nonnull VerfuegungZeitabschnitt zeitabschnittNeu,
																					  @Nonnull Betreuung betreuungNeu);
}

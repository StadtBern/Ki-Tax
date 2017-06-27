package ch.dvbern.ebegu.services;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;

import ch.dvbern.ebegu.entities.EbeguVorlage;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Vorlage;
import ch.dvbern.ebegu.enums.EbeguVorlageKey;

/**
 * Service zum Verwalten von EbeguVorlagen
 */
public interface EbeguVorlageService {

	/**
	 * Speichert den EbeguVorlage neu in der DB falls der Key noch nicht existiert. Sonst wird das existierende EbeguVorlage aktualisiert
	 * @param ebeguVorlage Das EbeguVorlage als DTO
	 */
	@Nonnull
	EbeguVorlage saveEbeguVorlage(@Nonnull EbeguVorlage ebeguVorlage);

	/**
	 * Gibt eine optionale Vorlage fuer den uebergebenen Key zurueck, welche im uebergebenen Zeitraum gueltig ist.
	 */
	@Nonnull
	Optional<EbeguVorlage> getEbeguVorlageByDatesAndKey(LocalDate abDate, LocalDate bisDate, EbeguVorlageKey ebeguVorlageKey);

	/**
	 * Gibt eine optionale Vorlage fuer den uebergebenen Key zurueck, welche im uebergebenen Zeitraum gueltig ist.
	 */
	@Nonnull
	Optional<EbeguVorlage> getEbeguVorlageByDatesAndKey(LocalDate abDate, LocalDate bisDate, EbeguVorlageKey ebeguVorlageKey, EntityManager em);

	/**
	 * Gibt alle Vorlagen einer Gesuchsperiode zurueck.
	 */
	@Nonnull
	List<EbeguVorlage> getALLEbeguVorlageByGesuchsperiode(Gesuchsperiode gesuchsperiode);

	/**
	 * Aktualisiert die EbeguVorlage in der DB
	 * @param ebeguVorlage Die EbeguVorlage als DTO
	 */
	@Nullable
	EbeguVorlage updateEbeguVorlage(@Nonnull EbeguVorlage ebeguVorlage);

	/**
	 * Entfernt die Vorlage mit der uebergebenen Id aus der Datenbank
	 */
	void removeVorlage(@Nonnull String id);

	/**
	 * Sucht die Vorlage mit der uebergebenen Id.
	 */
	@Nonnull
	Optional<EbeguVorlage> findById(@Nonnull final String id);

	/**
	 * Gibt alle Vorlagen zurueck, welche am Stichtag gueltig sind.
	 * @param proGesuchsperiode true, wenn nur Gesuchsperioden-abhaengige Vorlagen gesucht werden sollen
	 */
	@Nonnull
	Collection<EbeguVorlage> getALLEbeguVorlageByDate(@Nonnull LocalDate date, boolean proGesuchsperiode);

	/**
	 * Kopiert alle Vorlagen einer Gesuchsperiode zur naechsten (uebergebenen) Gesuchsperiode
	 * @param gesuchsperiodeToCopyTo Die neue Gesuchsperiode
	 */
	void copyEbeguVorlageListToNewGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiodeToCopyTo);

	/**
	 * Gibt fuer den eingeloggten Benutzer das richtige Benutzerhandbuch zurueck.
	 */
	Vorlage getBenutzerhandbuch();
}

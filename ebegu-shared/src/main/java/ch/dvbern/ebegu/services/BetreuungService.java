package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Betreuung;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service zum Verwalten von Betreuungen
 */
public interface BetreuungService {

	/**
	 * Speichert die Betreuung neu in der DB falls der Key noch nicht existiert. Sonst wird die existierende Betreuung aktualisiert
	 * Bean validation wird eingeschaltet
	 *
	 * @param betreuung Die Betreuung als DTO
	 */
	@Nonnull
	Betreuung saveBetreuung(@Valid @Nonnull Betreuung betreuung, @Nonnull Boolean isAbwesenheit);

	/**
	 * @param key PK (id) der Betreuung
	 * @return Betreuung mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Betreuung> findBetreuung(@Nonnull String key);

	@Nonnull
	Optional<Betreuung> findBetreuungWithBetreuungsPensen(@Nonnull String key);

	/**
	 * entfernt eine Betreuung aus der Databse
	 *
	 * @param betreuungId Id der Betreuung zu entfernen
	 */
	void removeBetreuung(@Nonnull String betreuungId);

	/**
	 * entfernt eine Betreuuung aus der Databse. Um diese Methode aufzurufen muss man sich vorher vergewissern, dass die Betreuuung existiert
	 *
	 * @param betreuung
	 */
	void removeBetreuung(@Nonnull Betreuung betreuung);

	/**
	 * Gibt die Pendenzen fuer einen Benutzer mit Rolle Institution oder Traegerschaft zurueck.
	 * Dies sind Betreuungen, welche zu einer Institution gehoeren, fuer welche der Benutzer berechtigt ist,
	 * und deren Status "WARTEN" ist.
	 */
	@Nonnull
	Collection<Betreuung> getPendenzenForInstitutionsOrTraegerschaftUser();

	@Nonnull
	List<Betreuung> findAllBetreuungenFromGesuch(String gesuchId);

	/**
	 * Schliesst die Betreuung (Status GESCHLOSSEN_OHNE_VERFUEGUNG) ohne eine neue Verfuegung zu erstellen
	 * (bei gleichbleibenden Daten)
     */
	@Nonnull
	Betreuung schliessenOhneVerfuegen(@Nonnull Betreuung betreuung);

	/**
	 * Schliesst die Betreuung mit Status NICHT_EINGETRETEN.
     */
	@Nonnull
	Betreuung nichtEintreten(@Nonnull Betreuung betreuung);
}

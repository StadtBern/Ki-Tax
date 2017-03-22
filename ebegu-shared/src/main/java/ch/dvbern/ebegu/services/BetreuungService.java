package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Abwesenheit;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Fall;

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
	 * Setzt die Betreuungsplatzanfrage auf ABGEWIESEN und sendet dem Gesuchsteller eine E-Mail
	 */
	@Nonnull
	Betreuung betreuungPlatzAbweisen(@Valid @Nonnull Betreuung betreuung);

	/**
	 * Setzt die Betreuungsplatzanfrage auf BESTAETIGT und sendet dem Gesuchsteller eine E-Mail,
	 * falls damit alle Betreuungen des Gesuchs bestaetigt sind.
	 */
	@Nonnull
	Betreuung betreuungPlatzBestaetigen(@Valid @Nonnull Betreuung betreuung);

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
	 * @param fall Fall, dessen Verfuegungen zurueckgegeben werden
	 * @return BetreuungList, welche zum Fall gehoeren oder null
	 */
	@Nonnull
	List<Betreuung> findAllBetreuungenFromFall(@Nonnull Fall fall);

	/**
	 * Schliesst die Betreuung (Status GESCHLOSSEN_OHNE_VERFUEGUNG) ohne eine neue Verfuegung zu erstellen
	 * (bei gleichbleibenden Daten)
     */
	@Nonnull
	Betreuung schliessenOhneVerfuegen(@Nonnull Betreuung betreuung);

	/**
	 * Gibt alle Betreuungen zurueck, welche Mutationen betreffen, die verfügt sind und deren
	 * betreuungMutiert-Flag noch nicht gesetzt sind
	 */
	@Nonnull
	List<Betreuung> getAllBetreuungenWithMissingStatistics();

	/**
	 * Gibt alle Abwesenheiten zurueck, welche Mutationen betreffen, die verfügt sind und deren
	 * abwesenheitMutiert-Flag noch nicht gesetzt sind
	 */
	@Nonnull
	List<Abwesenheit> getAllAbwesenheitenWithMissingStatistics();
}

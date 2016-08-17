package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Betreuung;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Betreuungen
 */
public interface BetreuungService {

	/**
	 * Speichert die Betreuung neu in der DB falls der Key noch nicht existiert. Sonst wird die existierende Betreuung aktualisiert
	 * @param betreuung Die Betreuung als DTO
	 */
	@Nonnull
	Betreuung saveBetreuung(@Valid @Nonnull Betreuung betreuung);

	/**
	 * @param key PK (id) der Betreuung
	 * @return Betreuung mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Betreuung> findBetreuung(@Nonnull String key);

	@Nonnull
	Betreuung findBetreuungWithBetreuungsPensen(@Nonnull String key);

	/**
	 * entfernt eine Betreuung aus der Databse
	 * @param betreuungId Id der Betreuung zu entfernen
	 */
	void removeBetreuung(@Nonnull String betreuungId);

	/**
	 * Gibt die Pendenzen fuer einen Benutzer mit Rolle Institution oder Traegerschaft zurueck.
	 * Dies sind Betreuungen, welche zu einer Institution gehoeren, fuer welche der Benutzer berechtigt ist,
	 * und deren Status "WARTEN" ist.
     */
	@Nonnull
	Collection<Betreuung> getPendenzenForInstitutionsOrTraegerschaftUser();

}

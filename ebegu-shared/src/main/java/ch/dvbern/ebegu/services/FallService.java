package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Fall;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Fallen
 */
public interface FallService {

	/**
	 * Erstellt einen neuen Fall in der DB, falls der key noch nicht existiert. Sollte es existieren, aktualisiert es den Inhalt
	 *
	 * @param fall der Fall als DTO
	 * @return den gespeicherten Fall
	 */
	@Nonnull
	Fall saveFall(@Nonnull Fall fall);

	/**
	 * @param key PK (id) des Falles
	 * @return Fall mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
 	Optional<Fall> findFall(@Nonnull String key);

	/**
	 * Gibt den Fall mit der angegebenen Fall-Nummer zurueck
	 */
	@Nonnull
	Optional<Fall> findFallByNumber(@Nonnull Long fallnummer);

	/**
	 * Gibt den Fall des eingeloggten Benutzers zurueck
	 */
	@Nonnull
	Optional<Fall> findFallByCurrentBenutzerAsBesitzer();

	/**
	 * Gibt den Fall zurueck der zum eingeloggten Benutzer gehoert oder ein leeres optional wenn keiner vorhanden
	 * @param benutzer
	 * @return
	 */
	@Nonnull
	Optional<Fall> findFallByBesitzer(Benutzer benutzer);

	/**
	 * Gibt alle existierenden Faelle zurueck.
	 *
	 * @return Liste aller Faelle aus der DB
	 */
	@Nonnull
	Collection<Fall> getAllFalle();

	/**
	 * entfernt einen Fall aus der Database
	 *
	 * @param fall der Fall als DTO
	 */
	void removeFall(@Nonnull Fall fall);

	/**
	 * Erstellt einen neuen Fall fuer den aktuellen Benutzer und setzt diesen als Besitzer des Falles.
	 * - Nur wenn der aktuellen Benutzer ein GESUCHSTELLER ist und noch keinen Fall zugeordnet hat
	 * - In allen anderen Fällen ein Optional.empty() wird zurueckgegeben
	 */
	Optional<Fall> createFallForCurrentGesuchstellerAsBesitzer();


	/**
	 * Gibt die GS1-Emailadresse des neusten Gesuchs fuer diesen Fall zurueck, wenn noch kein Gesuch vorhanden ist, wird
	 * die E-Mail zurueckgegeben die beim besitzer des Falls eingegeben wurde (aus IAM importiert)
	 */
	Optional<String> getEmailAddressForFall(String fallID);
}

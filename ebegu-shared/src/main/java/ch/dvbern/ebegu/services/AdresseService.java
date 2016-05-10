package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.PersonenAdresse;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Personen Adressen
 */
public interface AdresseService {

	/**
	 * Speichert die Adresse neu in der DB falls der Key noch nicht existiert.
	 *
	 * @param personenAdresse Die Adresse als DTO
	 */
	@Nonnull
	PersonenAdresse createAdresse(@Nonnull PersonenAdresse personenAdresse);

	/**
	 * Aktualisiert die Adresse in der DB.
	 *
	 * @param personenAdresse Die Adresse als DTO
	 */
	@Nonnull
	PersonenAdresse updateAdresse(@Nonnull PersonenAdresse personenAdresse);

	/**
	 * @param key PK (id) der Adresse
	 * @return Adresse mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<PersonenAdresse> findAdresse(@Nonnull String key);

	/**
	 * @return Liste aller Adressen aus der DB
	 */
	@Nonnull
	Collection<PersonenAdresse> getAllAdressen();

	/**
	 * entfernt eine Adresse aus der Databse
	 *
	 * @param personenAdresse Adresse zu entfernen
	 */
	void removeAdresse(@Nonnull PersonenAdresse personenAdresse);

	/**
	 * Laedt die aktuellste Adresse mit gueltigBis EndOfTime fuer die Gesuchsteller mit gesuchstellerID
	 */
	@Nonnull
	Optional<PersonenAdresse> getNewestWohnadresse(String gesuchstellerID);

	/**
	 * Laedt die Korrespondenzadresse (aktuell gibt es immer nur 1) fuer die Gesuchsteller mit gesuchstellerID
	 */
	@Nonnull
	Optional<PersonenAdresse> getKorrespondenzAdr(String gesuchstellerID);

	/**
	 * Laedt die Wohnadresse die Stichtag heute gueltig ist
	 */
	@Nonnull
	PersonenAdresse getCurrentWohnadresse(String gesuchstellerID);
}

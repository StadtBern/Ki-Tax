package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.GesuchstellerAdresseContainer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Personen Adressen
 */
public interface GesuchstellerAdresseService {

	/**
	 * Speichert die Adresse neu in der DB falls der Key noch nicht existiert.
	 *
	 * @param gesuchstellerAdresse Die Adresse als DTO
	 */
	@Nonnull
	GesuchstellerAdresseContainer createAdresse(@Nonnull GesuchstellerAdresseContainer gesuchstellerAdresse);

	/**
	 * Aktualisiert die Adresse in der DB.
	 *
	 * @param gesuchstellerAdresse Die Adresse als DTO
	 */
	@Nonnull
	GesuchstellerAdresseContainer updateAdresse(@Nonnull GesuchstellerAdresseContainer gesuchstellerAdresse);

	/**
	 * @param key PK (id) der Adresse
	 * @return Adresse mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<GesuchstellerAdresseContainer> findAdresse(@Nonnull String key);

	/**
	 * @return Liste aller Adressen aus der DB
	 */
	@Nonnull
	Collection<GesuchstellerAdresseContainer> getAllAdressen();

	/**
	 * entfernt eine Adresse aus der Databse
	 *
	 * @param gesuchstellerAdresse Adresse zu entfernen
	 */
	void removeAdresse(@Nonnull GesuchstellerAdresseContainer gesuchstellerAdresse);

	/**
	 * Laedt die aktuellste Adresse mit gueltigBis EndOfTime fuer die Gesuchsteller mit gesuchstellerID
	 */
	@Nonnull
	Optional<GesuchstellerAdresseContainer> getNewestWohnadresse(String gesuchstellerID);

	/**
	 * Laedt die Korrespondenzadresse (aktuell gibt es immer nur 1) fuer die Gesuchsteller mit gesuchstellerID
	 */
	@Nonnull
	Optional<GesuchstellerAdresseContainer> getKorrespondenzAdr(String gesuchstellerID);

	/**
	 * Laedt die Wohnadresse die Stichtag heute gueltig ist
	 */
	@Nonnull
	GesuchstellerAdresseContainer getCurrentWohnadresse(String gesuchstellerID);
}

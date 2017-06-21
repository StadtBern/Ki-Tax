package ch.dvbern.ebegu.services;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.GesuchstellerAdresseContainer;

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
	 * @param id PK (id) der Adresse
	 * @return Adresse mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<GesuchstellerAdresseContainer> findAdresse(@Nonnull String id);

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
	 * Laedt die Korrespondenzadresse (aktuell gibt es immer nur 1) fuer die Gesuchsteller mit gesuchstellerID
	 */
	@Nonnull
	Optional<GesuchstellerAdresseContainer> getKorrespondenzAdr(@Nonnull String gesuchstellerID);
}

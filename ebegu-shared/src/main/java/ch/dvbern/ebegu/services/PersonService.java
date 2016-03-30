package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Person;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Personen
 */
public interface PersonService {

	/**
	 * Speichert die Person neu in der DB falls der Key noch nicht existiert.
	 * @param person Die Person als DTO
	 */
	@Nonnull
	Person createPerson(@Nonnull Person person);

	/**
	 * Aktualisiert die Person in der DB.
	 * @param person Die Person als DTO
	 */
	@Nonnull
	Person updatePerson(@Nonnull Person person);

	/**

	 * @param key PK (id) der Person
	 * @return Person mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Person> findPerson(@Nonnull String key);

	/**
	 *
	 * @return Liste aller Personn aus der DB
	 */
	@Nonnull
	Collection<Person> getAllPersonen();

	/**
	 * entfernt eine Person aus der Databse
	 * @param person Person zu entfernen
	 */
	void removePerson(@Nonnull Person person);

}

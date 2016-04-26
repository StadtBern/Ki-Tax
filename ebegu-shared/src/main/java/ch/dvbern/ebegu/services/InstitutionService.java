package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Institution;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Institutionen
 */
public interface InstitutionService {

	/**
	 * Speichert die Institution neu in der DB falls der Key noch nicht existiert.
	 * @param institution Die Institution als DTO
	 */
	@Nonnull
	Institution saveInstitution(@Nonnull Institution institution);

	/**
	 * @param key PK (id) der Institution
	 * @return Institution mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Institution> findInstitution(@Nonnull String key);

	/**
	 * removes an Institution from the Database.
	 * @param InstitutionId
	 */
	void removeInstitution(@Nonnull String InstitutionId);

	/**
	 *
	 * @param traegerschaftId Der ID der Traegerschaft, fuer welche die Institutionen gesucht werden muessen
	 * @return Liste mit allen Institutionen der gegebenen Traegerschaft
     */
	@Nonnull
	Collection<Institution> getAllInstitutionenFromTraegerschaft(String traegerschaftId);
}

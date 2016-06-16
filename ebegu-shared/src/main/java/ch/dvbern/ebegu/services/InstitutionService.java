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
	 * Aktualisiert die Institution in der DB
	 * @param institution Die Institution als DTO
	 */
	@Nonnull
	Institution updateInstitution(@Nonnull Institution institution);

	/**
	 * Speichert die Institution neu in der DB
	 * @param institution Die Institution als DTO
	 */
	@Nonnull
	Institution createInstitution(@Nonnull Institution institution);

	/**
	 * @param key PK (id) der Institution
	 * @return Institution mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Institution> findInstitution(@Nonnull String key);

	/**
	 * marks an Institution as inactive on the Database.
	 * @param InstitutionId
	 */
	void setInstitutionInactive(@Nonnull String InstitutionId);

	/**
	 * Delete Institution on the Database.
	 * @param InstitutionId
	 */
	void deleteInstitution(@Nonnull String InstitutionId);

	/**
	 *
	 * @param traegerschaftId Der ID der Traegerschaft, fuer welche die Institutionen gesucht werden muessen
	 * @return Liste mit allen Institutionen der gegebenen Traegerschaft
     */
	@Nonnull
	Collection<Institution> getAllInstitutionenFromTraegerschaft(String traegerschaftId);

	/**
	 *
	 * @return Alle Institutionen in der DB
     */
	@Nonnull
	Collection<Institution> getAllInstitutionen();

	/**
	 *
	 * @return Gibt alle aktive Institutionen zurück
	 */
	Collection<Institution> getAllActiveInstitutionen();
}

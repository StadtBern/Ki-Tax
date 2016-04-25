package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Institution;

import javax.annotation.Nonnull;
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
	Institution createInstitution(@Nonnull Institution institution);

	/**
	 * @param key PK (id) der Institution
	 * @return Institution mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Institution> findInstitution(@Nonnull String key);

}

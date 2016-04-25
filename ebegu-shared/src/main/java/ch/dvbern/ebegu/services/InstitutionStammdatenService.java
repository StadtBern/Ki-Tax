package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.InstitutionStammdaten;

import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von InstitutionStammdaten
 */
public interface InstitutionStammdatenService {

	/**
	 * Erstellt eine InstitutionStammdaten in der DB. Wenn eine InstitutionStammdaten mit demselben ID bereits existiert
	 * wird diese dann aktualisiert.
	 * @param institutionStammdaten Die InstitutionStammdaten als DTO
	 */
	InstitutionStammdaten saveInstitutionStammdaten(InstitutionStammdaten institutionStammdaten);

	/**
	 * @param institutionStammdatenID PK (id) der InstitutionStammdaten
	 * @return InstitutionStammdaten mit dem gegebenen key oder null falls nicht vorhanden
	 */
	Optional<InstitutionStammdaten> findInstitutionStammdaten(String institutionStammdatenID);

	/**
	 *
	 * @return Aller InstitutionStammdaten aus der DB.
     */
	Collection<InstitutionStammdaten> getAllInstitutionStammdaten();
}

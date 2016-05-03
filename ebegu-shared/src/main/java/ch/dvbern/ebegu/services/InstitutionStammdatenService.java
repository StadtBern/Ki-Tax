package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.InstitutionStammdaten;

import javax.annotation.Nonnull;
import java.time.LocalDate;
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

	/**
	 * removes a InstitutionStammdaten from the Database.
	 * @param institutionStammdatenId  PK (id) der InstitutionStammdaten
	 */
	void removeInstitutionStammdaten(@Nonnull String institutionStammdatenId);

	/**
	 *
	 * @param date Das Datum fuer welches die InstitutionStammdaten gesucht werden muessen
	 * @return Alle InstitutionStammdaten, bei denen das gegebene Datum zwischen datumVon und datumBis liegt
     */
	Collection<InstitutionStammdaten> getAllInstitutionStammdatenByDate(LocalDate date);
}

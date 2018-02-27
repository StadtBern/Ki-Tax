/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.services;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;

/**
 * Service zum Verwalten von InstitutionStammdaten
 */
public interface InstitutionStammdatenService {

	/**
	 * Erstellt eine InstitutionStammdaten in der DB. Wenn eine InstitutionStammdaten mit demselben ID bereits existiert
	 * wird diese dann aktualisiert.
	 *
	 * @param institutionStammdaten Die InstitutionStammdaten als DTO
	 */
	InstitutionStammdaten saveInstitutionStammdaten(InstitutionStammdaten institutionStammdaten);

	/**
	 * @param institutionStammdatenID PK (id) der InstitutionStammdaten
	 * @return InstitutionStammdaten mit dem gegebenen key oder null falls nicht vorhanden
	 */
	Optional<InstitutionStammdaten> findInstitutionStammdaten(String institutionStammdatenID);

	/**
	 * @return Aller InstitutionStammdaten aus der DB.
	 */
	Collection<InstitutionStammdaten> getAllInstitutionStammdaten();

	/**
	 * removes a InstitutionStammdaten from the Database.
	 *
	 * @param institutionStammdatenId PK (id) der InstitutionStammdaten
	 */
	void removeInstitutionStammdaten(@Nonnull String institutionStammdatenId);

	/**
	 * @param date Das Datum fuer welches die InstitutionStammdaten gesucht werden muessen
	 * @return Alle InstitutionStammdaten, bei denen das gegebene Datum zwischen datumVon und datumBis liegt
	 */
	Collection<InstitutionStammdaten> getAllInstitutionStammdatenByDate(LocalDate date);

	/**
	 * @param gesuchsperiodeId Id der gewuenschten Gesuchsperiode
	 * @return Alle aktiven InstitutionStammdaten bei denen eine Ueberschneidung der Gueltigkeit zwischen datumVon und datumBis liegt
	 */
	Collection<InstitutionStammdaten> getAllActiveInstitutionStammdatenByGesuchsperiode(@Nonnull String gesuchsperiodeId);

	/**
	 * @param institutionId Die Institutions-id für welche alle Stammdaten gesucht werden sollen
	 * @return Alle InstitutionStammdaten, bei denen die Institution dem übergebenen id-Wert entspricht
	 */
	Collection<InstitutionStammdaten> getAllInstitutionStammdatenByInstitution(String institutionId);

	/**
	 * Gibt alle Betreuungsangebotstypen zurueck, welche die Institutionen anbieten, fuer welche der
	 * aktuell eingeloggte Benutzer berechtigt ist. Sollte der Benutzer ein Admin oder Sachbearbeiter vom Schulamt
	 * sein, wird dann direkt TAGESSCHULE und FERIENINSEL zurueckgegeben.
	 */
	Collection<BetreuungsangebotTyp> getBetreuungsangeboteForInstitutionenOfCurrentBenutzer();
}

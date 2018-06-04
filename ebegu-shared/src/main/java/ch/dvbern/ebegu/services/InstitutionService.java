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

import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;

/**
 * Service zum Verwalten von Institutionen
 */
public interface InstitutionService {

	/**
	 * Aktualisiert die Institution in der DB
	 *
	 * @param institution Die Institution als DTO
	 */
	@Nonnull
	Institution updateInstitution(@Nonnull Institution institution);

	/**
	 * Speichert die Institution neu in der DB
	 *
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
	 */
	Institution setInstitutionInactive(@Nonnull String institutionId);

	/**
	 * Delete Institution on the Database.
	 */
	void deleteInstitution(@Nonnull String institutionId);

	/**
	 * @param traegerschaftId Der ID der Traegerschaft, fuer welche die Institutionen gesucht werden muessen
	 * @return Liste mit allen Institutionen der gegebenen Traegerschaft
	 */
	@Nonnull
	Collection<Institution> getAllInstitutionenFromTraegerschaft(String traegerschaftId);

	/**
	 * @return gibt alle aktiven Institution einer Traegerschaft zurueck
	 */
	@Nonnull
	Collection<Institution> getAllActiveInstitutionenFromTraegerschaft(String traegerschaftId);

	/**
	 * @return Alle Institutionen in der DB
	 */
	@Nonnull
	Collection<Institution> getAllInstitutionen();

	/**
	 * @return Gibt alle aktive Institutionen zurück
	 */
	Collection<Institution> getAllActiveInstitutionen();

	/**
	 * Gibt alle aktiven Institutionen zurueck, fuer welche der aktuell eingeloggte Benutzer berechtigt ist.
	 * @param restrictedForSCH true wenn nur die Institutionen der Art TAGESSCHULE oder FERIENINSEL geholt werden. Dieses Parameter
	 * gilt nur fuer die Rolen vom Schulamt
	 */
	Collection<Institution> getAllowedInstitutionenForCurrentBenutzer(boolean restrictedForSCH);

	/**
	 * returns all types of Angebot that are offered by this Institution
	 */
	EnumSet<BetreuungsangebotTyp> getAllAngeboteFromInstitution(@Nonnull String institutionId);

}

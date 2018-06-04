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

import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;

/**
 * Service zum Verwalten von Traegerschaften
 */
public interface TraegerschaftService {

	/**
	 * Speichert die Traegerschaft neu in der DB falls der Key noch nicht existiert.
	 *
	 * @param traegerschaft Die Traegerschaft als DTO
	 */
	@Nonnull
	Traegerschaft saveTraegerschaft(@Nonnull Traegerschaft traegerschaft);

	/**
	 * @param traegerschaftId PK (id) der Traegerschaft
	 * @return Traegerschaft mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Traegerschaft> findTraegerschaft(@Nonnull String traegerschaftId);

	/**
	 * @return Liste aller Traegerschaften aus der DB
	 */
	@Nonnull
	Collection<Traegerschaft> getAllTraegerschaften();

	/**
	 * @return Liste aller aktiven Traegerschaften aud der DB
	 */
	@Nonnull
	Collection<Traegerschaft> getAllActiveTraegerschaften();

	/**
	 * removes a Traegerschaft from the Databse
	 */
	void removeTraegerschaft(@Nonnull String traegerschaftId);

	/**
	 * marks an Traegerschft as inactive on the Database.
	 */
	void setInactive(@Nonnull String traegerschaftId);

	/**
	 * returns all types of Angebot that are offered by at least one of the Institutions of this traegerschaft
	 */
	EnumSet<BetreuungsangebotTyp> getAllAngeboteFromTraegerschaft(@Nonnull String traegerschaftId);
}

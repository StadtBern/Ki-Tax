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
import java.util.Optional;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Fachstelle;

/**
 * Service zum Verwalten von Fachstellen
 */
public interface FachstelleService {

	/**
	 * Aktualisiert die Fachstelle in der DB
	 *
	 * @param fachstelle die Fachstelle als DTO
	 * @return Die aktualisierte Fachstelle
	 */
	@Nonnull
	Fachstelle saveFachstelle(@Nonnull Fachstelle fachstelle);

	/**
	 * @param fachstelleId PK (id) der Fachstelle
	 * @return Fachstelle mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Fachstelle> findFachstelle(@Nonnull String fachstelleId);

	/**
	 * Gibt alle existierenden Fachstellen zurueck.
	 *
	 * @return Liste aller Fachstellen aus der DB
	 */
	@Nonnull
	Collection<Fachstelle> getAllFachstellen();

	/**
	 * entfernt die Fachstelle aus der Database
	 *
	 * @param fachstelleId die Fachstelle als DTO
	 */
	void removeFachstelle(@Nonnull String fachstelleId);

}

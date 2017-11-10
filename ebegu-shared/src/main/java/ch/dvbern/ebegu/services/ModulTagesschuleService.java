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
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.ModulTagesschule;

/**
 * Service zum Verwalten von Modulen
 */
public interface ModulTagesschuleService {

	/**
	 * Erstellt ein neues ModulTagesschule in der DB
	 *
	 * @param modulTagesschule das zu speichernde Modul
	 * @return das gespeicherte Modul
	 */
	@Nonnull
	ModulTagesschule createModul(@Nonnull ModulTagesschule modulTagesschule);

	/**
	 * Aktualisiert das ModulTagesschule in der DB
	 *
	 * @param modulTagesschule das zu aktualisierende Modul
	 * @return Das aktualisierte Modul
	 */
	@Nonnull
	ModulTagesschule updateModul(@Nonnull ModulTagesschule modulTagesschule);

	/**
	 * Laedt das ModulTagesschule mit der id aus der DB.
	 *
	 * @param modulTagesschuleId PK des Moduls
	 * @return Modul mit der gegebenen id oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<ModulTagesschule> findModul(@Nonnull String modulTagesschuleId);

	/**
	 * entfernt ein Modul aus der Database
	 *
	 * @param modulTagesschuleId des zu entfernenden Moduls
	 */
	void removeModul(@Nonnull String modulTagesschuleId);

	/**
	 * Gibt alle existierenden ModulTagesschule zurueck.
	 *
	 * @return Liste aller Module aus der DB
	 */
	//TODO wijo brauchts das getAll?
	@Nonnull
	Collection<ModulTagesschule> getAllModule();

	/**
	 * Gibt eine Liste von Modulen zureck, deren Modulname angegebenen Namen  hat.
	 * Achtung, damit ist ein Modul nicht eindeutig identifiziert!
	 *
	 * @param modulname des Moduls
	 */
	//TODO wijo brauchts das findByName?
	@Nonnull
	List<ModulTagesschule> findModulByName(String modulname);


	/**
	 * Gibt eine Liste der vorhandenen Montagsmodule der institutionsStammdaten (Tagesschule) zurueck
	 * @param institutionStammdatenID
	 * @return Liste der Montagsmodule der Tagesschule
	 */
	Collection<ModulTagesschule> findMondayModuleTagesschuleByInstitutionStammdaten(String institutionStammdatenID);

}

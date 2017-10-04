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

import ch.dvbern.ebegu.entities.GesuchstellerAdresseContainer;

/**
 * Service zum Verwalten von Personen Adressen
 */
public interface GesuchstellerAdresseService {

	/**
	 * Speichert die Adresse neu in der DB falls der Key noch nicht existiert.
	 *
	 * @param gesuchstellerAdresse Die Adresse als DTO
	 */
	@Nonnull
	GesuchstellerAdresseContainer createAdresse(@Nonnull GesuchstellerAdresseContainer gesuchstellerAdresse);

	/**
	 * Aktualisiert die Adresse in der DB.
	 *
	 * @param gesuchstellerAdresse Die Adresse als DTO
	 */
	@Nonnull
	GesuchstellerAdresseContainer updateAdresse(@Nonnull GesuchstellerAdresseContainer gesuchstellerAdresse);

	/**
	 * @param id PK (id) der Adresse
	 * @return Adresse mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<GesuchstellerAdresseContainer> findAdresse(@Nonnull String id);

	/**
	 * @return Liste aller Adressen aus der DB
	 */
	@Nonnull
	Collection<GesuchstellerAdresseContainer> getAllAdressen();

	/**
	 * entfernt eine Adresse aus der Databse
	 *
	 * @param gesuchstellerAdresse Adresse zu entfernen
	 */
	void removeAdresse(@Nonnull GesuchstellerAdresseContainer gesuchstellerAdresse);

	/**
	 * Laedt die Korrespondenzadresse (aktuell gibt es immer nur 1) fuer die Gesuchsteller mit gesuchstellerID
	 */
	@Nonnull
	Optional<GesuchstellerAdresseContainer> getKorrespondenzAdr(@Nonnull String gesuchstellerID);
}

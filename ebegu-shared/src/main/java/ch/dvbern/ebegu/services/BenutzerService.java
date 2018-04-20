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

import ch.dvbern.ebegu.entities.Benutzer;

/**
 * Service fuer die Verwaltung von Benutzern
 */
public interface BenutzerService {

	/**
	 * Aktualisiert den Benutzer in der DB or erstellt ihn wenn er noch nicht existiert
	 *
	 * @param benutzer die Benutzer als DTO
	 * @return Die aktualisierte Benutzer
	 */
	@Nonnull
	Benutzer saveBenutzer(@Nonnull Benutzer benutzer);

	/**
	 * @param username PK (id) des Benutzers
	 * @return Benutzer mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Benutzer> findBenutzer(@Nonnull String username);

	/**
	 * Gibt alle existierenden Benutzer zurueck.
	 *
	 * @return Liste aller Benutzern aus der DB
	 */
	@Nonnull
	Collection<Benutzer> getAllBenutzer();

	/**
	 * Gibt alle existierenden Benutzer mit Rolle Sachbearbeiter_JA oder Admin zurueck.
	 *
	 * @return Liste aller Benutzern mit entsprechender Rolle aus der DB
	 */
	@Nonnull
	Collection<Benutzer> getBenutzerJAorAdmin();

	/**
	 * Gibt alle existierenden Benutzer mit Rolle ADMINISTRATOR_SCHULAMT oder SCHULAMT zurueck.
	 *
	 * @return Liste aller Benutzern mit entsprechender Rolle aus der DB
	 */
	@Nonnull
	Collection<Benutzer> getBenutzerSCHorAdminSCH();

	/**
	 * @return Liste saemtlicher Gesuchsteller aus der DB
	 */
	@Nonnull
	Collection<Benutzer> getGesuchsteller();

	/**
	 * entfernt die Benutzer aus der Database
	 *
	 * @param username die Benutzer als DTO
	 */
	void removeBenutzer(@Nonnull String username);

	/**
	 * Gibt den aktuell eingeloggten Benutzer zurueck
	 */
	@Nonnull
	Optional<Benutzer> getCurrentBenutzer();

	/**
	 * inserts a user received from iam or updates it if it alreday exists
	 */
	Benutzer updateOrStoreUserFromIAM(Benutzer benutzer);
}

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
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.dto.suchfilter.smarttable.BenutzerTableFilterDTO;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Berechtigung;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.enums.UserRole;
import org.apache.commons.lang3.tuple.Pair;

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
	Benutzer updateOrStoreUserFromIAM(@Nonnull Benutzer benutzer);

	/**
	 * Setzt den uebergebenen Benutzer auf gesperrt. Es werden auch alle möglicherweise noch vorhandenen AuthentifizierteBenutzer gelöscht.
	 */
	@Nonnull
	Benutzer sperren(@Nonnull String username);

	/**
	 * Reaktiviert den uebergebenen Benutzer wieder.
	 */
	@Nonnull
	Benutzer reaktivieren(@Nonnull String username);

	/**
	 * Gibt alle Berechtigungen des Benutzers mit dem uebergebenen Username zurueck
	 */
	@Nonnull
	List<Berechtigung> getBerechtigungenForBenutzer(@Nonnull String username);

	/**
	 * Ändert die aktuelle Berechtigung des uebergebenen Benutzers mit den uebergebenen Attributen.
	 * Alle anderen Attribute werden nicht verändert.
	 */
	@Nonnull
	Benutzer changeRole(@Nonnull String username, @Nonnull UserRole userRole, @Nullable Institution institution,
		@Nullable Traegerschaft traegerschaft, @Nullable LocalDate gueltigAb, @Nullable LocalDate gueltigBis);

	/**
	 * Sucht Benutzer, welche den übergebenen Filterkriterien entsprechen
	 */
	@Nonnull
	Pair<Long, List<Benutzer>> searchBenutzer(@Nonnull BenutzerTableFilterDTO benutzerTableFilterDto);

	/**
	 * Setzt alle Benutzer mit abgelaufenen Rollen auf die Rolle GESUCHSTELLER zurück.
	 * @return Die Anzahl zurückgesetzter Benutzer
	 */
	int handleAbgelaufeneRollen(@Nonnull LocalDate stichtag);

	/**
	 * Suche die Berechtigung mit der uebergebenen Id
	 */
	@Nonnull
	Optional<Berechtigung> findBerechtigung(@Nonnull String id);

	/**
	 * Speichert die Berechtigungen zu einem Benutzer
	 */
	void saveBerechtigungen(@Nonnull Benutzer benutzer, @Nonnull List<Berechtigung> berechtigungen);
}

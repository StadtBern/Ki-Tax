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

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;

/**
 * Service zum Verwalten von Gesuchsperiode
 */
public interface GesuchsperiodeService {

	/**
	 * Erstellt eine neue Gesuchsperiode in der DB, falls der key noch nicht existiert
	 */
	@Nonnull
	Gesuchsperiode saveGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode);

	/**
	 * Erstellt eine neue Gesuchsperiode in der DB, falls der key noch nicht existiert.
	 * Aufgrund des letzten Status wird geprüft, ob der Statusübergang zulässig ist und ob
	 * evt. weitere Aktionen durchgeführt werden müssen (z.B. E-Mails etc.)
	 */
	@Nonnull
	Gesuchsperiode saveGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode, @Nonnull GesuchsperiodeStatus statusBisher);

	/**
	 * @param key PK (id) der Gesuchsperiode
	 * @return Gesuchsperiode mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Gesuchsperiode> findGesuchsperiode(@Nonnull String key);

	/**
	 * Gibt alle existierenden Gesuchsperioden zurueck.
	 *
	 * @return Liste aller Gesuchsperiodeen aus der DB
	 */
	@Nonnull
	Collection<Gesuchsperiode> getAllGesuchsperioden();

	/**
	 * Loescht alle Gesuchsperioden inkl. Gesuche und Dokumente, wenn die Gesuchsperiode mehr als 10 Jahre alt ist.
	 */
	void removeGesuchsperiode(@Nonnull String gesuchsPeriodeId);

	/**
	 * Gibt alle aktiven Gesuchsperioden zurueck.
	 *
	 * @return Liste aller Gesuchsperiodeen aus der DB
	 */
	@Nonnull
	Collection<Gesuchsperiode> getAllActiveGesuchsperioden();

	/**
	 * Gibt alle Gesuchsperioden zurueck, deren Ende-Datum noch nicht erreicht ist.
	 */
	@Nonnull
	Collection<Gesuchsperiode> getAllNichtAbgeschlosseneGesuchsperioden();

	/**
	 * Gibt alle Gesuchsperioden zurueck, deren Ende-Datum noch nicht erreicht ist, und für die der angegebene
	 * Fall noch kein Gesuch freigegeben hat.
	 */
	@Nonnull
	Collection<Gesuchsperiode> getAllNichtAbgeschlosseneNichtVerwendeteGesuchsperioden(String fallId);

	/**
	 * Gibt die Gesuchsperiode zurueck, welche am uebergebenen Stichtag aktuell war/ist
	 */
	@Nonnull
	Optional<Gesuchsperiode> getGesuchsperiodeAm(@Nonnull LocalDate stichtag);

	/**
	 * Gibt alle Gesuchsperioden zurueck, welche im angegebenen Zeitraum liegen (nicht zwingend vollständig)
	 */
	@Nonnull
	Collection<Gesuchsperiode> getGesuchsperiodenBetween(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis);
}

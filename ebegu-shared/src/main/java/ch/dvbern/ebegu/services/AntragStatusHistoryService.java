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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.AntragStatusHistory;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;

/**
 * Service zum Verwalten von AntragStatusHistory
 */
public interface AntragStatusHistoryService {

	/**
	 * Erstellt einen neuen Datensatz mit aktuellerStatus gleich wie der Status des uebergebenden Gesuchs
	 * und alterStatus, der Status der es vorher hatte. Wenn saveAsUser null ist wird der eingeloggte Benutzer
	 * wird als benutzer hinterlegt, sonst wird saveAsUser als benutzer hinterlegt
	 */
	@Nonnull
	AntragStatusHistory saveStatusChange(@Nonnull Gesuch gesuch, @Nullable Benutzer saveAsUser);

	/**
	 * Findet den letzten StatusChange furs gegebene Gesuch und gibt ihn zurueck
	 */
	@Nullable
	AntragStatusHistory findLastStatusChange(@Nonnull Gesuch gesuch);

	/**
	 * Entfernt alle AntrasStatusHistory Objekte vom gegebenen Gesuch
	 */
	void removeAllAntragStatusHistoryFromGesuch(@Nonnull Gesuch gesuch);

	/**
	 * Gibt eine Collection aller StatusHistory Objekte des uebergebenen Gesuchs zurueck-
	 */
	@Nonnull
	Collection<AntragStatusHistory> findAllAntragStatusHistoryByGesuch(@Nonnull Gesuch gesuch);

	/**
	 * Gibt eine Collection aller StatusHistory Objekte fuer den uebergebenen Fall in der uebergebenen
	 * Gesuchsperiode zurueck.
	 */
	@Nonnull
	Collection<AntragStatusHistory> findAllAntragStatusHistoryByGPFall(@Nonnull Gesuchsperiode gesuchsperiode,
		@Nonnull Fall fall);

	/**
	 * Prueft dass die letzte Statusaenderung auf BESCHWERDE_HAENGIG war und dann gibt die vorletzte Statusaenderung zurueck.
	 */
	@Nonnull
	AntragStatusHistory findLastStatusChangeBeforeBeschwerde(@Nonnull Gesuch gesuch);
}

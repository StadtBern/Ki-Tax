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

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;

/**
 * Service zum Verwalten von Mahnungen
 */
public interface MahnungService {

	/**
	 * Erstellt eine neue Mahnung in der DB, falls der key noch nicht existiert
	 */
	@Nonnull
	Mahnung createMahnung(@Nonnull Mahnung mahnung);

	/**
	 * Gibt die Mahnung mit der uebergebenen Id zurueck.
	 */
	@Nonnull
	Optional<Mahnung> findMahnung(@Nonnull String mahnungId);

	/**
	 * Gibt alle (aktiven und vergangenen) Mahnungen fuer das uebergebene Gesuch zurueck
	 */
	@Nonnull
	Collection<Mahnung> findMahnungenForGesuch(@Nonnull Gesuch gesuch);

	/**
	 * Setzt den Status zurueck auf "in Bearbeitung". Setzt die offenen Mahnungen auf inaktiv.
	 */
	@Nonnull
	Gesuch mahnlaufBeenden(@Nonnull Gesuch gesuch);

	/**
	 * Generiert den Vorschlag für die Bemerkungen aus den fehlenden Dokumenten.
	 */
	@Nonnull
	String getInitialeBemerkungen(@Nonnull Gesuch gesuch);

	/**
	 * Ueberprueft fuer alle aktiven Mahnungen, ob deren Ablauffrist eingetreten ist
	 */
	void fristAblaufTimer();

	/**
	 * Gibt die (einzige aktive erstmahnung zurueck)
	 */
	Optional<Mahnung> findAktiveErstMahnung(Gesuch gesuch);

	/**
	 * Entfernt alle Mahnungen vom gegebenen Gesuch
	 */
	void removeAllMahnungenFromGesuch(Gesuch gesuch);
}

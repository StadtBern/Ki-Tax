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
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;

/**
 * Service zum Verwalten von Gesuchstellern
 */
public interface GesuchstellerService {

	/**
	 * Aktualisiert die Gesuchsteller in der DB.
	 *
	 * @param gesuchsteller Die Gesuchsteller als DTO
	 * @param gsNumber Die Gesuchersteller-Nummer
	 */
	@Nonnull
	GesuchstellerContainer saveGesuchsteller(@Nonnull GesuchstellerContainer gesuchsteller, final Gesuch gesuch, Integer gsNumber, boolean umzug);

	/**
	 * @param id PK (id) der Gesuchsteller
	 * @return Gesuchsteller mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<GesuchstellerContainer> findGesuchsteller(@Nonnull String id);

	/**
	 * @return Liste aller Gesuchsteller aus der DB
	 */
	@Nonnull
	Collection<GesuchstellerContainer> getAllGesuchsteller();

	/**
	 * entfernt eine Gesuchsteller aus der Databse
	 *
	 * @param gesuchsteller Gesuchsteller zu entfernen
	 */
	void removeGesuchsteller(@Nonnull GesuchstellerContainer gesuchsteller);

	/**
	 * Sucht nach einem Gesuch an dem ein Gesuchsteller mit der uebergebenen ID angehaengt ist und gibt es zurueck
	 * Achtung hier wird keine authorisierung geprueft nicht direkt nach aussen zugaenglich machen
	 *
	 * @param gesuchstellerContainerID die Gesuchsteller ID deren Parentgesuch gefunden werden soll
	 * @return Das Gesuch an dem der Gesuchsteller angehaengt ist
	 */
	@Nullable
	Gesuch findGesuchOfGesuchsteller(@Nonnull String gesuchstellerContainerID);
}

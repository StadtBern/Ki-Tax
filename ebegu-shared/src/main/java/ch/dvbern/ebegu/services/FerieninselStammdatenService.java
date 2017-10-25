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

import ch.dvbern.ebegu.entities.FerieninselStammdaten;
import ch.dvbern.ebegu.enums.Ferienname;

/**
 * Service zum Verwalten von Ferieninsel-Stammdaten
 */
public interface FerieninselStammdatenService {

	/**
	 * Erstellt ein neues FerieninselStammdaten-Objekt in der DB, falls der key noch nicht existiert
	 */
	@Nonnull
	FerieninselStammdaten saveFerieninselStammdaten(@Nonnull FerieninselStammdaten ferieninselStammdaten);

	/**
	 * Sucht das FerieninselStammdaten-Objekt mit der uebergebenen Id
	 */
	@Nonnull
	Optional<FerieninselStammdaten> findFerieninselStammdaten(@Nonnull String ferieninselStammdatenId);

	/**
	 * Gibt alle existierenden FerieninselStammdaten-Objekte (aller Gesuchsperioden) zurueck.
	 */
	@Nonnull
	Collection<FerieninselStammdaten> getAllFerieninselStammdaten();

	/**
	 * Gibt alle FerieninselStammdaten-Objekte fuer die uebergebene Gesuchsperiode zurueck.
	 */
	@Nonnull
	Collection<FerieninselStammdaten> findFerieninselStammdatenForGesuchsperiode(@Nonnull String gesuchsperiodeId);

	/**
	 * Gibt alle FerieninselStammdaten-Objekte fuer die uebergebene Gesuchsperiode und Ferien zurueck.
	 */
	@Nonnull
	Optional<FerieninselStammdaten> findFerieninselStammdatenForGesuchsperiodeAndFerienname(@Nonnull String gesuchsperiodeId, @Nonnull Ferienname ferienname);

	/**
	 * Loescht das uebergebene FerieninselStammdaten-Objekt
	 */
	void removeFerieninselStammdaten(@Nonnull String ferieninselStammdatenId);

}

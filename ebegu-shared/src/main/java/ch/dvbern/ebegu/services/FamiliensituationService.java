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

import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.FamiliensituationContainer;
import ch.dvbern.ebegu.entities.Gesuch;

/**
 * Service zum Verwalten von Familiensituation
 */
public interface FamiliensituationService {

	/**
	 * Aktualisiert idn Familiensituation in der DB oder erstellt sie wenn sie noch nicht existiert
	 * @param loadedFamiliensituation dies ist der bisher gespeicherte FamiliensituationJA Wert aus der DB
	 */
	FamiliensituationContainer saveFamiliensituation(Gesuch gesuch, FamiliensituationContainer familiensituationContainer, Familiensituation loadedFamiliensituation);

	/**
	 * @param key PK (id) der Familiensituation
	 * @return Familiensituation mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<FamiliensituationContainer> findFamiliensituation(@Nonnull String key);

	/**
	 * Gibt alle existierenden Familiensituationen zurueck.
	 *
	 * @return Liste aller Familiensituationen aus der DB
	 */
	@Nonnull
	Collection<FamiliensituationContainer> getAllFamiliensituatione();

	/**
	 * entfernt eine Familiensituation aus der Database
	 *
	 * @param familiensituation die Familiensituation als DTO
	 */
	void removeFamiliensituation(@Nonnull FamiliensituationContainer familiensituation);

}

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

import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.entities.Gesuch;

/**
 * Service zum Verwalten von EinkommensverschlechterungInfoContainer
 */
public interface EinkommensverschlechterungInfoService {

	/**
	 * Erstellt eine neue EinkommensverschlechterungInfoContainer in der DB, falls der key noch nicht existiert
	 *
	 * @param einkommensverschlechterungInfo die EinkommensverschlechterungInfoContainer als DTO
	 * @return die gespeicherte EinkommensverschlechterungInfoContainer
	 */
	@Nonnull
	Optional<EinkommensverschlechterungInfoContainer> createEinkommensverschlechterungInfo(
		@Nonnull EinkommensverschlechterungInfoContainer einkommensverschlechterungInfo);

	/**
	 * Aktualisiert idn EinkommensverschlechterungInfoContainer in der DB
	 */
	@Nonnull
	EinkommensverschlechterungInfoContainer updateEinkommensVerschlechterungInfoAndGesuch(Gesuch gesuch, EinkommensverschlechterungInfoContainer oldEVData,
																				 EinkommensverschlechterungInfoContainer convertedEkvi);

	/**
	 * @param key PK (id) der EinkommensverschlechterungInfoContainer
	 * @return EinkommensverschlechterungInfoContainer mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<EinkommensverschlechterungInfoContainer> findEinkommensverschlechterungInfo(@Nonnull String key);

	/**
	 * Gibt alle existierenden EinkommensverschlechterungInfoen zurueck.
	 * @return Liste aller EinkommensverschlechterungInfoen aus der DB
	 */
	@Nonnull
	Collection<EinkommensverschlechterungInfoContainer> getAllEinkommensverschlechterungInfo();

	/**
	 * entfernt eine EinkommensverschlechterungInfoContainer aus der Database
	 * @param einkommensverschlechterungInfo die EinkommensverschlechterungInfoContainer als DTO
	 */
	void removeEinkommensverschlechterungInfo(@Nonnull EinkommensverschlechterungInfoContainer einkommensverschlechterungInfo);

}

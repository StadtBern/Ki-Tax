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

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.Einkommensverschlechterung;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;
import ch.dvbern.ebegu.entities.Gesuch;

/**
 * Service zum Verwalten von EinkommensverschlechterungContainerService
 */
public interface EinkommensverschlechterungService {

	/**
	 * Aktualisiert idn EinkommensverschlechterungContainer in der DB
	 *
	 * @param einkommensverschlechterungContainer die EinkommensverschlechterungContainer als DTO
	 * @param gesuchId die ID des Gesuchs dessen Status modifiziert werden soll
	 * @return Die aktualisierte EinkommensverschlechterungContainer
	 */
	@Nonnull
	EinkommensverschlechterungContainer saveEinkommensverschlechterungContainer(@Nonnull EinkommensverschlechterungContainer einkommensverschlechterungContainer, String gesuchId);

	/**
	 * @param key PK (id) der EinkommensverschlechterungContainer
	 * @return EinkommensverschlechterungContainer mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<EinkommensverschlechterungContainer> findEinkommensverschlechterungContainer(@Nonnull String key);

	/**
	 * Gibt alle existierenden EinkommensverschlechterungContainer zurueck.
	 *
	 * @return Liste aller EinkommensverschlechterungContainer aus der DB
	 */
	@Nonnull
	Collection<EinkommensverschlechterungContainer> getAllEinkommensverschlechterungContainer();

	/**
	 * entfernt eine EinkommensverschlechterungContaine aus der Database
	 *
	 * @param einkommensverschlechterungContainer die EinkommensverschlechterungContainer als DTO
	 */
	void removeEinkommensverschlechterungContainer(@Nonnull EinkommensverschlechterungContainer einkommensverschlechterungContainer);

	/**
	 * Removes the given Einkommensverschlechterung
	 */
	void removeEinkommensverschlechterung(@Nonnull Einkommensverschlechterung einkommensverschlechterung);

	/**
	 * Berechnet die Einkomensverschlechterung beider Gesuchsteller für das entsprechende BasisJahr 1 oder 2
	 */
	@Nonnull
	FinanzielleSituationResultateDTO calculateResultate(@Nonnull Gesuch gesuch, int basisJahrPlus);

	/**
	 * Removes all Einkommensverschlechterungen of the given year for the given Gesuch. The year is 1 or 2 and will
	 * be the difference between the BasisJahr and the one to remove.
	 * @param gesuch the Gesuch
	 * @param yearPlus 1 or 2. any other value won't do anything and will return false
	 */
	boolean removeAllEKVOfGesuch(@Nonnull Gesuch gesuch, int yearPlus);
}

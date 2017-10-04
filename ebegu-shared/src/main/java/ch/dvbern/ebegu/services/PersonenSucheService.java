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

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.dto.personensuche.EWKResultat;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.errors.PersonenSucheServiceBusinessException;
import ch.dvbern.ebegu.errors.PersonenSucheServiceException;

/**
 * Service für die Personensuche
 */
public interface PersonenSucheService {

	/**
	 * Sucht den uebergebenen Gesuchsteller im EWK.
	 * Falls die Suche eindeutig ist, wird die ewkPersonenId auf dem Gesuchsteller gesetzt
	 */
	@Nonnull
	EWKResultat suchePerson(@Nonnull Gesuchsteller gesuchsteller) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException;

	/**
	 * Verknuepft die uebergebene EWK-Person mit dem Gesuchsteller: Setzt die ewkPersonenId auf
	 * dem Gesuchsteller
	 */
	@Nonnull
	Gesuchsteller selectPerson(@Nonnull Gesuchsteller gesuchsteller, @Nonnull String ewkPersonID);

	/**
	 * Sucht eine Person im EWK, anhand eindeutiger PersonenID
	 */
	@Nonnull
	EWKResultat suchePerson(@Nonnull String id) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException;

	/**
	 * Sucht eine Person im EWK, mit allen Angaben
	 */
	@Nonnull
	EWKResultat suchePerson(@Nonnull String name, @Nonnull String vorname, @Nonnull LocalDate geburtsdatum, @Nonnull Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException;

	/**
	 * Sucht eine Person im EWK, mit allen Angaben ausser Vorname
	 */
	@Nonnull
	EWKResultat suchePerson(@Nonnull String name, @Nonnull LocalDate geburtsdatum, @Nonnull Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException;
}



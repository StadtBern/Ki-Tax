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
import javax.persistence.EntityManager;

import ch.dvbern.ebegu.entities.EbeguVorlage;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Vorlage;
import ch.dvbern.ebegu.enums.EbeguVorlageKey;

/**
 * Service zum Verwalten von EbeguVorlagen
 */
public interface EbeguVorlageService {

	/**
	 * Speichert den EbeguVorlage neu in der DB falls der Key noch nicht existiert. Sonst wird das existierende EbeguVorlage aktualisiert
	 *
	 * @param ebeguVorlage Das EbeguVorlage als DTO
	 */
	@Nonnull
	EbeguVorlage saveEbeguVorlage(@Nonnull EbeguVorlage ebeguVorlage);

	/**
	 * Gibt eine optionale Vorlage fuer den uebergebenen Key zurueck, welche im uebergebenen Zeitraum gueltig ist.
	 */
	@Nonnull
	Optional<EbeguVorlage> getEbeguVorlageByDatesAndKey(LocalDate abDate, LocalDate bisDate, EbeguVorlageKey ebeguVorlageKey);

	/**
	 * Gibt eine optionale Vorlage fuer den uebergebenen Key zurueck, welche im uebergebenen Zeitraum gueltig ist.
	 */
	@Nonnull
	Optional<EbeguVorlage> getEbeguVorlageByDatesAndKey(LocalDate abDate, LocalDate bisDate, EbeguVorlageKey ebeguVorlageKey, EntityManager em);

	/**
	 * Gibt alle Vorlagen einer Gesuchsperiode zurueck.
	 */
	@Nonnull
	List<EbeguVorlage> getALLEbeguVorlageByGesuchsperiode(Gesuchsperiode gesuchsperiode);

	/**
	 * Aktualisiert die EbeguVorlage in der DB
	 *
	 * @param ebeguVorlage Die EbeguVorlage als DTO
	 */
	@Nullable
	EbeguVorlage updateEbeguVorlage(@Nonnull EbeguVorlage ebeguVorlage);

	/**
	 * Entfernt die Vorlage mit der uebergebenen Id aus der Datenbank
	 */
	void removeVorlage(@Nonnull String id);

	/**
	 * Sucht die Vorlage mit der uebergebenen Id.
	 */
	@Nonnull
	Optional<EbeguVorlage> findById(@Nonnull final String id);

	/**
	 * Gibt alle Vorlagen zurueck, welche am Stichtag gueltig sind.
	 *
	 * @param proGesuchsperiode true, wenn nur Gesuchsperioden-abhaengige Vorlagen gesucht werden sollen
	 */
	@Nonnull
	Collection<EbeguVorlage> getALLEbeguVorlageByDate(@Nonnull LocalDate date, boolean proGesuchsperiode);

	/**
	 * Kopiert alle Vorlagen einer Gesuchsperiode zur naechsten (uebergebenen) Gesuchsperiode
	 *
	 * @param gesuchsperiodeToCopyTo Die neue Gesuchsperiode
	 */
	void copyEbeguVorlageListToNewGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiodeToCopyTo);

	/**
	 * Gibt fuer den eingeloggten Benutzer das richtige Benutzerhandbuch zurueck.
	 */
	Vorlage getBenutzerhandbuch();
}

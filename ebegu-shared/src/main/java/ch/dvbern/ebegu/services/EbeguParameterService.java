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
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.EbeguParameterKey;

/**
 * Service zum Verwalten von zeitabhängigen E-BEGU-Parametern.
 */
public interface EbeguParameterService {

	/**
	 * Speichert den Parameter neu in der DB falls der Key noch nicht existiert. Ansonsten wird ein neuer Parameter mit
	 * diesem Key erstellt
	 */
	@Nonnull
	EbeguParameter saveEbeguParameter(@Nonnull EbeguParameter ebeguParameter);

	/**
	 * Gibt den Parameter mit diesem Key zurück oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<EbeguParameter> findEbeguParameter(@Nonnull String id);

	/**
	 * Gibt alle Parameter zurück
	 */
	@Nonnull
	Collection<EbeguParameter> getAllEbeguParameter();

	/**
	 * Entfernt einen Parameter aus der Datenbank
	 */
	void removeEbeguParameter(@Nonnull String id);

	/**
	 * Sucht alle am Stichtag gueltigen Ebegu-Parameter
	 */
	@Nonnull
	Collection<EbeguParameter> getAllEbeguParameterByDate(@Nonnull LocalDate date);

	/**
	 * Sucht alle für die Gesuchsperiode gueltigen Ebegu-Parameter. Falls noch keine vorhanden sind, werden sie
	 * aus der letzten Gesuchsperiode kopiert
	 */
	@Nonnull
	Collection<EbeguParameter> getEbeguParameterByGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode);

	/**
	 * Sucht alle für das Jahr gültigen Ebegu-Parameter. Falls ncoh keine vorhanden sind, werden sie vom
	 * Vorjahr kopiert.
	 */
	@Nonnull
	Collection<EbeguParameter> getEbeguParametersByJahr(@Nonnull Integer jahr);

	/**
	 * Gibt alle Jahresabh Parameter zuruek fuer die verschiendenen Jahre
	 */
	@Nonnull
	Collection<EbeguParameter> getJahresabhParameter();

	/**
	 * Sucht den am Stichtag gueltigen Ebegu-Parameter mit dem übergebenen Key.
	 */
	@Nonnull
	Optional<EbeguParameter> getEbeguParameterByKeyAndDate(@Nonnull EbeguParameterKey key, @Nonnull LocalDate date);

	/**
	 * Sucht den am Stichtag gueltigen Ebegu-Parameter mit dem übergebenen Key.
	 * Ein externes EntityManager wird uebergeben. Damit vermeiden wir Fehler  ConcurrentModificationException in hibernate
	 */
	@Nonnull
	Optional<EbeguParameter> getEbeguParameterByKeyAndDate(@Nonnull EbeguParameterKey key, @Nonnull LocalDate date, final EntityManager em);

	/**
	 * Copies all ebeguParameters from the last Gesuchperiode into the given one
	 */
	void copyEbeguParameterListToNewGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode);

	/**
	 * searches all parameters that were valid at the first of january of the jahr-1. Then go through those parameters and if
	 * the parameter is set "per Gesuchsperiode" then copy it from the previous year and set the daterange for the current year
	 */
	void createEbeguParameterListForJahr(@Nonnull Integer jahr);


	/**
	 * Gleiches resultat wie getEbeguParameterByGesuchsperiode aber als Map
	 */
	Map<EbeguParameterKey, EbeguParameter> getEbeguParameterByGesuchsperiodeAsMap(@Nonnull Gesuchsperiode gesuchsperiode);
}

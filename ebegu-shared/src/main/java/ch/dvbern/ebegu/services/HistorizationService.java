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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.AbstractEntity;

/**
 * Service zum Verwalten von Historization elementen
 */
public interface HistorizationService {

	/**
	 * Gibt eine Liste mit allen Revisions fuer einen Objekt der entity mit entityname und mit entityid zurueck.
	 *
	 * @param entityName Klassename der Entity
	 * @param entityId ID Nummer der Entity
	 * @return Eine Liste mit Object-Arrays. Jedes Array enthaelt ein DefaultRevisionEntity, ein RevisionType und eine AbstractEntity
	 */
	@Nullable
	List<Object[]> getAllRevisionsById(@Nonnull String entityName, @Nonnull String entityId);

	/**
	 * Gibt alle Objekte der Art entityName auf einer bestimmten Revision zurueck. Das heisst der Zustand
	 * einer "Tabelle" in Revision x
	 *
	 * @param entityName Klassename der Entity
	 * @param revision Revision
	 * @return Eine Liste mit allen AbstractEntities von der eingegebenen .Revision
	 */
	@Nullable
	List<AbstractEntity> getAllEntitiesByRevision(@Nonnull String entityName, @Nonnull Integer revision);

}

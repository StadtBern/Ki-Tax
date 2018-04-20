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

package ch.dvbern.ebegu.dto.suchfilter.lucene;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface welches von allen Indizierten Entities implementiert wird. Auf diese weise koennen diese auf einen
 * gemeinsamen nenner gebracht werden
 */
public interface Searchable {

	/**
	 * @return Liefert die Unique-ID dieser Entity.
	 */
	@Nonnull
	String getSearchResultId();

	/**
	 * @return Dieser Text wird in den Suchergebnissen zur Volltextsuche angezeigt.
	 */
	@Nonnull
	String getSearchResultSummary();

	/**
	 * @return Dieser Text wird in den Suchergebnissen zur Volltextsuche als Zusatz angezeigt.
	 */
	@Nullable
	String getSearchResultAdditionalInformation();

	/**
	 * @return Liefert die Unique_ID des Gesuchs zu dem dieses Entity gehoert. Kann null sein wenn es keine direkte verknuepfung gibt. In diesem Fall muss die gesuchID per query emittelt werden :(
	 */
	@Nullable
	String getOwningGesuchId();

	/**
	 * @return Liefert die Unique_ID des Falls zu dem dieses Entity gehoert. Kann null sein wenn es keine direkte
	 * verknuepfung gibt. In diesem Fall muss die fallID per query emittelt werden :(
	 */
	@Nullable
	String getOwningFallId();

}

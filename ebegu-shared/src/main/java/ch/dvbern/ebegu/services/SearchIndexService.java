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

import ch.dvbern.ebegu.dto.suchfilter.lucene.QuickSearchResultDTO;
import ch.dvbern.ebegu.dto.suchfilter.lucene.SearchFilter;

/**
 * Service to perform a search in the search index (hibernate-search, lucene)
 */
public interface SearchIndexService {

	void rebuildSearchIndex();

	/**
	 * Perform a search against all the indizes specified in the SearchFilter Objects
	 *
	 * @param searchText this is the text to search (will be tokenized and normalized by an analyser).
	 * All terms will be wildcarded automatically
	 * @param filters used to set the searched index and fields
	 * @return List of results. Note that the list only contains results that are visible by the current user
	 */
	@Nonnull
	QuickSearchResultDTO search(@Nonnull String searchText, @Nonnull List<SearchFilter> filters);

	/**
	 * Perform a search over all indexed fields with the given searchString
	 *
	 * @param limitResult if true the actual loaded results will never contain more elements than the specified limit allows
	 * @return results contains a list of potential matches as well as the list of loaded matches
	 */
	QuickSearchResultDTO quicksearch(String searchStringParam, boolean limitResult);
}

package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.dto.suchfilter.lucene.QuickSearchResultDTO;
import ch.dvbern.ebegu.dto.suchfilter.lucene.SearchFilter;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Service to perform a search in the search index (hibernate-search, lucene)
 */
public interface SearchIndexService {

	void rebuildSearchIndex();


	/**
	 *
	 * Perform a search against all the indizes specified in the SearchFilter Objects
	 * @param searchText this is the text to search (will be tokenized and normalized by an analyser).
	 *                      All terms will be wildcarded automatically
	 * @param filters used to set the searched index and fields
	 * @return List of results. Note that the list only contains results that are visible by the current user
	 */
	@Nonnull
	QuickSearchResultDTO search(@Nonnull String searchText, @Nonnull List<SearchFilter> filters);

	/**
	 * Perform a search over all indexed fields with the given searchString
	 * @param limitResult if true the actual loaded results will never contain more elements than the specified limit allows
	 * @return results contains a list of potential matches as well as the list of loaded matches
	 */
	QuickSearchResultDTO quicksearch(String searchStringParam, boolean limitResult);
}

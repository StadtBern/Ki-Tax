package ch.dvbern.ebegu.dto.suchfilter.lucene;

import ch.dvbern.ebegu.dto.JaxAntragDTO;
import com.google.common.collect.ArrayListMultimap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DTO to pass around a result that was found in the Lucene index
 */
public class QuickSearchResultDTO implements Serializable {

	private static final long serialVersionUID = -4426951516105536674L;
	private List<SearchResultEntryDTO> resultEntities = new ArrayList<>();

	/**
	 * This number is the sum of the combined lucene matches. It may not be equal to the size of the actually loaded {@link SearchResultEntryDTO} list
	 */
	private int numberOfResults;

	public QuickSearchResultDTO() {
	}


	public QuickSearchResultDTO(List<SearchResultEntryDTO> resultEntities, int numberOfResults) {
		this.resultEntities = resultEntities;
		this.numberOfResults = numberOfResults;
	}

	public List<SearchResultEntryDTO> getResultEntities() {
		return resultEntities;
	}

	public int getNumberOfResults() {
		return numberOfResults;
	}

	public void setNumberOfResults(int numberOfResults) {
		this.numberOfResults = numberOfResults;
	}

	/**
	 * merges this result object with the passed object by adding the number of results and adding all entries in the entry list
	 * @param subResult
	 */
	public void addSubResult(QuickSearchResultDTO subResult) {
		resultEntities.addAll(subResult.getResultEntities());
		numberOfResults += subResult.getNumberOfResults();
	}


	/**
	 * this helper method removes all but one resultEntry for a given Gesuch. It does this by identifying the
	 * Gesuche based on their ids and just taking the first.
	 * Also the numberOfResults will be reduced by the number of omitted Gesuche.
	 *
	 * @param quickSearch
	 * @return Returns a NEW {@link QuickSearchResultDTO}
	 */
	public static QuickSearchResultDTO reduceToSingleEntyPerAntrag(QuickSearchResultDTO quickSearch) {
		ArrayListMultimap<String, SearchResultEntryDTO> antragIdToEntryMultimap = ArrayListMultimap.create();
		quickSearch.getResultEntities()
			.forEach(searchResultEntryDTO -> {
				JaxAntragDTO antragDTO = searchResultEntryDTO.getAntragDTO();
				if (antragDTO != null) {
					antragIdToEntryMultimap.put(antragDTO.getAntragId(), searchResultEntryDTO);
				}
			});

		List<SearchResultEntryDTO> mergedEntries = new ArrayList<>();
		//wir nehmen mal nur das erste resultat fuer ein bestimmtes gesuch
		for (String gesuchId : antragIdToEntryMultimap.keySet()) {
			Optional<SearchResultEntryDTO> first = antragIdToEntryMultimap.get(gesuchId).stream().findFirst();
			first.ifPresent(mergedEntries::add);
		}
		int numOfOmittedAntraege = quickSearch.getResultEntities().size() - mergedEntries.size();
		return new QuickSearchResultDTO(mergedEntries, quickSearch.numberOfResults - numOfOmittedAntraege);

	}
}

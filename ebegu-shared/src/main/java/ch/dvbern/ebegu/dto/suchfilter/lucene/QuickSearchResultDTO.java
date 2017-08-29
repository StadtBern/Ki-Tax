package ch.dvbern.ebegu.dto.suchfilter.lucene;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.google.common.collect.ArrayListMultimap;

import ch.dvbern.ebegu.dto.JaxAbstractAntragDTO;
import ch.dvbern.ebegu.dto.JaxAntragDTO;
import ch.dvbern.ebegu.dto.JaxFallAntragDTO;
import ch.dvbern.ebegu.entities.Fall;

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
	 * It adds the given fall to the existing resultlist, but only if the fallID is not already present in an existing result
	 */
	public void addSubResultFall(SearchResultEntryDTO resultFall, @NotNull Fall fall) {
		if (!fallAlreadyInResultEntities(fall.getId())) {
			addResult(createFakeAntragDTO(resultFall, fall));
		}
	}

	/**
	 * Since for some results we do not really have a visible Antrag yet we create a fake one that compriese essentially
	 * the fall id and besitzer name
	 */
	private SearchResultEntryDTO createFakeAntragDTO(@NotNull SearchResultEntryDTO searchResultEntryDTO, @NotNull Fall fall) {
		if (searchResultEntryDTO.getAntragDTO() == null) {
			final JaxFallAntragDTO antragDTO = new JaxFallAntragDTO();
			antragDTO.setFallID(fall.getId());
			antragDTO.setFallNummer(fall.getFallNummer());
			if (fall.getBesitzer() != null) {
				antragDTO.setFamilienName(fall.getBesitzer().getFullName());
			}
			searchResultEntryDTO.setAntragDTO(antragDTO);
		}
		return searchResultEntryDTO;
	}

	/**
	 * Adds a result to the list
	 */
	public void addResult(SearchResultEntryDTO result) {
		if (result != null) {
			this.resultEntities.add(result);
			this.numberOfResults++;
		}
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
				JaxAbstractAntragDTO antragDTO = searchResultEntryDTO.getAntragDTO();
				if (antragDTO instanceof JaxAntragDTO) {
					antragIdToEntryMultimap.put(((JaxAntragDTO)antragDTO).getAntragId(), searchResultEntryDTO);
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

	/**
	 * Checks whether the given fall has already been found and added to the list
	 */
	private boolean fallAlreadyInResultEntities(@NotNull String fallID) {
		return resultEntities.stream()
			.filter(searchResultEntryDTO -> fallID.equalsIgnoreCase(searchResultEntryDTO.getFallID()))
			.findAny()
			.isPresent();
	}
}

package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.dto.JaxAntragDTO;
import ch.dvbern.ebegu.dto.suchfilter.lucene.QuickSearchResultDTO;
import ch.dvbern.ebegu.dto.suchfilter.lucene.SearchEntityType;
import ch.dvbern.ebegu.dto.suchfilter.lucene.SearchFilter;
import ch.dvbern.ebegu.dto.suchfilter.lucene.SearchResultEntryDTO;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.services.*;
import com.google.common.collect.ArrayListMultimap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.directory.SearchResult;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Path("search")
@Stateless
@Api
public class SearchIndexResource {


	@Inject
	private SearchIndexService searchIndexService;

	@Inject
	private GesuchstellerService gesuchstellerServiceBean;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private JaxBConverter converter;

	@Inject
	private BooleanAuthorizer authorizer;

	@Inject
	private PrincipalBean principalBean;

	@POST()
	@Path("/parameterized/query/{searchString}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Perform a search for the searchString in the indexes determined by the filter objects", response = SearchResult.class)
	public Response searchQuery(@Nonnull @PathParam("searchString") String searchStringParam, List<SearchFilter> filters) {
		QuickSearchResultDTO searchResult = searchIndexService.search(searchStringParam, filters);
		return Response.ok(searchResult).build();
	}

	/**
	 * Searches for the passed searchString in all lucene indizes and returns visible result objects.
	 * The search is limited to a small number of results in order to make the search as quick as possible.
	 * In case there are no results left after filtering  objects that are not visible for the current user
	 * the search is performed again with no max result limit.
	 */
	@GET
	@Path("/quicksearch/{searchString}")
	@ApiOperation(value = "Perform a search for the searchString in all indizes returning only a small number of results", response = SearchResult.class)
	public Response quicksearch(@Context HttpServletRequest request, @Nonnull @PathParam("searchString") String searchStringParam) {
		Validate.notEmpty(searchStringParam);
		QuickSearchResultDTO search = searchIndexService.quicksearch(searchStringParam, true);
		QuickSearchResultDTO quickSearchResultDTO = convertQuicksearchResultToDTO(search);
		//if no result is returened but we know that there are result we assume that the loaded results were not visible
		//for the current user. In that case  try the unlimited search so the user gets his visible result
		if (quickSearchResultDTO.getResultEntities().isEmpty() && quickSearchResultDTO.getNumberOfResults() > 0) {
			QuickSearchResultDTO unlimitedSearch = searchIndexService.quicksearch(searchStringParam, false);
			quickSearchResultDTO = convertQuicksearchResultToDTO(unlimitedSearch);
		}

		return Response.ok(quickSearchResultDTO).build();
	}

	/**
	 * Searches for the passed searchString in all lucene indizes and returns all visible result objects.
	 * The search is limited to a small number of results. In case there are no results left after filtering
	 * objects that are not visible for the current user then the search is performed again with no max result limit.
	 */
	@GET
	@Path("/globalsearch/{searchString}")
	@ApiOperation(value = "Perform a search for the searchString in all indizes without a result limit", response = SearchResult.class)
	public Response globalsearch(@Context HttpServletRequest request, @Nonnull @PathParam("searchString") String searchStringParam) {
		Validate.notEmpty(searchStringParam);
		QuickSearchResultDTO search = searchIndexService.quicksearch(searchStringParam, false);
		QuickSearchResultDTO quickSearchResultDTO = convertQuicksearchResultToDTO(search);

		return Response.ok(quickSearchResultDTO).build();
	}

	/**
	 * Helper that reduces and normalizes the result from the Lucene search index. Duplcate Gesuche that matched in different indizes
	 * are removed as well as Gesuche that are invisible for the current user. For every Fall only the newest Gesuch that was in the
	 * original QuckSearchResultDTO will be rturned
	 */
	private QuickSearchResultDTO convertQuicksearchResultToDTO(QuickSearchResultDTO quickSearch) {
		List<Gesuch> allowedGesuche = filterUnreadableGesuche(quickSearch); //nur erlaubte Gesuche
		Map<String, Gesuch> gesucheToShow = groupByFallAndSelectNewestAntrag(allowedGesuche); //nur neustes gesuch
		QuickSearchResultDTO filteredQuickSearch = mergeAllowedGesucheWithQuickSearchResult(quickSearch, gesucheToShow);//search result anpassen so dass nur noch sichtbare Antrage drin sind und Antragdtos gesetzt sind
		return QuickSearchResultDTO.reduceToSingleEntyPerAntrag(filteredQuickSearch); // Gesuche die in mehreren Indizes gefunden wurden auslassen so dass jedes gesuch nur 1 mal drin ist

	}

	private List<Gesuch> filterUnreadableGesuche(QuickSearchResultDTO search) {
		//fuer suchresultate ungleich GesuchstellerContainer kennen wir die id des gesuchs schon
		List<String> gesuchIds = search.getResultEntities().stream()
			.filter(searchResultEntryDTO -> searchResultEntryDTO.getEntity() != SearchEntityType.GESUCHSTELLER_CONTAINER)
			.map(SearchResultEntryDTO::getGesuchID).collect(Collectors.toList());
		List<Gesuch> readableGesuche = gesuchService.findReadableGesuche(gesuchIds);

		//fuer die suchrestultate die im GesuchstellerIndex gematched haben muessen wir das Gesuch noch ermitteln (N-SQL Abfragen)
		List<Gesuch> gesucheFromGesuchstellermatch = search.getResultEntities().stream()
			.filter(searchResultEntryDTO -> searchResultEntryDTO.getEntity() == SearchEntityType.GESUCHSTELLER_CONTAINER)
			.map((searchResultEntryDTO) -> {
				Gesuch foundGesuch = gesuchstellerServiceBean.findGesuchOfGesuchsteller(searchResultEntryDTO.getResultId());
				searchResultEntryDTO.setGesuchID(foundGesuch != null ? foundGesuch.getId() : null);
				return foundGesuch;
			})
			.filter(gesuch -> this.authorizer.hasReadAuthorization(gesuch))
			.collect(Collectors.toList());

		List<Gesuch> allGesuche = new ArrayList<>(readableGesuche);
		allGesuche.addAll(gesucheFromGesuchstellermatch);
		return allGesuche;
	}

	private Map<String, Gesuch> groupByFallAndSelectNewestAntrag(List<Gesuch> allGesuche) {
		ArrayListMultimap<Fall, Gesuch> fallToAntragMultimap = ArrayListMultimap.create();
		allGesuche.forEach(gesuch -> fallToAntragMultimap.put(gesuch.getFall(), gesuch));
		// map erstellen in der nur noch das gesuch mit der hoechsten laufnummer drin ist
		Map<String, Gesuch> gesuchMap = new HashMap<>();
		for (Fall fall : fallToAntragMultimap.keySet()) {
			List<Gesuch> antraege = fallToAntragMultimap.get(fall);
			antraege.sort(Comparator.comparing(Gesuch::getLaufnummer));
			gesuchMap.put(antraege.get(0).getId(), antraege.get(0)); //nur neusten Antrag zurueckgeben
		}

		return gesuchMap;

	}

	/**
	 * macht einen Quervergleich zwischen den beiden Collections und behaelt nur die Resultate in
	 * QuickSearchResultDTO die wir in der Gesuchmap finden. Setzt zudem das AntragDTO inds Result
	 */
	private QuickSearchResultDTO mergeAllowedGesucheWithQuickSearchResult(QuickSearchResultDTO quickSearch, Map<String, Gesuch> gesucheToShow) {
		boolean isInstOrTraegerschaft = isCurrentUserInstitutionOrTraegerschaft();
		Collection<Institution> allowedInst = isInstOrTraegerschaft ? institutionService.getAllowedInstitutionenForCurrentBenutzer() : null;

		for (Iterator<SearchResultEntryDTO> iterator = quickSearch.getResultEntities().iterator(); iterator.hasNext(); ) {
			SearchResultEntryDTO searchEnry = iterator.next();
			Gesuch gesuch = gesucheToShow.get(searchEnry.getGesuchID());
			if (gesuch == null) {
				iterator.remove();
				quickSearch.setNumberOfResults(quickSearch.getNumberOfResults() - 1);
			} else {
				JaxAntragDTO jaxAntragDTO;
				if (isInstOrTraegerschaft) { //fuer institutionen und traegerschaften nur erlaubte inst mitgeben
					jaxAntragDTO = this.converter.gesuchToAntragDTO(gesuch, principalBean.getBenutzer().getRole(), allowedInst);
				} else{
					jaxAntragDTO = this.converter.gesuchToAntragDTO(gesuch);
				}
				searchEnry.setAntragDTO(jaxAntragDTO);
				String fullNameGS1 = gesuch.getGesuchsteller1() != null ? gesuch.getGesuchsteller1().extractFullName() : "";
				if (searchEnry.getAntragDTO() != null) {
					searchEnry.getAntragDTO().setFamilienName(fullNameGS1);
				}
			}
		}
		return quickSearch;
	}

	private boolean isCurrentUserInstitutionOrTraegerschaft() {
		UserRole userRole = principalBean.discoverMostPrivilegedRole();
		return UserRole.SACHBEARBEITER_INSTITUTION.equals(userRole) || UserRole.SACHBEARBEITER_TRAEGERSCHAFT.equals(userRole);
	}

}

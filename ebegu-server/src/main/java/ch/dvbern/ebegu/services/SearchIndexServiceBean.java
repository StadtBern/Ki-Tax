package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.dto.suchfilter.lucene.*;
import ch.dvbern.ebegu.entities.Sequence;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang.Validate;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static ch.dvbern.ebegu.enums.UserRoleName.*;

@Stateless
@RolesAllowed({SUPER_ADMIN, ADMIN})
public class SearchIndexServiceBean implements SearchIndexService {

	private static final Logger LOG = LoggerFactory.getLogger(SearchIndexServiceBean.class);


	@Nonnull
	private static final List<SearchFilter> SEARCH_FILTER_FOR_ALL_ENTITIES =
		Arrays.stream(SearchEntityType.values())
			.filter(SearchEntityType::isGlobalSearch)
			.map(searchEntityType -> new SearchFilter(searchEntityType))
			.collect(Collectors.toList());

	@Nonnull
	private static final List<SearchFilter> SEARCH_FILTER_FOR_ALL_ENTITIES_WITH_LIMIT =
		Arrays.stream(SearchEntityType.values())
			.filter(SearchEntityType::isGlobalSearch)
			.map(searchEntityType -> new SearchFilter(searchEntityType, Constants.MAX_LUCENE_QUICKSEARCH_RESULTS))
			.collect(Collectors.toList());

	private static final String WILDCARD = "*";

	@Inject
	private Persistence<Sequence> persistence;


	@Override
	public void rebuildSearchIndex() {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(persistence.getEntityManager());
		try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			LOG.error("Could not index data");
			throw new EbeguRuntimeException("rebuildSearchIndex", "Index konnte nicht erstellt werden", e, e.getMessage());
		}
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER, STEUERAMT, SCHULAMT})
	public QuickSearchResultDTO search(@Nonnull String searchText, @Nonnull List<SearchFilter> filters) {
		Validate.notNull(searchText, "searchText must be set");
		Validate.notNull(filters, "filters must be set");
		QuickSearchResultDTO result = new QuickSearchResultDTO();
		List<String> stringsToMatch = tokenizeAndAndAddWildcardToQuery(searchText);
		Validate.notNull(filters);
		for (SearchFilter filter : filters) {
			QuickSearchResultDTO subResult = searchInSingleIndex(stringsToMatch, filter);
			result.addSubResult(subResult);
		}
		return result;
	}

	/**
	 * Der uebergebene Searchtext wird hier mit einem Analyzer gesplittet und normalisiert. Zudem wird am Ende jedes erhaltenen
	 * Suchterms der wildcardmarker * eingefuegt.
	 * Es sollte drauf geachtet werden, dass der gleiche Analyzer verwendet wird mit dem jeweils auch der Index erzeugt wird.
	 * Wir fuehren diesen schritt manuell durch weil Hibernate-Search bei wildcard queries den analyzer NICHT anwendet
	 * vergl.doku (Wildcard queries do not apply the analyzer on the matching terms. Otherwise the risk of * or ? being mangled is too high.)
	 *
	 * @param searchText searchstring der tokenized werden soll
	 * @return Liste der normalizierten und um wildcards ergaenzten suchstrings
	 */
	private List<String> tokenizeAndAndAddWildcardToQuery(@Nonnull String searchText) {
		@SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
		Analyzer analyzer = new EBEGUGermanAnalyzer();
		List<String> tokenizedStrings = LuceneUtil.tokenizeString(new EBEGUGermanAnalyzer(), searchText);
		analyzer.close();
		return tokenizedStrings.stream().map(term -> term + WILDCARD).collect(Collectors.toList());
	}

	/**
	 * sucht im durch den SearchFilter spezifizierten Index nach dem searchText. Es wird nicht laaenger als 500ms gesucht.
	 */
	@Nonnull
	private QuickSearchResultDTO searchInSingleIndex(@Nonnull List<String> searchText, @Nonnull SearchFilter filter) {
		QuickSearchResultDTO result = new QuickSearchResultDTO();
		FullTextQuery query = buildLuceneQuery(searchText, filter);
		query.limitExecutionTimeTo(Constants.MAX_LUCENE_QUERY_RUNTIME, TimeUnit.MILLISECONDS); //laufzeit limitieren
		if (filter.getMaxResults() != null) { //allenfalls anzahl resultate limitieren
			query.setMaxResults(filter.getMaxResults());
		}
		@SuppressWarnings("unchecked")
		List<Searchable> results = query.getResultList();
		List<SearchResultEntryDTO> searchResultEntryDTOS = SearchResultEntryDTO.convertSearchResult(filter, results);
		result.getResultEntities().addAll(searchResultEntryDTOS);
		result.setNumberOfResults(query.getResultSize());
		return result;
	}


	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER, STEUERAMT, SCHULAMT})
	public QuickSearchResultDTO quicksearch(String searchStringParam, boolean limitResult) {

		List<SearchFilter> filterToUse = limitResult ? SEARCH_FILTER_FOR_ALL_ENTITIES_WITH_LIMIT : SEARCH_FILTER_FOR_ALL_ENTITIES;
		return this.search(searchStringParam, filterToUse);
	}


	//hibernate-search dsl is not well suited for programmatic queries which is why this code is kind of unwieldy.
	private FullTextQuery buildLuceneQuery(@Nonnull List<String> searchTermList, @Nonnull SearchFilter filter) {
		Class<Searchable> entityClass = filter.getSearchEntityType().getEntityClass();
		Validate.notNull(filter.getSearchEntityType());

		EntityManager em = persistence.getEntityManager();
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
		QueryContextBuilder queryContextBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder();
		QueryBuilder qb = queryContextBuilder.forEntity(entityClass).get();
		//noinspection rawtypes
		BooleanJunction<? extends BooleanJunction> booleanJunction = qb.bool();
		// create a MUST (= AND) query for every search term
		for (String currSearchTerm : searchTermList) {
			Query subtermquery = createTermquery(currSearchTerm, filter, qb);
			booleanJunction = booleanJunction.must(subtermquery);
		}

		Query query = booleanJunction.createQuery();
		return fullTextEntityManager.createFullTextQuery(query, entityClass);
	}

	/**
	 * creats a 'subquery' for the given search term and returns it.
	 */
	private Query createTermquery(String currSearchTerm, SearchFilter filter, QueryBuilder qb) {
		//manche felder sollen ohne field bridge matched werden, daher hier die komplizierte aufteilung
		List<String> normalFieldsToSearch = new ArrayList<>(filter.getFieldsToSearch().length);
		List<String> fieldsIgnoringBridge = new ArrayList<>();
		for (IndexedEBEGUFieldName indexedField : filter.getFieldsToSearch()) {
			if (!indexedField.isIgnoreFieldBridgeInQuery()) {
				normalFieldsToSearch.add(indexedField.getIndexedFieldName());
			} else {
				fieldsIgnoringBridge.add(indexedField.getIndexedFieldName());  //geburtsdatum ignoriert field-bridge
			}
		}
		TermMatchingContext termCtxt = qb
			.keyword()
			.wildcard()
			.onFields(normalFieldsToSearch.toArray(new String[normalFieldsToSearch.size()]));

		for (String s : fieldsIgnoringBridge) {
			termCtxt = termCtxt.andField(s).ignoreFieldBridge();
		}
		TermTermination matching = termCtxt.matching(currSearchTerm);
		return matching.createQuery();
	}

}

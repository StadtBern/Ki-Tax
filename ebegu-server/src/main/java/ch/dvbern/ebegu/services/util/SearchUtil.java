/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

package ch.dvbern.ebegu.services.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import ch.dvbern.ebegu.enums.SearchMode;

public final class SearchUtil {

	private SearchUtil() {
	}

	public static List<String> determineDistinctIdsToLoad(List<String> allIds, int startindex, int maxresults) {
		List<String> uniqueGesuchIds = new ArrayList<>(new LinkedHashSet<>(allIds)); //keep order but remove duplicate ids
		int lastindex = Math.min(startindex + maxresults, (uniqueGesuchIds.size()));
		return uniqueGesuchIds.subList(startindex, lastindex);
	}

	public static String withWildcards(String s) {
		return '%' + s + '%';
	}

	@SuppressWarnings("rawtypes") // Je nach Abfrage ist es String oder Long
	public static CriteriaQuery getQueryForSearchMode(CriteriaBuilder cb, SearchMode mode, String methodName) {
		CriteriaQuery query;
		switch (mode) {
		case SEARCH:
			query = cb.createQuery(String.class);
			return query;
		case COUNT:
			query = cb.createQuery(Long.class);
			return query;
		default:
			throw new IllegalStateException("Undefined Mode for " + methodName + " Query: " + mode);
		}
	}
}

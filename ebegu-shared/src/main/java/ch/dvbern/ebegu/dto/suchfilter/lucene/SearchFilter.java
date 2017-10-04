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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * Filter class for searches in the lucene index. Determines which Index should be searched and which fields.
 * May also include the number of maxResults
 */
public class SearchFilter implements Serializable {

	private static final long serialVersionUID = 9077335739843860474L;

	@Nonnull
	private final SearchEntityType searchEntityType;
	@Nonnull
	private final IndexedEBEGUFieldName[] fieldsToSearch;

	private final Integer maxResults;

	/**
	 * @param fieldNames Leer: alle Felder der Entity durchsuchen.
	 */
	public SearchFilter(@Nonnull SearchEntityType searchEntityType, @Nonnull String... fieldNames) {
		this(searchEntityType, null, fieldNames);

	}

	public SearchFilter(@Nonnull SearchEntityType searchEntityType, @Nullable Integer maxResults, @Nonnull String... fieldNames) {
		Objects.requireNonNull(searchEntityType);
		Objects.requireNonNull(fieldNames);

		if (fieldNames.length > 0) {
			Preconditions.checkArgument(searchEntityType.getFieldNames().containsAll(Arrays.asList(fieldNames)));
			this.fieldsToSearch = Arrays.stream(searchEntityType.getIndexedFields())
				.filter(indexedField -> Arrays.asList(fieldNames).contains(indexedField.getIndexedFieldName()))
				.toArray(IndexedEBEGUFieldName[]::new);
		} else {
			this.fieldsToSearch = Arrays.stream(searchEntityType.getIndexedFields())
				.toArray(IndexedEBEGUFieldName[]::new);
		}
		this.searchEntityType = searchEntityType;
		this.maxResults = maxResults;

	}

	public Integer getMaxResults() {
		return maxResults;
	}

	@Nonnull
	public SearchEntityType getSearchEntityType() {
		return searchEntityType;
	}

	@Nonnull
	public IndexedEBEGUFieldName[] getFieldsToSearch() {
		return fieldsToSearch.clone();
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("searchEntityType", searchEntityType)
			.add("fieldNames", Joiner.on("/").join(fieldsToSearch))
			.toString();
	}
}


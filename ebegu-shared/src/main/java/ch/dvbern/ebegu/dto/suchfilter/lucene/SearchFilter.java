package ch.dvbern.ebegu.dto.suchfilter.lucene;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

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


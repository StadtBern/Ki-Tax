package ch.dvbern.ebegu.dto.suchfilter.lucene;

import ch.dvbern.ebegu.entities.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ch.dvbern.ebegu.dto.suchfilter.lucene.IndexedEBEGUFieldName.*;

/**
 * Enum of Entities that can be searched in the index via the SearchService. Also determines the searchable fields
 * and wether the entity will be searched in a globalSearch or not
 */
public enum SearchEntityType {
	// Reihenfolge bitte entsprechend in der Wichtigkeit im GUI
	GESUCHSTELLER_CONTAINER(GesuchstellerContainer.class, new IndexedEBEGUFieldName[]{GS_NACHNAME, GS_VORNAME, GS_GEBDATUM}),
	KIND_CONTAINER(KindContainer.class, new IndexedEBEGUFieldName[]{KIND_NACHNAME, KIND_VORNAME, KIND_GEBDATUM}),
	GESUCH(Gesuch.class, new IndexedEBEGUFieldName[]{FALL_NUMMER}),
	BETREUUNG(Betreuung.class, new IndexedEBEGUFieldName[]{BETREUUNG_BGNR});


	@Nonnull
	private final Class<? extends Searchable> entityClass;

	private final boolean globalSearch;

	@Nonnull
	private final List<String> fieldNames;


	@Nonnull
	private final IndexedEBEGUFieldName[] indexedFields;

	<T extends Searchable> SearchEntityType(@Nonnull Class<T> entityClass, @Nonnull IndexedEBEGUFieldName[] indexedFields) {
		this(entityClass, indexedFields, true);
	}

	<T extends Searchable> SearchEntityType(@Nonnull Class<T> entityClass, @Nonnull IndexedEBEGUFieldName[] indexedFields, boolean globalSearch) {
		this.entityClass = entityClass;
		this.indexedFields = indexedFields.clone();
		this.fieldNames = Collections.unmodifiableList(Arrays.stream(indexedFields).map(IndexedEBEGUFieldName::getIndexedFieldName).collect(Collectors.toList()));
		this.globalSearch = globalSearch;
	}

	@Nonnull
	public <T extends Searchable> Class<T> getEntityClass() {
		//noinspection unchecked
		return (Class<T>) entityClass;
	}

	public boolean isGlobalSearch() {
		return globalSearch;
	}

	@Nonnull
	public List<String> getFieldNames() {
		return fieldNames;
	}

	@Nonnull
	public IndexedEBEGUFieldName[] getIndexedFields() {
		return indexedFields.clone();
	}


	/**
	 * @param clazz Die Entity-Klasse.
	 * @param exact true: Es wird nach der exakten Klasse gesucht. False: clazz darf auch eine Subklasse der Entity sein.
	 */
	@Nullable
	public static SearchEntityType fromEntityClass(@Nonnull Class<? extends AbstractEntity> clazz, boolean exact) {
		Objects.requireNonNull(clazz);

		for (SearchEntityType e : values()) {
			if (exact) {
				if (Objects.equals(e.getEntityClass(), clazz)) {
					return e;
				}
			} else {
				if (e.getEntityClass().isAssignableFrom(clazz)) {
					return e;
				}
			}
		}
		return null;
	}
}

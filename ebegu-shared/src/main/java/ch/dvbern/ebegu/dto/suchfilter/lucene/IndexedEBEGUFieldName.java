package ch.dvbern.ebegu.dto.suchfilter.lucene;

import org.hibernate.search.annotations.FieldBridge;

/**
 * Enum with all the indexed fields in our Lucene Index
 */
public enum IndexedEBEGUFieldName {

	GS_VORNAME("gesuchstellerJA.nachname"),
	GS_NACHNAME("gesuchstellerJA.vorname"),
	GS_GEBDATUM("gesuchstellerJA.geburtsdatum", true),
	KIND_VORNAME("kindJA.nachname"),
	KIND_NACHNAME("kindJA.vorname"),
	KIND_GEBDATUM("kindJA.geburtsdatum", true),
	BETREUUNG_BGNR("bGNummer"),
	FALL_NUMMER("fall.fallNummer");


	private final String indexedFieldName;

	/**
	 * wenn hier true gesetzt wird wird das feld beim erstellen des queries nicht ueber die {@link FieldBridge} gesucht sondern
	 * direkt als string
	 */
	private final boolean ignoreFieldBridgeInQuery;

	IndexedEBEGUFieldName(String indexedFieldName) {
		this(indexedFieldName, false);
	}

	IndexedEBEGUFieldName(String indexedFieldName, boolean ignoreFieldBridgeInQuery) {
		this.indexedFieldName = indexedFieldName;
		this.ignoreFieldBridgeInQuery = ignoreFieldBridgeInQuery;

	}

	public String getIndexedFieldName() {
		return indexedFieldName;
	}


	public boolean isIgnoreFieldBridgeInQuery() {
		return ignoreFieldBridgeInQuery;
	}




}

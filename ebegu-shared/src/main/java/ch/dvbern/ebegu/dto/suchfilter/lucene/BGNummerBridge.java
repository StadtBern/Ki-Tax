package ch.dvbern.ebegu.dto.suchfilter.lucene;

import ch.dvbern.ebegu.entities.Betreuung;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;

/**
 * Class Bridge welche es ermoeglicht den transient Getter fuer getBGNummer zu indizieren
 * Damit die bridge in queries gebraucht werden kann ist es ausserdem eine {@link TwoWayFieldBridge} welche
 * mit strings umgehen kann
 */
public class BGNummerBridge implements TwoWayFieldBridge {


	@Override
	public void set(
		String name, Object value, Document document, LuceneOptions luceneOptions) {
		Betreuung betreuung = (Betreuung) value;
		String fieldValue = betreuung.getBGNummer();
		Field field = new StringField(name, fieldValue, luceneOptions.getStore());
		field.setBoost(luceneOptions.getBoost());
		document.add(field);
	}

	@Override
	public Object get(String name, Document document) {
		final IndexableField field = document.getField(name);
		return field != null ? field.stringValue() : null;
	}

	@Override
	public String objectToString(Object object) {
		if (object instanceof String) {
			return (String) object;
		}
		return "";
	}
}

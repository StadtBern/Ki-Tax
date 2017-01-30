package ch.dvbern.ebegu.dto.suchfilter.lucene;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This FieldBridge ats LocalDate values as strings to the Index.
 * Note that it is not a {@link TwoWayFieldBridge} so queries on fields that use this bridge have to
 * be marked as to not use the FieldBridge
 */
public class EbeguLocalDateBridge implements FieldBridge{


	private static final DateTimeFormatter DF_FULL = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter DF_SHORT1 = DateTimeFormatter.ofPattern("d.M.yyyy");
	private static final DateTimeFormatter DF_SHORT2 = DateTimeFormatter.ofPattern("d.M.yy");
	private static final DateTimeFormatter DF_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public void set(
		String name, Object value, Document document, LuceneOptions luceneOptions) {
		if (value != null) {
			LocalDate dateToIndex = (LocalDate) value; //mus ein localDate sein
			luceneOptions.addFieldToDocument(name, dateToIndex.format(DF_FULL), document);
			luceneOptions.addFieldToDocument(name, dateToIndex.format(DF_SHORT1), document);
			luceneOptions.addFieldToDocument(name, dateToIndex.format(DF_SHORT2), document);
			luceneOptions.addFieldToDocument(name, dateToIndex.format(DF_ISO), document);
		}
	}

}

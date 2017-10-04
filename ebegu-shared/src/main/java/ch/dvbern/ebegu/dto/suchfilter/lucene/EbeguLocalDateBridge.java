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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;

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

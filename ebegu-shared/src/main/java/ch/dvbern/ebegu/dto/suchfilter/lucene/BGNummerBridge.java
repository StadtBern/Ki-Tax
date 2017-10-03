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

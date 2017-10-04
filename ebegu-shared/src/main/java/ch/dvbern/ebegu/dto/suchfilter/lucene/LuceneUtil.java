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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * Helper that runs a lucene analyzer on a string
 */
public final class LuceneUtil {

	private LuceneUtil() {
	}

	public static List<String> tokenizeString(Analyzer analyzer, String stringToAnalyze) {
		List<String> result = new ArrayList<>();
		try {
			TokenStream stream = analyzer.tokenStream(null, new StringReader(stringToAnalyze));
			stream.reset();
			while (stream.incrementToken()) {
				result.add(stream.getAttribute(CharTermAttribute.class).toString());
			}
		} catch (IOException e) {
			// not thrown b/c we're using a string reader...
			throw new EbeguRuntimeException("tokenizeString", "Unexpected Error when tokenizing", e, stringToAnalyze);
		}
		return result;
	}

}

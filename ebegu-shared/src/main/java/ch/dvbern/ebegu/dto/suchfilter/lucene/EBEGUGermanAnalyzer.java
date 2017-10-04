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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.de.GermanNormalizationFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * Wir moechten in der E-Begu suche keine Stopwoerter aulassen und das reduzieren auf wortstaemme etc ist unnoetig
 * Daher definieren wir einen eigenen Analyzer der nur einige wenige Normalizierungen vornimmt
 * Dazu gehoert das umwanden von umlauten und kleinschreibung. Dadurch wird unser Index relativ ungeeignet
 * fuer die suche in lanngen Texten aber das machen wir zur Zeit nicht
 */
public final class EBEGUGermanAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		final Tokenizer source = new StandardTokenizer();
		TokenStream result = new StandardFilter(source);
		result = new LowerCaseFilter(result);
		result = new GermanNormalizationFilter(result);
		return new TokenStreamComponents(source, result);
	}
}

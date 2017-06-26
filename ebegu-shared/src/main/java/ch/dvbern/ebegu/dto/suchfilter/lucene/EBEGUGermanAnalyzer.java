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

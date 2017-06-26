package ch.dvbern.ebegu.dto.suchfilter.lucene;

import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper that runs a lucene analyzer on a string
 */
public final class LuceneUtil {

  private LuceneUtil() {}

  public static List<String> tokenizeString(Analyzer analyzer, String stringToAnalyze) {
	  List<String> result = new ArrayList<>();
    try {
      TokenStream stream  = analyzer.tokenStream(null, new StringReader(stringToAnalyze));
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

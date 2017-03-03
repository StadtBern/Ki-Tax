package ch.dvbern.ebegu.dto.suchfilter.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * We decided to create our own normalizer. This test gives a fes examples to showcase its behaviour
 */
public class EBEGUGermanAnalyzerTest {

	private Analyzer analyzer;

	@Before
	public void setUp(){
		this.analyzer = new EBEGUGermanAnalyzer();

	}

	@After
	public void after(){
		this.analyzer.close();
	}

	@Test
	public void testNormalization() throws Exception {
		String testquery = "Bäckerin  Aepfel Äpfel Meier löblichster Stuehle";
		List<String> strings = LuceneUtil.tokenizeString(analyzer, testquery);
		List<String> expectedTokens = new ArrayList<>();
		Collections.addAll(expectedTokens, "backerin", "apfel", "apfel", "meier", "loblichster", "stuhle");
		Assert.assertEquals(expectedTokens, strings);

	}

	@Test
	public void testNoStopwordsAreRemoved() throws Exception {
		String testquery = "Maximilian von und zu Habsburg";
		List<String> strings = LuceneUtil.tokenizeString(analyzer, testquery);
		List<String> expectedTokens = new ArrayList<>();
		Collections.addAll(expectedTokens, "maximilian", "von", "und", "zu", "habsburg");
		Assert.assertEquals(expectedTokens, strings);

	}

	@Test
	public void testLowercaseNormalization() throws Exception {
		String testquery = "MAX Haus mauS";
		List<String> strings = LuceneUtil.tokenizeString(analyzer, testquery);
		List<String> expectedTokens = new ArrayList<>();
		Collections.addAll(expectedTokens, "max", "haus", "maus");
		Assert.assertEquals(expectedTokens, strings);

	}

	@Test
	public void testTokenizationOfSpecialChars() throws Exception {
		String testquery = "test@löwenfels.ch 123-3456 123.456 mueller-meier 'test' \"test\"";
		List<String> strings = LuceneUtil.tokenizeString(analyzer, testquery);
		List<String> expectedTokens = new ArrayList<>();
		Collections.addAll(expectedTokens, "test", "lowenfels.ch", "123", "3456", "123.456", "muller", "meier", "test", "test");
		Assert.assertEquals(expectedTokens, strings);

	}

}

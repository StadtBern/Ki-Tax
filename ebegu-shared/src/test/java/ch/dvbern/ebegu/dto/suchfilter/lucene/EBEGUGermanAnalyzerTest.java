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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * We decided to create our own normalizer. This test gives a fes examples to showcase its behaviour
 */
public class EBEGUGermanAnalyzerTest {

	private Analyzer analyzer;

	@Before
	public void setUp() {
		this.analyzer = new EBEGUGermanAnalyzer();

	}

	@After
	public void after() {
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

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

package ch.dvbern.ebegu.tests;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import javax.inject.Inject;

import ch.dvbern.ebegu.reporting.ReportMassenversandService;
import ch.dvbern.ebegu.reporting.massenversand.MassenversandDataRow;
import ch.dvbern.ebegu.util.Constants;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@UsingDataSet("datasets/massenversand-dataset.xml")
@Transactional(TransactionMode.DISABLED)
public class ReportMassenversandServiceBeanArquillianTest extends AbstractEbeguLoginTest {

	private static final String GESUCHSPERIODE_1718_ID = "0621fb5d-a187-5a91-abaf-8a813c4d263a";
	private static final String GESUCHSPERIODE_1819_ID = "19f5403c-3bdc-494a-b0d1-bebffdc6eee0";
	private static final LocalDate DATE_FIRST_MASSENVERSAND = LocalDate.of(2018, Month.NOVEMBER, 27);
	private static final LocalDate DATE_SECOND_MASSENVERSAND = LocalDate.of(2018, Month.DECEMBER, 27);

	@Inject
	ReportMassenversandService reportService;

	/**
	 * This will simulate the first massenversand at the date 27-11-2018 for the periode 1718 and only JA.
	 * The data that is used to asssert the results are taken from the values in massenversand-dataset.xml
	 */
	@Test
	public void testFirstMassenversandJA() {
		final List<MassenversandDataRow> massenversandDataRows = reportService.getReportMassenversand(
			Constants.START_OF_TIME,
			DATE_FIRST_MASSENVERSAND,
			GESUCHSPERIODE_1718_ID,
			true,
			false,
			false,
			false,
			null
		);

		Assert.assertEquals(5, massenversandDataRows.size());
		assertJABetreuungen(massenversandDataRows);
	}

	/**
	 * This will simulate the second massenversand at the date 27-12-2018 for the periode 1718 and only JA.
	 * The data that is used to asssert the results are taken from the values in massenversand-dataset.xml
	 */
	@Test
	public void testSecondMassenversandJA() {
		final List<MassenversandDataRow> massenversandDataRows = reportService.getReportMassenversand(
			DATE_FIRST_MASSENVERSAND.plusDays(1), // important!!!!
			DATE_SECOND_MASSENVERSAND,
			GESUCHSPERIODE_1718_ID,
			true,
			false,
			false,
			false,
			null
		);

		Assert.assertEquals(1, massenversandDataRows.size());
		assertJABetreuungen(massenversandDataRows);
	}

	/**
	 * This will simulate the first massenversand at the date 27-11-2018 for the periode 1718 and only Mischgesuche.
	 * The data that is used to asssert the results are taken from the values in massenversand-dataset.xml
	 */
	@Test
	public void testFirstMassenversandMisch() {
		final List<MassenversandDataRow> massenversandDataRows = reportService.getReportMassenversand(
			Constants.START_OF_TIME,
			DATE_FIRST_MASSENVERSAND,
			GESUCHSPERIODE_1718_ID,
			false,
			true,
			false,
			false,
			null
		);

		Assert.assertEquals(2, massenversandDataRows.size());
		massenversandDataRows.forEach(row -> {
			Assert.assertEquals("2017/2018", row.getGesuchsperiode());
			row.getKinderCols().forEach(kind -> {
				// no Mischgesuch and no SCHGesuch
				Assert.assertNotNull(kind.getKindInstitutionTagesschule());
				Assert.assertNotNull(kind.getKindInstitutionKita());
			});
		});
	}

	/**
	 * This will simulate the second massenversand at the date 27-12-2018 for the periode 1718 and only Mischgesuche.
	 * The data that is used to asssert the results are taken from the values in massenversand-dataset.xml
	 */
	@Test
	public void testSecondMassenversandMisch() {
		final List<MassenversandDataRow> massenversandDataRows = reportService.getReportMassenversand(
			DATE_FIRST_MASSENVERSAND.plusDays(1), // important!!!!
			DATE_SECOND_MASSENVERSAND,
			GESUCHSPERIODE_1718_ID,
			false,
			true,
			false,
			false,
			null
		);

		Assert.assertTrue(massenversandDataRows.isEmpty());
	}

	/**
	 * This will simulate the first massenversand at the date 27-11-2018 for the periode 1718 and only TS.
	 * The data that is used to asssert the results are taken from the values in massenversand-dataset.xml
	 */
	@Test
	public void testFirstMassenversandTS() {
		final List<MassenversandDataRow> massenversandDataRows = reportService.getReportMassenversand(
			Constants.START_OF_TIME,
			DATE_FIRST_MASSENVERSAND,
			GESUCHSPERIODE_1718_ID,
			false,
			false,
			true,
			false,
			null
		);

		Assert.assertEquals(1, massenversandDataRows.size());
		massenversandDataRows.forEach(row -> {
			Assert.assertEquals("2017/2018", row.getGesuchsperiode());
			row.getKinderCols().forEach(kind -> {
				// no Mischgesuch and no SCHGesuch
				Assert.assertNotNull(kind.getKindInstitutionTagesschule());
				Assert.assertNull(kind.getKindInstitutionKita());
				Assert.assertNull(kind.getKindInstitutionTagi());
				Assert.assertNull(kind.getKindInstitutionTeKleinkind());
				Assert.assertNull(kind.getKindInstitutionTeSchulkind());
			});
		});
	}

	/**
	 * This will simulate the second massenversand at the date 27-12-2018 for the periode 1718 and only TS.
	 * The data that is used to asssert the results are taken from the values in massenversand-dataset.xml
	 */
	@Test
	public void testSecondMassenversandTS() {
		final List<MassenversandDataRow> massenversandDataRows = reportService.getReportMassenversand(
			DATE_FIRST_MASSENVERSAND.plusDays(1), // important!!!!
			DATE_SECOND_MASSENVERSAND,
			GESUCHSPERIODE_1718_ID,
			false,
			false,
			true,
			false,
			null
		);

		Assert.assertTrue(massenversandDataRows.isEmpty());
	}

	/**
	 * This will simulate the first massenversand at the date 27-11-2018 for the periode 1718 and all kinds of gesuch.
	 * The data that is used to asssert the results are taken from the values in massenversand-dataset.xml
	 */
	@Test
	public void testFirstMassenversandAll() {
		final List<MassenversandDataRow> massenversandDataRows = reportService.getReportMassenversand(
			Constants.START_OF_TIME,
			DATE_FIRST_MASSENVERSAND,
			GESUCHSPERIODE_1718_ID,
			true,
			true,
			true,
			false,
			null
		);

		Assert.assertEquals(8, massenversandDataRows.size());
	}

	/**
	 * This will simulate the first massenversand at the date 27-11-2018 for the periode 1718, all kinds of gesuch
	 * and without Folgegesuche.
	 * The data that is used to asssert the results are taken from the values in massenversand-dataset.xml
	 */
	@Test
	public void testFirstMassenversandAllNoFolgegesuch() {
		final List<MassenversandDataRow> massenversandDataRows = reportService.getReportMassenversand(
			Constants.START_OF_TIME,
			DATE_FIRST_MASSENVERSAND,
			GESUCHSPERIODE_1718_ID,
			true,
			true,
			true,
			true,
			null
		);

		Assert.assertEquals(7, massenversandDataRows.size());
	}

	/**
	 * This will simulate the first massenversand at the date 27-11-2018 for the periode 1718 and all kinds of gesuch.
	 * The data that is used to asssert the results are taken from the values in massenversand-dataset.xml
	 */
	@Test
	public void testSecondMassenversandAll() {
		final List<MassenversandDataRow> massenversandDataRows = reportService.getReportMassenversand(
			DATE_FIRST_MASSENVERSAND.plusDays(1), // important!!!!
			DATE_SECOND_MASSENVERSAND,
			GESUCHSPERIODE_1718_ID,
			true,
			true,
			true,
			false,
			null
		);

		Assert.assertEquals(1, massenversandDataRows.size());
	}

	/**
	 * This will simulate the first massenversand at the date 27-11-2018 for the periode 1819, all kinds of gesuch
	 * and without Folgegesuche.
	 * The data that is used to asssert the results are taken from the values in massenversand-dataset.xml
	 */
	@Test
	public void testFirstMassenversandAll1819() {
		final List<MassenversandDataRow> massenversandDataRows = reportService.getReportMassenversand(
			Constants.START_OF_TIME,
			DATE_FIRST_MASSENVERSAND,
			GESUCHSPERIODE_1819_ID,
			true,
			true,
			true,
			true,
			null
		);

		Assert.assertTrue(massenversandDataRows.isEmpty());
	}

	/**
	 * This will simulate the second massenversand at the date 27-11-2018 for the periode 1819, all kinds of gesuch
	 * and without Folgegesuche.
	 * The data that is used to asssert the results are taken from the values in massenversand-dataset.xml
	 */
	@Test
	public void testSecondMassenversandAll1819() {
		final List<MassenversandDataRow> massenversandDataRows = reportService.getReportMassenversand(
			DATE_FIRST_MASSENVERSAND.plusDays(1), // important!!!!
			DATE_SECOND_MASSENVERSAND,
			GESUCHSPERIODE_1819_ID,
			true,
			true,
			true,
			true,
			null
		);

		Assert.assertEquals(1, massenversandDataRows.size());
	}

	private void assertJABetreuungen(List<MassenversandDataRow> massenversandDataRows) {
		massenversandDataRows.forEach(row -> {
			Assert.assertEquals("2017/2018", row.getGesuchsperiode());
			row.getKinderCols().forEach(kind -> {
				// no Mischgesuch and no SCHGesuch
				Assert.assertNull(kind.getKindInstitutionTagesschule());
				Assert.assertNull(kind.getKindInstitutionFerieninsel());
			});
		});
	}

}

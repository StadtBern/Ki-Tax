package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests fuer VerfuegungUtil
 */
public class VerfuegungUtilTest {

	/**
	 *  J   F   M   A   M   J   J
	 *  |---|---|---|---|---|---|   -> Zeitabschnitte
	 *      |---|                   -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitInterval1Abschnitt() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 3, 31));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(new BigDecimal(80).setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	/**
	 *  J   F   M   A   M   J   J
	 *  |---|---|---|---|---|---|   -> Zeitabschnitte
	 *      |-------|               -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitInterval2Abschnitte() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 4, 30));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(new BigDecimal(160).setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	/**
	 *  J   F   M   A   M   J   J
	 *  |---|---|---|---|---|---|   -> Zeitabschnitte
	 *       |-|                    -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitIntervalSubabschnitt() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2018, 3, 10), LocalDate.of(2018, 3, 25));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(new BigDecimal(41.29).setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	/**
	 *  J   F   M   A   M   J   J
	 *  |---|---|---|---|---|---|   -> Zeitabschnitte
	 *      |-----|                 -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitIntervalOnAndHalfabschnitt() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 4, 20));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(new BigDecimal(133.33).setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	/**
	 *  J   F   M   A   M   J   J
	 *  |---|---|---|---|---|---|   -> Zeitabschnitte
	 *        |---|                 -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitIntervalTwoHalfsabschnitt() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2018, 3, 15), LocalDate.of(2018, 4, 20));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(new BigDecimal(97.20).setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	/**
	 *  J   F   M   A   M   J   J
	 *  |---|---|---|---|---|---|   -> Zeitabschnitte
	 *         |----|               -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitIntervalTwoHalfsabschnitt2() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2018, 3, 15), LocalDate.of(2018, 4, 30));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(new BigDecimal(123.87).setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	/**
	 *  J   F   M   A   M   J   J
	 *  |---|---|---|---|---|---|   -> Zeitabschnitte
	 *        |-----------|         -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitIntervalSeveralabschnitte() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2018, 2, 27), LocalDate.of(2018, 5, 3));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(new BigDecimal(173.46).setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	/**
	 *          J   F   M   A   M   J   J
	 *          |---|---|---|---|---|---|   -> Zeitabschnitte
	 *  |---|                               -> interval
	 */
	@Test
	public void testGetVerguenstigungZeitIntervalOutOfRange() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = createZeitabschnitte();
		DateRange dateRange = createIntervall(LocalDate.of(2017, 12, 10), LocalDate.of(2017, 12, 30));

		final BigDecimal verguenstigung = VerfuegungUtil.getVerguenstigungZeitInterval(zeitabschnitte, dateRange);

		Assert.assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), verguenstigung);
	}

	// HELP METHODS

	@Nonnull
	private DateRange createIntervall(LocalDate dateAb, LocalDate dateBis) {
		DateRange dateRange = new DateRange();
		dateRange.setGueltigAb(dateAb);
		dateRange.setGueltigBis(dateBis);
		return dateRange;
	}

	@Nonnull
	private List<VerfuegungZeitabschnitt> createZeitabschnitte() {
		List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();
		for (int i = 1; i < 8; i++) {
			VerfuegungZeitabschnitt abschnitt1 = new VerfuegungZeitabschnitt();
			// ranges for whole months. Last day calculated from next month minus 1 day
			DateRange gueltigkeit = createIntervall(LocalDate.of(2018, i, 1), LocalDate.of(2018, i + 1, 1).minusDays(1));
			abschnitt1.setGueltigkeit(gueltigkeit);
			abschnitt1.setVollkosten(new BigDecimal(100));
			abschnitt1.setElternbeitrag(new BigDecimal(20));
			zeitabschnitte.add(abschnitt1);
		}
		return zeitabschnitte;
	}
}

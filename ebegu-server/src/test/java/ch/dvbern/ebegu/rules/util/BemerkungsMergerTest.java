package ch.dvbern.ebegu.rules.util;

import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Test Class for Bemerkungsmerger
 */
public class BemerkungsMergerTest {

	private static final DateRange JAN = new DateRange(LocalDate.of(2016, 1, 1), LocalDate.of(2016, 1, 1).with(TemporalAdjusters.lastDayOfMonth()));
	private static final DateRange FEB = new DateRange(LocalDate.of(2016, 2, 1), LocalDate.of(2016, 2, 1).with(TemporalAdjusters.lastDayOfMonth()));
	private static final DateRange MAR = new DateRange(LocalDate.of(2016, 3, 1), LocalDate.of(2016, 3, 1).with(TemporalAdjusters.lastDayOfMonth()));
	private static final DateRange APR = new DateRange(LocalDate.of(2016, 4, 1), LocalDate.of(2016, 4, 1).with(TemporalAdjusters.lastDayOfMonth()));
	private static final DateRange MAI = new DateRange(LocalDate.of(2016, 5, 1), LocalDate.of(2016, 5, 1).with(TemporalAdjusters.lastDayOfMonth()));


	@Test
	public void evaluateRangesByBemerkungKeyTest() throws Exception {
		VerfuegungZeitabschnitt jan = new VerfuegungZeitabschnitt(JAN);
		VerfuegungZeitabschnitt feb = new VerfuegungZeitabschnitt(FEB);
		VerfuegungZeitabschnitt mar = new VerfuegungZeitabschnitt(MAR);
		VerfuegungZeitabschnitt apr = new VerfuegungZeitabschnitt(APR);
		VerfuegungZeitabschnitt mai = new VerfuegungZeitabschnitt(MAI);
		jan.setBemerkungen("A\nB\nC");
		feb.setBemerkungen("A\nB\nC");
		mar.setBemerkungen("A\nB");
		apr.setBemerkungen("B\nC");
		mai.setBemerkungen("A\nB\nC");

		List<VerfuegungZeitabschnitt> verfZeitabschn = new ArrayList<>();
		Collections.addAll(verfZeitabschn, jan, feb, mar, apr, mai);

		Map<String, Collection<DateRange>> bemerkungenByKey = BemerkungsMerger.evaluateRangesByBemerkungKey(verfZeitabschn);

		Collection<DateRange> aRanges = bemerkungenByKey.get("A");
		Deque<DateRange> resultRange = new LinkedList<>(aRanges);
		Assert.assertEquals(2, aRanges.size());
		Assert.assertEquals(resultRange.getFirst().getGueltigAb(), jan.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(resultRange.getFirst().getGueltigBis(), mar.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(resultRange.getLast().getGueltigAb(), mai.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(resultRange.getLast().getGueltigBis(), mai.getGueltigkeit().getGueltigBis());

		Collection<DateRange> bRanges = bemerkungenByKey.get("B");
		resultRange = new LinkedList<>(bRanges);
		Assert.assertEquals(1, bRanges.size());
		Assert.assertEquals(resultRange.getFirst().getGueltigAb(), jan.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(resultRange.getFirst().getGueltigBis(), mai.getGueltigkeit().getGueltigBis());
		Collection<DateRange> cRanges = bemerkungenByKey.get("C");
		Assert.assertEquals(2, cRanges.size());
		resultRange = new LinkedList<>(cRanges);
		Assert.assertEquals(resultRange.getFirst().getGueltigAb(), jan.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(resultRange.getFirst().getGueltigBis(), feb.getGueltigkeit().getGueltigBis());
		Assert.assertEquals(resultRange.getLast().getGueltigAb(), apr.getGueltigkeit().getGueltigAb());
		Assert.assertEquals(resultRange.getLast().getGueltigBis(), mai.getGueltigkeit().getGueltigBis());

		//test output
		String resultingBem = BemerkungsMerger.evaluateBemerkungenForVerfuegung(verfZeitabschn);
		String[] strings = resultingBem.split("\\n");
		Assert.assertEquals(5, strings.length);
		Assert.assertEquals("[01.01.2016 - 31.03.2016] A", strings[0]);
		Assert.assertEquals("[01.05.2016 - 31.05.2016] A", strings[1]);
		Assert.assertEquals("[01.01.2016 - 31.05.2016] B", strings[2]);
		Assert.assertEquals("[01.01.2016 - 29.02.2016] C", strings[3]);
		Assert.assertEquals("[01.04.2016 - 31.05.2016] C", strings[4]);

	}

	@Test
	public void evaluateBemerkungenForVerfuegungOverlappInvalidTest() throws Exception {
		VerfuegungZeitabschnitt jan = new VerfuegungZeitabschnitt(JAN);
		VerfuegungZeitabschnitt overlappWithJan = new VerfuegungZeitabschnitt(new DateRange(JAN.getGueltigBis(), FEB.getGueltigBis()));
		jan.setBemerkungen("A");
		overlappWithJan.setBemerkungen("A");

		List<VerfuegungZeitabschnitt> verfZeitabschn = new ArrayList<>();
		Collections.addAll(verfZeitabschn, jan, overlappWithJan);
		try {
			Map<String, Collection<DateRange>> bemerkungenByKey = BemerkungsMerger.evaluateRangesByBemerkungKey(verfZeitabschn);
			Assert.fail("Should throw exception because of overlap");
		} catch (IllegalArgumentException e) {
			//noop
		}
	}


}

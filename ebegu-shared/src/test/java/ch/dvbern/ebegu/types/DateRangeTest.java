package ch.dvbern.ebegu.types;

import org.junit.Assert;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class DateRangeTest {

	private final DateRange year2015 = new DateRange(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 31));

	@Test
	public void testIsInRange_shouldBeFalseWhenNotInRange() {
		assertFalse(year2015.contains(LocalDate.of(2014, 12, 31)));
		assertFalse(year2015.contains(LocalDate.of(2016, 1, 1)));
	}

	@Test
	public void testIsInRange_shouldBeTrueWhenInRange() {
		assertTrue(year2015.contains(LocalDate.of(2015, 1, 1)));
		assertTrue(year2015.contains(LocalDate.of(2015, 12, 31)));
		assertTrue(year2015.contains(LocalDate.of(2015, 2, 10)));
	}

	@Test
	public void testEquals() {
		assertEquals(year2015, new DateRange(year2015));
		assertFalse(year2015.equals(new DateRange(LocalDate.of(2015, 1, 2), LocalDate.of(2015, 12, 31))));
		assertFalse(year2015.equals(new DateRange(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 30))));
		assertFalse(year2015.equals(new DateRange(LocalDate.of(2016, 1, 1), LocalDate.of(2016, 12, 31))));
	}

	@Test
	public void testGetOverlap_shouldBeEqualForIdenticalRanges() {
		assertEquals(year2015, year2015.getOverlap(new DateRange(year2015)).get());
	}

	@Test
	public void testIntersect_rangeShouldOverlapWithItself() {
		assertTrue(year2015.intersects(new DateRange(year2015)));

	}

	@Test
	public void testGetOverlap_shouldBeTheSubRange() {
		DateRange subRange1 = new DateRange(year2015.getGueltigAb(), year2015.getGueltigBis().minusDays(1));
		assertEquals(subRange1, year2015.getOverlap(subRange1).get());
		assertEquals(subRange1, subRange1.getOverlap(year2015).get());

		DateRange subRange2 = new DateRange(year2015.getGueltigAb().plusDays(1), year2015.getGueltigBis());
		assertEquals(subRange2, year2015.getOverlap(subRange2).get());
		assertEquals(subRange2, subRange2.getOverlap(year2015).get());

		DateRange subRange3 = new DateRange(year2015.getGueltigAb().plusDays(1), year2015.getGueltigBis().minusDays(1));
		assertEquals(subRange3, year2015.getOverlap(subRange3).get());
		assertEquals(subRange3, subRange3.getOverlap(year2015).get());
	}

	@Test
	public void testGetOverlap_schnittpunktIstStartOderEnde() {
		LocalDate schnittpunkt = LocalDate.of(1976, 11, 19);
		DateRange schnittpunktRange = new DateRange(schnittpunkt, schnittpunkt);

		DateRange a = new DateRange(schnittpunkt.minusYears(1), schnittpunkt);
		DateRange b = new DateRange(schnittpunkt, schnittpunkt.plusYears(1));

		Optional<DateRange> overlap = a.getOverlap(b);
		assertEquals(schnittpunktRange, overlap.get());

		// nochmal andersrum
		Optional<DateRange> overlapInverse = b.getOverlap(a);
		assertEquals(schnittpunktRange, overlapInverse.get());
	}

	@Test
	public void testGetOverlap_shouldBeEmptyWhenNoOverlap() {
		assertFalse(year2015.getOverlap(new DateRange(LocalDate.of(2014, 1, 1), LocalDate.of(2014, 12, 31))).isPresent());
	}


	@Test
	public void testGetDays_shouldCountDaysFromGueltigAbToGueltigBis() {
		LocalDate date = LocalDate.of(2014, 1, 1);
		DateRange singleDay = new DateRange(date, date);
		assertEquals(1, singleDay.getDays());

		DateRange range = new DateRange(date, date.plusDays(3));
		assertEquals(4, range.getDays());
	}

	@Test
	public void testGetOverlap_stichtag() {
		LocalDate stichtag = year2015.getGueltigAb();
		DateRange expected = new DateRange(stichtag, stichtag);
		assertEquals(expected, year2015.getOverlap(new DateRange(stichtag, stichtag)).get());
	}

	@Test
	public void testEndsDayBefore_localdate() {
		DateRange a = year2015;
		LocalDate adjacent        = year2015.getGueltigAb().plusYears(1);
		LocalDate before          = adjacent.minusDays(1);
		LocalDate same            = year2015.getGueltigAb();
		LocalDate copy            = year2015.getGueltigAb().plusDays(0); // copy
		LocalDate after           = adjacent.plusDays(1);

		assertTrue(a.endsDayBefore(adjacent));
		assertFalse(a.endsDayBefore(before));
		assertFalse(a.endsDayBefore(same));
		assertFalse(a.endsDayBefore(copy));
		assertFalse(a.endsDayBefore(after));
	}

	@Test
	public void testEndsDayBefore_daterange() {
		DateRange a = year2015;
		DateRange adjacent        = new DateRange(LocalDate.of(2016, 1, 1), LocalDate.of(2016, 12, 31));
		DateRange before          = new DateRange(LocalDate.of(2012, 1, 1), LocalDate.of(2012, 12, 31));
		DateRange intersectBefore = new DateRange(LocalDate.of(2012, 1, 1), LocalDate.of(2015, 10, 10));
		DateRange intersectAfter  = new DateRange(LocalDate.of(2015, 3, 3), LocalDate.of(2016, 12, 31));
		DateRange after           = new DateRange(LocalDate.of(2017, 1, 1), LocalDate.of(2017, 12, 31));

		assertTrue(a.endsDayBefore(adjacent));
		assertFalse(a.endsDayBefore(a)); // same ref
		assertFalse(a.endsDayBefore(before));
		assertFalse(a.endsDayBefore(intersectBefore));
		assertFalse(a.endsDayBefore(intersectAfter));
		assertFalse(a.endsDayBefore(after));
	}

	@Test
	public void testStichtag() {
		LocalDate d = LocalDate.now();
		LocalDate other = d.plusDays(1);
		assertTrue(new DateRange(d).isStichtag());
		assertFalse(new DateRange(d, other).isStichtag());
	}

	@Test
	public void testWithFullWeeks_stichtag() {
		LocalDate montag = LocalDate.of(2015, 10, 12); // Montag
		assertEquals(DayOfWeek.MONDAY, montag.getDayOfWeek()); // nur zur Sicherheit :)
		LocalDate mittwoch = LocalDate.of(2015, 10, 14); // Mittwoch
		assertEquals(DayOfWeek.WEDNESDAY, mittwoch.getDayOfWeek()); // nur zur Sicherheit :)
		LocalDate sonntag = LocalDate.of(2015, 10, 18); // Sonntag
		assertEquals(DayOfWeek.SUNDAY, sonntag.getDayOfWeek()); // nur zur Sicherheit :)

		// Stichtag
		DateRange stichtag = new DateRange(mittwoch);
		assertEquals(montag, stichtag.withFullWeeks().getGueltigAb());
		assertEquals(sonntag, stichtag.withFullWeeks().getGueltigBis());
	}

	@Test
	public void testWithFullWeeks_range() {
		LocalDate montag = LocalDate.of(2015, 10, 12); // Montag
		assertEquals(DayOfWeek.MONDAY, montag.getDayOfWeek()); // nur zur Sicherheit :)
		LocalDate mittwoch = LocalDate.of(2015, 10, 14); // Mittwoch
		assertEquals(DayOfWeek.WEDNESDAY, mittwoch.getDayOfWeek()); // nur zur Sicherheit :)
		LocalDate donnerstag = LocalDate.of(2015, 10, 22); // Donnerstag, eine Woche spaeter
		assertEquals(DayOfWeek.THURSDAY, donnerstag.getDayOfWeek()); // nur zur Sicherheit :)
		LocalDate sonntag = LocalDate.of(2015, 10, 25); // Sonntag, eine Woche spaeter
		assertEquals(DayOfWeek.SUNDAY, sonntag.getDayOfWeek()); // nur zur Sicherheit :)

		// Stichtag
		DateRange stichtag = new DateRange(mittwoch, donnerstag);
		assertEquals(montag, stichtag.withFullWeeks().getGueltigAb());
		assertEquals(sonntag, stichtag.withFullWeeks().getGueltigBis());
	}

	@Test
	public void testWithFullMonths() {
		LocalDate stichtag = LocalDate.of(2015, 5, 5);
		DateRange oneMonth = new DateRange(stichtag).withFullMonths();
		assertEquals(LocalDate.of(2015, 5, 1), oneMonth.getGueltigAb());
		assertEquals(LocalDate.of(2015, 5, 31), oneMonth.getGueltigBis());

		LocalDate stichtagSchaltjahr = LocalDate.of(2016, 2, 5);
		DateRange oneMonthSchaltjahr = new DateRange(stichtagSchaltjahr).withFullMonths();
		assertEquals(LocalDate.of(2016, 2, 1), oneMonthSchaltjahr.getGueltigAb());
		assertEquals(LocalDate.of(2016, 2, 29), oneMonthSchaltjahr.getGueltigBis());

		DateRange range = new DateRange(LocalDate.of(2014, 5, 5), LocalDate.of(2015, 5, 5));
		DateRange multiYear = range.withFullMonths();
		assertEquals(new DateRange(LocalDate.of(2014, 5, 1), LocalDate.of(2015, 5, 31)), multiYear);
	}


	@Test
	public void testWithFullYears() {
		LocalDate stichtag = LocalDate.of(2015, 5, 5);
		DateRange oneYear = new DateRange(stichtag).withFullYears();
		assertEquals(LocalDate.of(2015, 1, 1), oneYear.getGueltigAb());
		assertEquals(LocalDate.of(2015, 12, 31), oneYear.getGueltigBis());

		DateRange range = new DateRange(LocalDate.of(2014, 5, 5), LocalDate.of(2015, 5, 5));
		DateRange multiYear = range.withFullYears();
		assertEquals(new DateRange(LocalDate.of(2014, 1, 1), LocalDate.of(2015, 12, 31)), multiYear);
	}

	private int sign(int value) {
		//noinspection NumericCastThatLosesPrecision
		return (int) Math.signum(value);
	}

	@Test
	public void testCompareTo() throws Exception {
		DateRange ref = new DateRange(LocalDate.of(2016, 1, 1), LocalDate.of(2016, 12, 31));
		DateRange refSame = new DateRange(LocalDate.of(2016, 1, 1), LocalDate.of(2016, 12, 31));
		DateRange later = new DateRange(LocalDate.of(9999, 1, 1), LocalDate.of(9999, 12, 31));
		DateRange laterLonger = new DateRange(LocalDate.of(2016, 1, 1), LocalDate.of(9999, 12, 31));
		DateRange before = new DateRange(LocalDate.of(1000, 1, 1), LocalDate.of(1000, 12, 31));
		DateRange beforeLonger = new DateRange(LocalDate.of(1000, 1, 1), LocalDate.of(2016, 12, 31));

		assertEquals(0, ref.compareTo(ref));
		assertEquals(0, ref.compareTo(refSame));
		assertEquals(-1, sign(ref.compareTo(later)));
		assertEquals(-1, sign(ref.compareTo(laterLonger)));
		assertEquals(1, sign(ref.compareTo(before)));
		assertEquals(1, sign(ref.compareTo(beforeLonger)));

	}

	@Test
	public void testToFullWeekRanges_shouldReturnASingleWeekForAWeekRange() {
		DateRange weekRange = new DateRange(LocalDate.now()).withFullWeeks();

		List<DateRange> actualRanges = weekRange.toFullWeekRanges();
		assertEquals(1, actualRanges.size());
		assertEquals(weekRange, actualRanges.get(0));
	}

	@Test
	public void testToFullWeekRanges_shouldReturnASingleWeekForARangeWithinAWeek() {
		// Tuesday to Wednesday in the same week
		DateRange withinAWeekRange = new DateRange(LocalDate.of(2016, 4, 19), LocalDate.of(2016, 4, 20));

		List<DateRange> actualRanges = withinAWeekRange.toFullWeekRanges();
		assertEquals(1, actualRanges.size());
		assertEquals(withinAWeekRange.withFullWeeks(), actualRanges.get(0));
	}

	@Test
	public void testToFullWeekRanges_shouldReturnASingleTwoWeekForALongRangeOfFullWeeks() {
		DateRange twoWeekRange = new DateRange(LocalDate.now()).withFullWeeks();
		twoWeekRange.getGueltigBis().plusWeeks(5);

		List<DateRange> actualRanges = twoWeekRange.toFullWeekRanges();
		assertEquals(1, actualRanges.size());
		assertEquals(twoWeekRange, actualRanges.get(0));
	}

	@Test
	public void testToFullWeekRanges_shouldReturnTwoRangesForARangeIntersectingTwoWeeks() {
		DateRange weednesdayToFridayNextWeek = new DateRange(LocalDate.of(2016, 4, 13), LocalDate.of(2016, 4, 22));

		List<DateRange> actualRanges = weednesdayToFridayNextWeek.toFullWeekRanges();
		assertEquals(2, actualRanges.size());
		assertEquals(new DateRange(weednesdayToFridayNextWeek.getGueltigAb()).withFullWeeks(), actualRanges.get(0));
		assertEquals(new DateRange(weednesdayToFridayNextWeek.getGueltigBis()).withFullWeeks(), actualRanges.get(1));
	}

	@Test
	public void testToFullWeekRanges_shouldReturnThreeRangesForARangeIntersectingManyWeeks() {
		DateRange tuesdayToThursdayThreeWeeksLater = new DateRange(LocalDate.of(2016, 4, 5), LocalDate.of(2016, 4, 28));

		List<DateRange> actualRanges = tuesdayToThursdayThreeWeeksLater.toFullWeekRanges();
		assertEquals(3, actualRanges.size());

		DateRange expectedGueltigAbWeek = new DateRange(tuesdayToThursdayThreeWeeksLater.getGueltigAb()).withFullWeeks();
		assertEquals(expectedGueltigAbWeek, actualRanges.get(0));

		DateRange expectedGueltigBisWeek = new DateRange(tuesdayToThursdayThreeWeeksLater.getGueltigBis()).withFullWeeks();
		assertEquals(expectedGueltigBisWeek, actualRanges.get(2));

		DateRange expectedInbetweenWeeks = new DateRange(LocalDate.of(2016, 4, 11), LocalDate.of(2016, 4, 24));
		assertEquals(expectedInbetweenWeeks, actualRanges.get(1));
	}

	@Test
	public void testToFullWeekRanges_shouldReturnTwoRangesWhenGueltigAbIsOnAMonday() {
		DateRange mondayToThursdayThreeWeeksLater = new DateRange(LocalDate.of(2016, 4, 4), LocalDate.of(2016, 4, 28));

		List<DateRange> actualRanges = mondayToThursdayThreeWeeksLater.toFullWeekRanges();
		assertEquals(2, actualRanges.size());

		DateRange expectedStartWeeks = new DateRange(LocalDate.of(2016, 4, 4), LocalDate.of(2016, 4, 24));
		assertEquals(expectedStartWeeks, actualRanges.get(0));

		DateRange expectedGueltigBisWeek = new DateRange(LocalDate.of(2016, 4, 25), LocalDate.of(2016, 5, 1));
		assertEquals(expectedGueltigBisWeek, actualRanges.get(1));
	}

	@Test
	public void testToFullWeekRanges_shouldReturnTwoRangesWhenGueltigBisIsOnASunday() {
		DateRange tuesdayToSundayThreeWeeksLater = new DateRange(LocalDate.of(2016, 4, 5), LocalDate.of(2016, 5, 1));

		List<DateRange> actualRanges = tuesdayToSundayThreeWeeksLater.toFullWeekRanges();
		assertEquals(2, actualRanges.size());

		DateRange expectedGueltigAbWeek = new DateRange(LocalDate.of(2016, 4, 4), LocalDate.of(2016, 4, 10));
		assertEquals(expectedGueltigAbWeek, actualRanges.get(0));

		DateRange expectedEndWeeks = new DateRange(LocalDate.of(2016, 4, 11), LocalDate.of(2016, 5, 1));
		assertEquals(expectedEndWeeks, actualRanges.get(1));
	}

	@Test
	public void testCalculateEndOfPreviousYear() {
		DateRange range = new DateRange(LocalDate.of(2016, 8, 1), LocalDate.of(2017, 7, 31));
		Assert.assertEquals(LocalDate.of(2015, 12, 31), range.calculateEndOfPreviousYear());
	}

	@Test
	public void testCalculateEndOfPreviousYearJanuar() {
		DateRange range = new DateRange(LocalDate.of(2016, 1, 1), LocalDate.of(2017, 7, 31));
		Assert.assertEquals(LocalDate.of(2015, 12, 31), range.calculateEndOfPreviousYear());
	}

	@Test
	public void testCalculateEndOfPreviousYearDezember() {
		DateRange range = new DateRange(LocalDate.of(2016, 12, 31), LocalDate.of(2017, 7, 31));
		Assert.assertEquals(LocalDate.of(2015, 12, 31), range.calculateEndOfPreviousYear());
	}

	@Test
	public void testCalculateEndOfPreviousYearYearZero() {
		DateRange range = new DateRange(LocalDate.of(0, 12, 31), LocalDate.of(2017, 7, 31));
		Assert.assertEquals(LocalDate.of(-1, 12, 31), range.calculateEndOfPreviousYear());
	}
}

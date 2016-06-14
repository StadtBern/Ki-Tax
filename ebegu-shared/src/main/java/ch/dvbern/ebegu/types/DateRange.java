package ch.dvbern.ebegu.types;

import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.validators.CheckDateRange;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

@Embeddable
@CheckDateRange
public class DateRange implements Serializable, Comparable<DateRange> {

	private static final long serialVersionUID = 8244737446639845584L;

	@Nonnull
	@NotNull
	@Column(nullable = false)
	private LocalDate gueltigAb;

	@Nonnull
	@NotNull
	@Column(nullable = false)
	private LocalDate gueltigBis;

	public DateRange(@Nonnull LocalDate gueltigAb, @Nonnull LocalDate gueltigBis) {
		this.gueltigAb = Objects.requireNonNull(gueltigAb);
		this.gueltigBis = Objects.requireNonNull(gueltigBis);
	}

	/**
	 * Von jetzt bis zur Unendlichkeit
	 */
	public DateRange() {
		this(LocalDate.now(), Constants.END_OF_TIME);
	}

	/**
	 * stichtag == gueltigAb == gueltigBis
	 */
	public DateRange(@Nonnull LocalDate stichtag) {
		this(stichtag, stichtag);
	}

	/**
	 * Copy-Constructor
	 */
	public DateRange(@Nonnull DateRange gueltigkeit) {
		this(gueltigkeit.getGueltigAb(), gueltigkeit.getGueltigBis());
	}

	/**
	 * DateRange für ein ganzes Kalender-Jahr
     */
	public DateRange(@Nonnull Integer jahr) {
		this(LocalDate.of(jahr, Month.JANUARY, 1), LocalDate.of(jahr, Month.DECEMBER, 31));
	}

	/**
	 * gueltigAb <= date <= gueltigBis
	 */
	public boolean contains(@Nonnull ChronoLocalDate date) {
		return !(date.isBefore(getGueltigAb()) || date.isAfter(getGueltigBis()));
	}

	/**
	 * gueltigAb < date && gueltigBis < date
	 */
	public boolean isBefore(@Nonnull ChronoLocalDate date) {
		return getGueltigAb().isBefore(date) && getGueltigBis().isBefore(date);
	}

	/**
	 * gueltigAb > date && gueltigBis > date
	 */
	public boolean isAfter(@Nonnull ChronoLocalDate date) {
		return getGueltigAb().isAfter(date) && getGueltigBis().isAfter(date);
	}

	/**
	 * gueltigBis == date - 1 Day
	 */
	public boolean endsDayBefore(@Nonnull ChronoLocalDate date) {
		return getGueltigBis().equals(date.minus(1, ChronoUnit.DAYS));
	}

	/**
	 * gueltigBis == gueltigAb
	 */
	public boolean isStichtag() {
		return getGueltigAb().equals(getGueltigBis());
	}

	/**
	 * gueltigBis == other.gueltigAb - 1 Day
	 */
	public boolean endsDayBefore(@Nonnull DateRange other) {
		return endsDayBefore(other.getGueltigAb());
	}

	/**
	 * setzt das gueltig bis einer Range auf den Tag vor dem datum von der "other" range
	 * @param other
	 */
	public void endOnDayBefore(@Nonnull DateRange other){
		this.setGueltigBis(other.gueltigAb.minusDays(1));

	}

	/**
	 * Neue DateRange, mit gueltigAb auf den vorherigen Montag und gueltigBis auf den naechsten Sonntag setzt.
	 * Use-Case z.B.: einen Stichtag auf die ganze Woche ausdehnen.
	 */
	public DateRange withFullWeeks() {
		LocalDate montag = getGueltigAb().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate sonntag = getGueltigBis().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
		return new DateRange(montag, sonntag);
	}



	@Nonnull
	public List<DateRange> toFullWeekRanges() {
		if (gueltigAb.getDayOfWeek() == DayOfWeek.MONDAY && gueltigBis.getDayOfWeek() == DayOfWeek.SUNDAY) {
			//noinspection ArraysAsListWithZeroOrOneArgument
			return Arrays.asList(new DateRange(gueltigAb, gueltigBis));
		}

		DateRange gueltigAbWeek = new DateRange(gueltigAb).withFullWeeks();
		DateRange gueltigBisWeek = new DateRange(gueltigBis).withFullWeeks();

		if (gueltigAbWeek.intersects(gueltigBisWeek)) {
			// both dates are within the same week
			//noinspection ArraysAsListWithZeroOrOneArgument
			return Arrays.asList(gueltigAbWeek);
		}

		if (gueltigAbWeek.endsDayBefore(gueltigBisWeek)) {
			// gueltigAb & gueltigBis are in two adjacent weeks
			return Arrays.asList(gueltigAbWeek, gueltigBisWeek);
		}


		LocalDate ab = gueltigAb.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
		LocalDate bis = gueltigBis.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
		List<DateRange> result = new ArrayList<>();

		if (ab.isAfter(gueltigAb)) {
			result.add(gueltigAbWeek);
		}

		result.add(new DateRange(ab, bis));

		if (bis.isBefore(gueltigBis)) {
			result.add(gueltigBisWeek);
		}

		return result;
	}

	/**
	 * Neue DateRange, mit gueltigAb auf den ersten Tag des Monats von gueltigAb
	 * un dem lezten Tag des Monats von gueltigBs.
	 * Kann also mehrere Monate umspannen!
	 */
	@Nonnull
	public DateRange withFullMonths() {
		LocalDate firstDay = getGueltigAb().with(TemporalAdjusters.firstDayOfMonth());
		LocalDate lastDay = getGueltigBis().with(TemporalAdjusters.lastDayOfMonth());
		return new DateRange(firstDay, lastDay);
	}

	/**
	 * Neue DateRange, mit gueltigAb auf den ersten Tag des Jahres von gueltigAb
	 * und gueltigBis auf den letzten Tag des Jahres von gueltigBis.
	 * Kann also mehrere Jahre umspannen!
	 */
	@Nonnull
	public DateRange withFullYears() {
		LocalDate firstDay = getGueltigAb().with(TemporalAdjusters.firstDayOfYear());
		LocalDate lastDay = getGueltigBis().with(TemporalAdjusters.lastDayOfYear());
		return new DateRange(firstDay, lastDay);
	}

	/**
	 * @return Falls es zwischen dieser DateRange und otherRange eine zeitliche ueberlappung gibt, so wird diese zurueck gegeben
	 */
	@Nonnull
	public Optional<DateRange> getOverlap(@Nonnull DateRange otherRange) {
		if (this.getGueltigAb().isAfter(otherRange.getGueltigBis()) || this.getGueltigBis().isBefore(otherRange.getGueltigAb())) {
			return Optional.empty();
		}

		LocalDate ab = otherRange.getGueltigAb().isAfter(this.getGueltigAb()) ? otherRange.getGueltigAb() : this.getGueltigAb();
		LocalDate bis = otherRange.getGueltigBis().isBefore(this.getGueltigBis()) ? otherRange.getGueltigBis() : this.getGueltigBis();

		return Optional.of(new DateRange(ab, bis));
	}

	/**
	 * {@link #getOverlap(DateRange)}.isPresent()
	 */
	public boolean intersects(@Nonnull DateRange other) {
		return getOverlap(other).isPresent();
	}

	/**
	 * @return counts the number of days between gueltigAb and gueltigBis (inclusive gueltigAb and gueltigBis)
	 */
	public long getDays() {
		return ChronoUnit.DAYS.between(gueltigAb, gueltigBis) + 1;
	}


	@Nonnull
	public LocalDate getGueltigAb() {
		return gueltigAb;
	}

	public void setGueltigAb(@Nonnull LocalDate gueltigAb) {
		this.gueltigAb = gueltigAb;
	}

	@Nonnull
	public LocalDate getGueltigBis() {
		return gueltigBis;
	}

	public void setGueltigBis(@Nonnull LocalDate gueltigBis) {
		this.gueltigBis = gueltigBis;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DateRange)) {
			return false;
		}

		DateRange other = (DateRange) o;

		return 0 == this.compareTo(other);
	}

	@Override
	public int hashCode() {
		int result = getGueltigAb().hashCode();
		result = 31 * result + getGueltigBis().hashCode();
		return result;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("gueltigAb",gueltigAb).add("gueltigBis", gueltigBis).toString();
	}

	/**
	 * Natural ordering: zuerst gueltigAb vergleichen, dann gueltigBis
	 */
	@Override
	public int compareTo(@Nonnull DateRange o) {
		checkNotNull(o);

		int cmp = getGueltigAb().compareTo(o.getGueltigAb());
		if (cmp == 0) {
			cmp = getGueltigBis().compareTo(o.getGueltigBis());
		}
		return cmp;
	}
}

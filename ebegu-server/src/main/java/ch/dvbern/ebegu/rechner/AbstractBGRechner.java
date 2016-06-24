package ch.dvbern.ebegu.rechner;

import ch.dvbern.ebegu.util.MathUtil;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.stream.Stream;

/**
 * Superklasse für BG-Rechner
 */
public class AbstractBGRechner {

	protected static final BigDecimal FAKTOR_KIND = MathUtil.EXACT.from(1);
	protected static final BigDecimal ZWOELF = MathUtil.EXACT.from(12L);
	protected static final BigDecimal NEUN = MathUtil.EXACT.from(9L);
	protected static final BigDecimal ZWANZIG = MathUtil.EXACT.from(20L);
	protected static final BigDecimal ZWEIHUNDERTVIERZIG = MathUtil.EXACT.from(240L);

	/**
	 * Berechnet den Anteil des Zeitabschnittes am gesamten Monat
     */
	protected BigDecimal calculateAnteilMonat(LocalDate von, LocalDate bis) {
		LocalDate monatsanfang = von.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate monatsende = bis.with(TemporalAdjusters.lastDayOfMonth());
		long nettoarbeitstageMonat = workDaysBetween(monatsanfang, monatsende);
		long nettoarbeitstageIntervall = workDaysBetween(von, bis);
		return MathUtil.EXACT.divide(MathUtil.EXACT.from(nettoarbeitstageIntervall), MathUtil.EXACT.from(nettoarbeitstageMonat));
	}

	/**
	 * Berechnet die Kosten einer Betreuungsstunde (Tagi und Tageseltern)
     */
	protected BigDecimal calculateKostenBetreuungsstunde(BigDecimal kostenProStundeMaximal, BigDecimal massgebendesEinkommen, BigDecimal anspruch, BGRechnerParameterDTO parameterDTO) {
		// Massgebendes Einkommen: Minimum und Maximum berücksichtigen
		BigDecimal massgebendesEinkommenBerechnet = (massgebendesEinkommen.max(parameterDTO.getMassgebendesEinkommenMinimal())).min(parameterDTO.getMassgebendesEinkommenMaximal());
		BigDecimal kostenProStundeMaxMinusMin = MathUtil.EXACT.subtract(kostenProStundeMaximal, parameterDTO.getKostenProStundeMinimal());
		BigDecimal massgebendesEinkommenMaxMinusMin = MathUtil.EXACT.subtract(parameterDTO.getMassgebendesEinkommenMaximal(), parameterDTO.getMassgebendesEinkommenMinimal());
		BigDecimal massgebendesEinkommenMinusMin = MathUtil.EXACT.subtract(massgebendesEinkommenBerechnet, parameterDTO.getMassgebendesEinkommenMinimal());
		BigDecimal zwischenresultat1 = MathUtil.EXACT.divide(kostenProStundeMaxMinusMin, massgebendesEinkommenMaxMinusMin);
		BigDecimal zwischenresultat2 = MathUtil.EXACT.multiply(anspruch, zwischenresultat1, massgebendesEinkommenMinusMin);
		return MathUtil.EXACT.add(zwischenresultat2, parameterDTO.getKostenProStundeMinimal());
	}

	/**
	 * Berechnet die Anzahl Wochentage zwischen (und inklusive) Start und End
	 */
	private long workDaysBetween(LocalDate start, LocalDate end) {
		return Stream.iterate(start, d->d.plusDays(1))
			.limit(start.until(end.plusDays(1), ChronoUnit.DAYS))
			.filter(d->!(DayOfWeek.SATURDAY.equals(d.getDayOfWeek()) || DayOfWeek.SUNDAY.equals(d.getDayOfWeek())))
			.count();
	}
}

package ch.dvbern.ebegu.rechner;

import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.util.MathUtil;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.stream.Stream;

/**
 * Berechnet die Vollkosten, den Elternbeitrag und die Vergünstigung für einen Zeitabschnitt (innerhalb eines Monats)
 * einer Betreuung für das Angebot TAGI (Tagesstätte für Schulkinder).
 */
public class TagiRechner {

	public static final BigDecimal FAKTOR_KIND = MathUtil.EXACT.from(1);
	public static final BigDecimal FAKTOR_BABY = MathUtil.EXACT.from(1.5);

	public static final BigDecimal ZWOELF = MathUtil.EXACT.from(12L);
	public static final BigDecimal NEUN = MathUtil.EXACT.from(9L);
	public static final BigDecimal ZWANZIG = MathUtil.EXACT.from(20L);
	public static final BigDecimal ZWEIHUNDERTVIERZIG = MathUtil.EXACT.from(240L);


	public VerfuegungZeitabschnitt calculate(VerfuegungZeitabschnitt verfuegungZeitabschnitt, Verfuegung verfuegung, BGRechnerParameterDTO parameterDTO) {
		// Benoetigte Daten
		LocalDate von = verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb();
		LocalDate bis = verfuegungZeitabschnitt.getGueltigkeit().getGueltigBis();
		BigDecimal anspruch = MathUtil.EXACT.pctToFraction(new BigDecimal(verfuegungZeitabschnitt.getAnspruchberechtigtesPensum()));
		BigDecimal massgebendesEinkommen = verfuegungZeitabschnitt.getMassgebendesEinkommen();


		// Zwischenresultate
		LocalDate monatsanfang = von.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate monatsende = bis.with(TemporalAdjusters.lastDayOfMonth());

		long nettoarbeitstageMonat = workDaysBetween(monatsanfang, monatsende);
		long nettoarbeitstageIntervall = workDaysBetween(von, bis);
		BigDecimal anteilMonat = MathUtil.EXACT.divide(MathUtil.EXACT.from(nettoarbeitstageIntervall), MathUtil.EXACT.from(nettoarbeitstageMonat));


		BigDecimal anzahlTageProMonat = MathUtil.EXACT.divide(parameterDTO.getAnzahlTageTagi(), ZWOELF);
		BigDecimal betreuungsstundenProMonat = MathUtil.EXACT.multiply(anzahlTageProMonat, parameterDTO.getAnzahlStundenProTagTagi(), anspruch);
		BigDecimal betreuungsstundenIntervall = MathUtil.EXACT.multiply(betreuungsstundenProMonat, anteilMonat);


		// Massgebendes Einkommen: Minimum und Maximum berücksichtigen
		BigDecimal massgebendesEinkommenBerechnet = (massgebendesEinkommen.max(parameterDTO.getMassgebendesEinkommenMinimal())).min(parameterDTO.getMassgebendesEinkommenMaximal());



		// Kosten Betreuungsstunde
		BigDecimal kostenProStundeMaxMinusMin = MathUtil.EXACT.subtract(parameterDTO.getKostenProStundeMaximalKitaTagi(), parameterDTO.getKostenProStundeMinimal());
		BigDecimal massgebendesEinkommenMaxMinusMin = MathUtil.EXACT.subtract(parameterDTO.getMassgebendesEinkommenMaximal(), parameterDTO.getMassgebendesEinkommenMinimal());
		BigDecimal massgebendesEinkommenMinusMin = MathUtil.EXACT.subtract(massgebendesEinkommenBerechnet, parameterDTO.getMassgebendesEinkommenMinimal());

		BigDecimal param1 = MathUtil.EXACT.divide(kostenProStundeMaxMinusMin, massgebendesEinkommenMaxMinusMin);
		BigDecimal zwischenresultat2 = MathUtil.EXACT.multiply(anspruch, param1, massgebendesEinkommenMinusMin);
		BigDecimal kostenProBetreuungsstunde = MathUtil.EXACT.add(zwischenresultat2, parameterDTO.getKostenProStundeMinimal());


		BigDecimal vollkosten = MathUtil.EXACT.multiply(parameterDTO.getKostenProStundeMaximalKitaTagi(), betreuungsstundenIntervall);
		BigDecimal elternbeitrag = MathUtil.EXACT.multiply(kostenProBetreuungsstunde, betreuungsstundenIntervall);

		// Runden und auf Zeitabschnitt zurückschreiben

		verfuegungZeitabschnitt.setVollkosten(MathUtil.roundToFrankenRappen(vollkosten));
		verfuegungZeitabschnitt.setElternbeitrag(MathUtil.roundToFrankenRappen(elternbeitrag));
		return verfuegungZeitabschnitt;
	}

	public static long workDaysBetween(LocalDate start, LocalDate end) {
		return Stream.iterate(start, d->d.plusDays(1))
			.limit(start.until(end.plusDays(1), ChronoUnit.DAYS))
			.filter(d->!(DayOfWeek.SATURDAY.equals(d.getDayOfWeek()) || DayOfWeek.SUNDAY.equals(d.getDayOfWeek())))
			.count();
	}
}

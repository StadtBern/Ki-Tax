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
 * einer Betreuung für das Angebot KITA.
 */
public class KitaRechner {

	public static final BigDecimal FAKTOR_KIND = MathUtil.EXACT.from(1);
	public static final BigDecimal ZWOELF = MathUtil.EXACT.from(12L);
	public static final BigDecimal NEUN = MathUtil.EXACT.from(9L);
	public static final BigDecimal ZWANZIG = MathUtil.EXACT.from(20L);
	public static final BigDecimal ZWEIHUNDERTVIERZIG = MathUtil.EXACT.from(240L);


	public VerfuegungZeitabschnitt calculate(VerfuegungZeitabschnitt verfuegungZeitabschnitt, Verfuegung verfuegung, BGRechnerParameterDTO parameterDTO) {
		// Benoetigte Daten
		LocalDate von = verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb();
		LocalDate bis = verfuegungZeitabschnitt.getGueltigkeit().getGueltigBis();
		LocalDate geburtsdatum = verfuegung.getBetreuung().getKind().getKindJA().getGeburtsdatum();
		BigDecimal oeffnungsstunden = verfuegung.getBetreuung().getInstitutionStammdaten().getOeffnungsstunden();
		BigDecimal oeffnungstage = verfuegung.getBetreuung().getInstitutionStammdaten().getOeffnungstage();
		BigDecimal anspruch = MathUtil.EXACT.pctToFraction(new BigDecimal(verfuegungZeitabschnitt.getAnspruchberechtigtesPensum()));
		BigDecimal massgebendesEinkommen = verfuegungZeitabschnitt.getMassgebendesEinkommen();

		// Zwischenresultate
		BigDecimal faktor = von.isAfter(geburtsdatum.plusMonths(parameterDTO.getBabyAlterInMonaten()).with(TemporalAdjusters.lastDayOfMonth())) ? FAKTOR_KIND : parameterDTO.getBabyFaktor();
		LocalDate monatsanfang = von.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate monatsende = bis.with(TemporalAdjusters.lastDayOfMonth());

		long nettoarbeitstageMonat = workDaysBetween(monatsanfang, monatsende);
		long nettoarbeitstageIntervall = workDaysBetween(von, bis);
		BigDecimal anteilMonat = MathUtil.EXACT.divide(MathUtil.EXACT.from(nettoarbeitstageIntervall), MathUtil.EXACT.from(nettoarbeitstageMonat));

		// Abgeltung pro Tag: Abgeltung des Kantons plus Beitrag der Stadt
		BigDecimal abgeltungProTag = MathUtil.EXACT.add(parameterDTO.getBeitragKantonProTag(), parameterDTO.getBeitragStadtProTag());
		// Massgebendes Einkommen: Minimum und Maximum berücksichtigen
		BigDecimal massgebendesEinkommenBerechnet = (massgebendesEinkommen.max(parameterDTO.getMassgebendesEinkommenMinimal())).min(parameterDTO.getMassgebendesEinkommenMaximal());
		// Öffnungstage und Öffnungsstunden; Maximum berücksichtigen
		BigDecimal oeffnungstageBerechnet = oeffnungstage.min(parameterDTO.getAnzahlTageMaximal());
		BigDecimal oeffnungsstundenBerechnet = oeffnungsstunden.min(parameterDTO.getAnzahlStundenProTagMaximal());

		// Vollkosten
		BigDecimal vollkostenZaehler = MathUtil.EXACT.multiply(abgeltungProTag, oeffnungsstundenBerechnet, oeffnungstageBerechnet, anspruch);
		BigDecimal vollkostenNenner = MathUtil.EXACT.multiply(parameterDTO.getAnzahlStundenProTagMaximal(), ZWOELF);
		BigDecimal vollkosten = MathUtil.EXACT.divide(vollkostenZaehler, vollkostenNenner);

		// Elternbeitrag
		BigDecimal kostenProStundeMaxMinusMin = MathUtil.EXACT.subtract(parameterDTO.getKostenProStundeMaximalKitaTagi(), parameterDTO.getKostenProStundeMinimal());
		BigDecimal massgebendesEinkommenMinusMin = MathUtil.EXACT.subtract(massgebendesEinkommenBerechnet, parameterDTO.getMassgebendesEinkommenMinimal());
		BigDecimal massgebendesEinkommenMaxMinusMin = MathUtil.EXACT.subtract(parameterDTO.getMassgebendesEinkommenMaximal(), parameterDTO.getMassgebendesEinkommenMinimal());
		BigDecimal param1 = MathUtil.EXACT.multiply(kostenProStundeMaxMinusMin, massgebendesEinkommenMinusMin);
		BigDecimal param2 = MathUtil.EXACT.multiply(parameterDTO.getKostenProStundeMinimal(), massgebendesEinkommenMaxMinusMin);
		BigDecimal param1Plus2 = MathUtil.EXACT.add(param1, param2);
		BigDecimal elternbeitragZaehler = MathUtil.EXACT.multiply(param1Plus2, NEUN, ZWANZIG, anspruch, oeffnungstageBerechnet, oeffnungsstundenBerechnet);
		BigDecimal elternbeitragNenner = MathUtil.EXACT.multiply(massgebendesEinkommenMaxMinusMin, ZWEIHUNDERTVIERZIG, parameterDTO.getAnzahlStundenProTagMaximal());
		BigDecimal elternbeitrag = MathUtil.EXACT.divide(elternbeitragZaehler, elternbeitragNenner);

		// Runden und auf Zeitabschnitt zurückschreiben
		BigDecimal vollkostenIntervall = MathUtil.EXACT.multiply(vollkosten, faktor, anteilMonat);
		BigDecimal elternbeitragIntervall = MathUtil.EXACT.multiply(elternbeitrag, anteilMonat);

		verfuegungZeitabschnitt.setVollkosten(MathUtil.roundToFrankenRappen(vollkostenIntervall));
		verfuegungZeitabschnitt.setElternbeitrag(MathUtil.roundToFrankenRappen(elternbeitragIntervall));
		return verfuegungZeitabschnitt;
	}

	public static long workDaysBetween(LocalDate start, LocalDate end) {
		return Stream.iterate(start, d->d.plusDays(1))
			.limit(start.until(end.plusDays(1), ChronoUnit.DAYS))
			.filter(d->!(DayOfWeek.SATURDAY.equals(d.getDayOfWeek()) || DayOfWeek.SUNDAY.equals(d.getDayOfWeek())))
			.count();
	}
}

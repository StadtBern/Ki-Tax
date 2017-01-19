package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.MathUtil;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Berechnet die hoehe des ErwerbspensumRule eines bestimmten Erwerbspensums
 * Diese Rule muss immer am Anfang kommen, d.h. sie setzt den initialen Anspruch
 * Die weiteren Rules müssen diesen Wert gegebenenfalls korrigieren.
 * ACHTUNG! Diese Regel gilt nur fuer Angebote vom Typ isAngebotJugendamtKleinkind
 * Verweis 16.9.2
 */
public class ErwerbspensumCalcRule extends AbstractCalcRule {

	private int maxZuschlagValue;


	public ErwerbspensumCalcRule(DateRange validityPeriod, int maxZuschlagValue) {
		super(RuleKey.ERWERBSPENSUM, RuleType.GRUNDREGEL_CALC, validityPeriod);
		this.maxZuschlagValue = maxZuschlagValue;
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		if (betreuung.getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()) {
			Objects.requireNonNull(betreuung.extractGesuch(), "Gesuch muss gesetzt sein");
			Objects.requireNonNull(betreuung.extractGesuch().extractFamiliensituation(), "Familiensituation muss gesetzt sein");
			boolean hasSecondGesuchsteller = hasSecondGSForZeit(betreuung, verfuegungZeitabschnitt.getGueltigkeit());
			int erwerbspensumOffset = hasSecondGesuchsteller ? 100 : 0;
			// Erwerbspensum ist immer die erste Rule, d.h. es wird das Erwerbspensum mal als Anspruch angenommen
			// Das Erwerbspensum muss PRO GESUCHSTELLER auf 100% limitiert werden
			Integer erwerbspensum1 = calculateErwerbspensumGS1(verfuegungZeitabschnitt);
			Integer erwerbspensum2 = 0;
			if (hasSecondGesuchsteller) {
				erwerbspensum2 = calculateErwerbspensumGS2(verfuegungZeitabschnitt);
			}
			int totalZuschlag = calculateTotalZuschlag(verfuegungZeitabschnitt);

			int anspruch = erwerbspensum1 + erwerbspensum2 + totalZuschlag - erwerbspensumOffset;
			int roundedAnspruch = checkAndRoundAnspruch(verfuegungZeitabschnitt, anspruch);

			verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(roundedAnspruch);
		}
	}

	/**
	 * Sollte der Anspruch weniger als 0 sein, wird dieser auf 0 gesetzt und eine Bemerkung eingefuegt.
	 * Wenn der anspruch groesser als 100 ist, wird dieser auf 100 gesetzt. Hier braucht es keine Bemerkung, denn sie
	 * wurde bereits in calculateErwerbspensum eingefuegt.
	 * Am Ende wird der Wert gerundet und zurueckgegeben
	 */
	private int checkAndRoundAnspruch(@Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt, int anspruch) {
		if (anspruch <= 0) {
            anspruch = 0;
            verfuegungZeitabschnitt.addBemerkung(RuleKey.ERWERBSPENSUM, MsgKey.ERWERBSPENSUM_ANSPRUCH);
            verfuegungZeitabschnitt.setKategorieKeinPensum(true);
        }
        else if (anspruch > 100) { // das Ergebniss darf nie mehr als 100 sein
            anspruch = 100;
        }
		// Der Anspruch wird immer auf 10-er Schritten gerundet.
		return MathUtil.roundIntToTens(anspruch);
	}

	@Nonnull
	private Integer calculateErwerbspensumGS1(@Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		Integer erwerbspensum = verfuegungZeitabschnitt.getErwerbspensumGS1() != null ? verfuegungZeitabschnitt.getErwerbspensumGS1() : 0;
		Integer zuschlag = verfuegungZeitabschnitt.getZuschlagErwerbspensumGS1() != null ? verfuegungZeitabschnitt.getZuschlagErwerbspensumGS1() : 0;
		return calculateErwerbspensum(verfuegungZeitabschnitt, erwerbspensum, zuschlag, MsgKey.ERWERBSPENSUM_GS1_MSG);
	}

	@Nonnull
	private Integer calculateErwerbspensumGS2(@Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		Integer erwerbspensum = verfuegungZeitabschnitt.getErwerbspensumGS2() != null ? verfuegungZeitabschnitt.getErwerbspensumGS2() : 0;
		Integer zuschlag = verfuegungZeitabschnitt.getZuschlagErwerbspensumGS2() != null ? verfuegungZeitabschnitt.getZuschlagErwerbspensumGS2() : 0;
		return calculateErwerbspensum(verfuegungZeitabschnitt, erwerbspensum, zuschlag, MsgKey.ERWERBSPENSUM_GS2_MSG);
	}

	@Nonnull
	private Integer calculateErwerbspensum(VerfuegungZeitabschnitt verfuegungZeitabschnitt, Integer erwerbspensum, Integer zuschlag, MsgKey bemerkung) {
		if (erwerbspensum > 100 || (erwerbspensum + zuschlag) > 100) {
			if (erwerbspensum > 100) {
				erwerbspensum = 100;
			}
			verfuegungZeitabschnitt.addBemerkung(RuleKey.ERWERBSPENSUM, bemerkung);
		}
		return erwerbspensum;
	}

	/**
	 * Nimmt alle Zuschlag-Werte von der Abschnitt und addiert sie. Das Ergebniss darf den macimalen
	 * Wert nicht ueberschreiten, der als EbeguParam definiert wurde. Sollte dieser ueberschritten werden,
	 * wird dieser mavimale Wert zurueckgegeben und ein passendes Kommentar eingefuegt.
	 */
	private int calculateTotalZuschlag(VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		int result = verfuegungZeitabschnitt.getZuschlagErwerbspensumGS1() != null ? verfuegungZeitabschnitt.getZuschlagErwerbspensumGS1() : 0;
		if (verfuegungZeitabschnitt.isHasSecondGesuchsteller()) {
			result += verfuegungZeitabschnitt.getZuschlagErwerbspensumGS2() != null ? verfuegungZeitabschnitt.getZuschlagErwerbspensumGS2() : 0;
		}
		if (result > maxZuschlagValue) {
			verfuegungZeitabschnitt.addBemerkung(RuleKey.ERWERBSPENSUM, MsgKey.ERWERBSPENSUM_MAX_ZUSCHLAG);
			result = maxZuschlagValue;
		}
		return result;
	}

	private boolean hasSecondGSForZeit(@Nonnull Betreuung betreuung, @Nonnull DateRange gueltigkeit) {
		final Gesuch gesuch = betreuung.extractGesuch();
		if (gesuch.extractFamiliensituation().getAenderungPer() != null && gesuch.extractFamiliensituationErstgesuch() != null
		    && gueltigkeit.getGueltigBis().isBefore(gesuch.extractFamiliensituation().getAenderungPer())) {
			return gesuch.extractFamiliensituationErstgesuch().hasSecondGesuchsteller();
		}
		else {
			return gesuch.extractFamiliensituation().hasSecondGesuchsteller();
		}
	}
}

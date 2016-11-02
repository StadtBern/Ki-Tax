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
 * Verweis 16.9.2
 */
public class ErwerbspensumCalcRule extends AbstractCalcRule {

	public ErwerbspensumCalcRule(DateRange validityPeriod) {
		super(RuleKey.ERWERBSPENSUM, RuleType.GRUNDREGEL_CALC, validityPeriod);
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		Objects.requireNonNull(betreuung.extractGesuch(), "Gesuch muss gesetzt sein");
		Objects.requireNonNull(betreuung.extractGesuch().getFamiliensituation(), "Familiensituation muss gesetzt sein");
		boolean hasSecondGesuchsteller = hasSecondGSForZeit(betreuung, verfuegungZeitabschnitt.getGueltigkeit());
		int erwerbspensumOffset = hasSecondGesuchsteller ? 100 : 0;
		// Erwerbspensum ist immer die erste Rule, d.h. es wird das Erwerbspensum mal als Anspruch angenommen
		// Das Erwerbspensum muss PRO GESUCHSTELLER auf 100% limitiert werden
		Integer erwerbspensum1 = verfuegungZeitabschnitt.getErwerbspensumGS1() != null ? verfuegungZeitabschnitt.getErwerbspensumGS1() : 0;
		if (erwerbspensum1 > 100) {
			erwerbspensum1 = 100;
			verfuegungZeitabschnitt.addBemerkung(RuleKey.ERWERBSPENSUM ,  MsgKey.ERWERBSPENSUM_GS1_MSG);
		}
		Integer erwerbspensum2 = 0;
		if (hasSecondGesuchsteller) {
			erwerbspensum2 = verfuegungZeitabschnitt.getErwerbspensumGS2() != null ? verfuegungZeitabschnitt.getErwerbspensumGS2() : 0;
			if (erwerbspensum2 > 100) {
				erwerbspensum2 = 100;
				verfuegungZeitabschnitt.addBemerkung(RuleKey.ERWERBSPENSUM, MsgKey.ERWERBSPENSUM_GS2_MSG);
			}
		}
		int anspruch = erwerbspensum1 + erwerbspensum2 - erwerbspensumOffset;
		if (anspruch <= 0) {
			anspruch = 0;
			verfuegungZeitabschnitt.addBemerkung(RuleKey.ERWERBSPENSUM, MsgKey.ERWERBSPENSUM_ANSPRUCH);
		}
		// Der Anspruch wird immer auf 10-er Schritten gerundet.
		int roundedAnspruch = MathUtil.roundIntToTens(anspruch);
		verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(roundedAnspruch);
	}

	private boolean hasSecondGSForZeit(@Nonnull Betreuung betreuung, @Nonnull DateRange gueltigkeit) {
		final Gesuch gesuch = betreuung.extractGesuch();
		if (gesuch.getFamiliensituation().getAenderungPer() != null && gesuch.getFamiliensituationErstgesuch() != null
		    && gueltigkeit.getGueltigBis().isBefore(gesuch.getFamiliensituation().getAenderungPer())) {
			return gesuch.getFamiliensituationErstgesuch().hasSecondGesuchsteller();
		}
		else {
			return gesuch.getFamiliensituation().hasSecondGesuchsteller();
		}
	}
}

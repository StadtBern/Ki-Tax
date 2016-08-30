package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;

/**
 * Berechnet als letzte Rule den Rest-Anspruch, welcher ins naechste Betreuungsangebot uebergeht
 */
public class RestanspruchCalcRule extends AbstractCalcRule {

	public RestanspruchCalcRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.NO_RULE, RuleType.NO_RULE, validityPeriod);
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		if (betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()) {
			int anspruchberechtigtesPensum = verfuegungZeitabschnitt.getAnspruchberechtigtesPensum();
			int betreuungspensum = verfuegungZeitabschnitt.getBetreuungspensum();
			if (betreuungspensum < anspruchberechtigtesPensum) {
				verfuegungZeitabschnitt.setAnspruchspensumRest(anspruchberechtigtesPensum - betreuungspensum);
			} else {
				verfuegungZeitabschnitt.setAnspruchspensumRest(0);
			}
		}
	}
}

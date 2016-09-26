package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.MathUtil;

import javax.annotation.Nonnull;

/**
 * Regel für die Betreuungspensen. Sie beachtet:
 * - Anspruch aus Betreuungspensum darf nicht höher sein als Erwerbspensum
 * - Nur relevant für Kita, Tageseltern-Kleinkinder, die anderen bekommen so viel wie sie wollen
 * - Falls Kind eine Fachstelle hat, gilt das Pensum der Fachstelle
 * Verweis 16.9.3
 */
public class FachstelleCalcRule extends AbstractCalcRule {

	public FachstelleCalcRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.FACHSTELLE, RuleType.GRUNDREGEL_CALC, validityPeriod);
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		// Ohne Fachstelle: Wird in einer separaten Rule behandelt
		if (betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()) {
			int pensumFachstelle = verfuegungZeitabschnitt.getFachstellenpensum();
			int roundedPensumFachstelle = MathUtil.roundIntToTens(pensumFachstelle);
			if (roundedPensumFachstelle > 0) {
				// Anspruch ist immer genau das Pensum der Fachstelle, ausser das Restpensum lässt dies nicht mehr zu
				verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(roundedPensumFachstelle);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.FACHSTELLE, MsgKey.FACHSTELLE_MSG);
			}
		}
	}
}

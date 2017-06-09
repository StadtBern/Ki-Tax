package ch.dvbern.ebegu.rules;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;

/**
 * Regel für die Kuendigung vor Eintritt in die Institution. Sie beachtet:
 * - Bemerkung wird hinzugefügt wenn Status STORNIERT ist
 * - Pensum ist schon richtig gesezt (0) bei Kita
 */
public class StorniertCalcRule extends AbstractCalcRule {

	public StorniertCalcRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.STORNIERT, RuleType.GRUNDREGEL_CALC, validityPeriod);
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		// Bei Betreuungen mit status STORNIERT wird Bemerkung hinzugefügt
		if (Betreuungsstatus.STORNIERT.equals(betreuung.getBetreuungsstatus())) {
			verfuegungZeitabschnitt.addBemerkung(RuleKey.STORNIERT, MsgKey.STORNIERT_MSG);
		}
	}
}

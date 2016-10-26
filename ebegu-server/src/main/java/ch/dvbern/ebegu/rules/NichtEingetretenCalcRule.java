package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;

/**
 * Regel für die Nicht Eingetreten. Sie beachtet:
 * - Bemerkung wird hinzugefügt wenn Status Nicht Eingetreten ist
 * - Pensum ist schon richtig gesezt (0) bei Kita
 */
public class NichtEingetretenCalcRule extends AbstractCalcRule {

	public NichtEingetretenCalcRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.NICHT_EINGETRETEN, RuleType.GRUNDREGEL_CALC, validityPeriod);
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		// Bei Betreuungen mit status nicht eingetreten wird Bemerkung hinzugefügt
		if (betreuung.getBetreuungsstatus().equals(Betreuungsstatus.NICHT_EINGETRETEN)) {
			verfuegungZeitabschnitt.addBemerkung(RuleKey.NICHT_EINGETRETEN, MsgKey.NICHT_EINGETRETEN_MSG);
		}
	}
}

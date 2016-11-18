package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;

/**
 * Regel für die Kuendigung vor Eintritt in die Institution. Sie beachtet:
 * - Bemerkung wird hinzugefügt wenn Status GEKUENDIGT_VOR_EINTRITT ist
 * - Pensum ist schon richtig gesezt (0) bei Kita
 */
public class GekuendigtVorEintrittCalcRule extends AbstractCalcRule {

	public GekuendigtVorEintrittCalcRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.GEKUENDIGT_VOR_EINTRITT, RuleType.GRUNDREGEL_CALC, validityPeriod);
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		// Bei Betreuungen mit status GEKUENDIGT_VOR_EINTRITT wird Bemerkung hinzugefügt
		if (Betreuungsstatus.GEKUENDIGT_VOR_EINTRITT.equals(betreuung.getBetreuungsstatus())) {
			verfuegungZeitabschnitt.addBemerkung(RuleKey.GEKUENDIGT_VOR_EINTRITT, MsgKey.GEKUENDIGT_VOR_EINTRITT_MSG);
		}
	}
}

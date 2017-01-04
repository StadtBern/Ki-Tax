package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;

/**
 * Umsetzung der ASIV Revision: Finanzielle Situation bei Mutation der Familiensituation anpassen
 * <p>
 * Gem. neuer ASIV Verordnung muss bei einem Wechsel von einem auf zwei Gesuchsteller oder umgekehrt die
 * finanzielle Situation ab dem Folgemonat angepasst werden.
 * </p>
 */
public class ZivilstandsaenderungCalcRule extends AbstractCalcRule {


	public ZivilstandsaenderungCalcRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.ZIVILSTANDSAENDERUNG, RuleType.GRUNDREGEL_CALC, validityPeriod);
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		if (verfuegungZeitabschnitt.isHasSecondGesuchsteller()) {
			verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(verfuegungZeitabschnitt.getMassgebendesEinkommenVorAbzugFamgr_zuZweit());
			verfuegungZeitabschnitt.setEinkommensjahr(verfuegungZeitabschnitt.getEinkommensjahr_zuZweit());
		} else {
			verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(verfuegungZeitabschnitt.getMassgebendesEinkommenVorAbzugFamgr_alleine());
			verfuegungZeitabschnitt.setEinkommensjahr(verfuegungZeitabschnitt.getEinkommensjahr_alleine());
		}
	}
}

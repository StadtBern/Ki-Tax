package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;

/**
 * Regel für Wohnsitz in Bern (Zuzug und Wegzug):
 * - Durch Adresse definiert
 * - Anspruch vom ersten Tag des Zuzugs
 * - Anspruch bis 2 Monate nach Wegzug, auf Ende Monat
 * Verweis 16.8 Der zivilrechtliche Wohnsitz
 */
public class WohnsitzCalcRule extends AbstractCalcRule {


	public WohnsitzCalcRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.WOHNSITZ, RuleType.REDUKTIONSREGEL, validityPeriod);
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		if (betreuung.getBetreuungsangebotTyp().isJugendamt()) {
			if (areNotInBern(betreuung, verfuegungZeitabschnitt)) {
				verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(0);
				verfuegungZeitabschnitt.addBemerkung(RuleKey.WOHNSITZ, MsgKey.WOHNSITZ_MSG);
			}

		}
	}

	/**
	 * Zuerst schaut ob es eine Aenderung in der Familiensituation gab. Dementsprechend nimmt es die richtige Familiensituation
	 * um zu wissen ob es ein GS2 gibt, erst dann wird es geprueft ob die Adressen von GS1 oder GS2 in Bern sind
	 * @param betreuung
	 * @param verfuegungZeitabschnitt
	 * @return
	 */
	private boolean areNotInBern(Betreuung betreuung, VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		boolean hasSecondGesuchsteller = false;
		final Gesuch gesuch = betreuung.extractGesuch();
		if (gesuch.getFamiliensituation() != null && gesuch.getFamiliensituation().getAenderungPer() != null
			&& !gesuch.getFamiliensituation().getAenderungPer().isAfter(verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb())) {

			hasSecondGesuchsteller = gesuch.getFamiliensituation().hasSecondGesuchsteller();
		}
		else if (gesuch.getFamiliensituationErstgesuch() != null) {
			hasSecondGesuchsteller =  gesuch.getFamiliensituationErstgesuch().hasSecondGesuchsteller();
		}
		return (hasSecondGesuchsteller
			&& verfuegungZeitabschnitt.isWohnsitzNichtInGemeindeGS1()
			&& verfuegungZeitabschnitt.isWohnsitzNichtInGemeindeGS2())
				|| (!hasSecondGesuchsteller
				&& verfuegungZeitabschnitt.isWohnsitzNichtInGemeindeGS1());

	}

}

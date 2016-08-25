package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.MathUtil;

import javax.annotation.Nonnull;

/**
 * Regel: Es wird geprueft ob das Kind im gleichen Haushalt wie die Eltern wohnt. Sollte es der Fall sein, wird
 * die eingegebene Prozentzahl als maximaler Wert fuer das Betreuungspensum gesetzt.
 */
public class WohnhaftImGleichenHaushaltCalcRule extends AbstractCalcRule {

	public WohnhaftImGleichenHaushaltCalcRule(DateRange validityPeriod) {
		super(RuleKey.WOHNHAFT_IM_GLEICHEN_HAUSHALT, RuleType.GRUNDREGEL_CALC, validityPeriod);
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		if (betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()) {
			if (betreuung.getKind() != null && betreuung.getKind().getKindJA() != null) {
				final Kind kindJA = betreuung.getKind().getKindJA();
				if (kindJA.getWohnhaftImGleichenHaushalt() != null) {
					int pensumGleicherHaushalt = MathUtil.roundIntToTens(kindJA.getWohnhaftImGleichenHaushalt());
					int anspruch = verfuegungZeitabschnitt.getAnspruchberechtigtesPensum();
					if (pensumGleicherHaushalt < anspruch) {
						anspruch = pensumGleicherHaushalt;
						verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(anspruch);
						verfuegungZeitabschnitt.addBemerkung(RuleKey.WOHNHAFT_IM_GLEICHEN_HAUSHALT.name() + ": Das Kind wohnt "
							+ kindJA.getWohnhaftImGleichenHaushalt() + "% im gleichen Haushalt");
					}
				}
			}
		}
	}
}

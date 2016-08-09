package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.MathUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Regel: Es wird geprueft ob das Kind im gleichen Haushalt wie die Eltern wohnt. Sollte es der Fall sein, wird
 * die eingegebene Prozentzahl als maximaler Wert fuer das Betreuungspensum gesetzt.
 */
public class WohnhaftImGleichenHaushaltRule extends AbstractEbeguRule {

	public WohnhaftImGleichenHaushaltRule(DateRange validityPeriod) {
		super(RuleKey.BETREUUNGSANGEBOT_TYP, RuleType.REDUKTIONSREGEL, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		return new ArrayList<>();
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		if (betreuung.getKind() != null && betreuung.getKind().getKindJA() != null) {
			final Kind kindJA = betreuung.getKind().getKindJA();
			if (kindJA.getWohnhaftImGleichenHaushalt() != null && kindJA.getWohnhaftImGleichenHaushalt() < verfuegungZeitabschnitt.getAnspruchberechtigtesPensum()) {
					verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(MathUtil.roundIntToTens(kindJA.getWohnhaftImGleichenHaushalt()));
					verfuegungZeitabschnitt.addBemerkung(RuleKey.WOHNHAFT_IM_GLEICHEN_HAUSHALT.name() + ": Das Kind wohnt "
						+ kindJA.getWohnhaftImGleichenHaushalt() + "% im gleichen Haushalt");
			}
		}
	}
}

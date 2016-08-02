package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.PensumFachstelle;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Regel für die Fachstelle. Sucht das PensumFachstelle falls vorhanden und wenn ja wird ein entsprechender
 * Zeitabschnitt generiert
 * Verweis 16.13 Fachstelle
 */
public class FachstelleDataRule extends AbstractAbschnittRule {

	public FachstelleDataRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.FACHSTELLE, RuleType.GRUNDREGEL_DATA, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> betreuungspensumAbschnitte = new ArrayList<>();
		PensumFachstelle pensumFachstelle = betreuung.getKind().getKindJA().getPensumFachstelle();
		if (pensumFachstelle != null) {
			betreuungspensumAbschnitte.add(toVerfuegungZeitabschnitt(pensumFachstelle));
		}
		return betreuungspensumAbschnitte;
	}


	@Nonnull
	private VerfuegungZeitabschnitt toVerfuegungZeitabschnitt(@Nonnull PensumFachstelle pensumFachstelle) {
		VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(pensumFachstelle.getGueltigkeit());
		zeitabschnitt.setFachstellenpensum(pensumFachstelle.getPensum());
		return zeitabschnitt;
	}
}

package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungspensum;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Regel für die Erstellung der Zeitabschnitte der Betreuungspensen
 * Verweis 16.9.3
 */
public class BetreuungspensumAbschnittRule extends AbstractAbschnittRule {

	public BetreuungspensumAbschnittRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.BETREUUNGSPENSUM, RuleType.GRUNDREGEL_DATA, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> betreuungspensumAbschnitte = new ArrayList<>();
		Set<BetreuungspensumContainer> betreuungspensen = betreuung.getBetreuungspensumContainers();
		for (BetreuungspensumContainer betreuungspensumContainer : betreuungspensen) {
			Betreuungspensum betreuungspensum = betreuungspensumContainer.getBetreuungspensumJA();
			betreuungspensumAbschnitte.add(toVerfuegungZeitabschnitt(betreuungspensum));
		}
		return betreuungspensumAbschnitte;
	}


	/**
	 * @param betreuungspensum zu konvertiertendes Betreuungspensum
	 * @return VerfuegungZeitabschnitt mit gleicher gueltigkeit und uebernommenem betreuungspensum
	 */
	@Nonnull
	private VerfuegungZeitabschnitt toVerfuegungZeitabschnitt(@Nonnull Betreuungspensum betreuungspensum) {
		VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(betreuungspensum.getGueltigkeit());
		zeitabschnitt.setBetreuungspensum(betreuungspensum.getPensum());
		return zeitabschnitt;
	}
}

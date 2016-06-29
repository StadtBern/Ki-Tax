package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
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
 * Regel für die Betreuungspensen. Sie beachtet:
 * - Anspruch aus Betreuungspensum darf nicht höher sein als Erwerbspensum
 * - Nur relevant für Kita, Tageseltern-Kleinkinder, die anderen bekommen so viel wie sie wollen
 * - Falls Kind eine Fachstelle hat, gilt das Pensum der Fachstelle
 */
public class BetreuungspensumDataRule extends AbstractEbeguRule {

	public BetreuungspensumDataRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.BETREUUNGSPENSUM, RuleType.GRUNDREGEL_DATA, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {
		List<VerfuegungZeitabschnitt> betreuungspensumAbschnitte = new ArrayList<>();
		Set<BetreuungspensumContainer> betreuungspensen = betreuung.getBetreuungspensumContainers();
		for (BetreuungspensumContainer betreuungspensumContainer : betreuungspensen) {
			Betreuungspensum betreuungspensum = betreuungspensumContainer.getBetreuungspensumJA();
			betreuungspensumAbschnitte.add(toVerfuegungZeitabschnitt(betreuungspensum));
		}
		return betreuungspensumAbschnitte;
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
	}

	@Nonnull
	private VerfuegungZeitabschnitt toVerfuegungZeitabschnitt(@Nonnull Betreuungspensum betreuungspensum) {
		VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(betreuungspensum.getGueltigkeit());
		zeitabschnitt.setBetreuungspensum(betreuungspensum.getPensum());
		return zeitabschnitt;
	}
}

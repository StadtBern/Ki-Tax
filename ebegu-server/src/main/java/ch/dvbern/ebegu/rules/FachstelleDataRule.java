package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.PensumFachstelle;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Regel für die Betreuungspensen. Sie beachtet:
 * - Anspruch aus Betreuungspensum darf nicht höher sein als Erwerbspensum
 * - Nur relevant für Kita, Tageseltern-Kleinkinder, die anderen bekommen so viel wie sie wollen
 * - Falls Kind eine Fachstelle hat, gilt das Pensum der Fachstelle
 */
public class FachstelleDataRule extends AbstractEbeguRule {

	public FachstelleDataRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.FACHSTELLE, RuleType.GRUNDREGEL_DATA, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {
		List<VerfuegungZeitabschnitt> betreuungspensumAbschnitte = new ArrayList<>();
		PensumFachstelle pensumFachstelle = betreuung.getKind().getKindJA().getPensumFachstelle();
		if (pensumFachstelle != null) {
			betreuungspensumAbschnitte.add(toVerfuegungZeitabschnitt(pensumFachstelle));
		}
		return betreuungspensumAbschnitte;
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
	}

	@Nonnull
	private VerfuegungZeitabschnitt toVerfuegungZeitabschnitt(@Nonnull PensumFachstelle pensumFachstelle) {
		VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(pensumFachstelle.getGueltigkeit());
		zeitabschnitt.setFachstellenpensum(pensumFachstelle.getPensum());
		return zeitabschnitt;
	}
}

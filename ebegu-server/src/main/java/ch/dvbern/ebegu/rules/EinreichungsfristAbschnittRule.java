package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Regel bezüglich der Einreichungsfrist des Gesuchs:
 * - Wird ein Gesuch zu spät eingereicht, entfällt der Anspruch auf den Monaten vor dem Einreichen des Gesuchs.
 * - Beispiel: Ein Gesuch wird am 5. September 2017 eingereicht. In diesem Fall ist erst per 1. September 2017
 * 		ein Anspruch verfügbar.
 *		D.h. für die Angebote „Kita“ und „Tageseltern – Kleinkinder“ ist im August kein Anspruch verfügbar.
 *		Falls sie einen Platz haben, wird dieser zum privaten Tarif der Kita berechnet.
 * - Für die Angebote Tageseltern–Schulkinder und Tagesstätten entspricht der Anspruch dem gewünschten Pensum.
 * 		Ihnen wird für den Monat August aber der Volltarif verrechnet.
 * 	Verweis 16.11 Gesuch zu Speat
 *
 * 	TODO: Diese Regel soll nur beim Erstgesuch gelten jedoch nicht bei Mutationen! Xaver fragen ob dies so richtig ist!
 */
public class EinreichungsfristAbschnittRule extends AbstractAbschnittRule {

	public EinreichungsfristAbschnittRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.EINREICHUNGSFRIST, RuleType.GRUNDREGEL_DATA, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> einreichungsfristAbschnitte = new ArrayList<>();
		Set<BetreuungspensumContainer> betreuungspensen = betreuung.getBetreuungspensumContainers();
		Gesuch gesuch = betreuung.extractGesuch();
		LocalDate eingangsdatum = gesuch.getEingangsdatum();
		if (betreuung.extractGesuch().getTyp().equals(AntragTyp.GESUCH) && eingangsdatum != null) {
			LocalDate firstOfMonthDesEinreichungsMonats = LocalDate.of(eingangsdatum.getYear(), eingangsdatum.getMonth(), 1);
			for (BetreuungspensumContainer betreuungspensumContainer : betreuungspensen) {
				Betreuungspensum betreuungspensum = betreuungspensumContainer.getBetreuungspensumJA();
				if (betreuungspensum.getGueltigkeit().getGueltigAb().isBefore(firstOfMonthDesEinreichungsMonats)) {
					VerfuegungZeitabschnitt verfuegungZeitabschnitt = new VerfuegungZeitabschnitt(betreuungspensum.getGueltigkeit());
					// Der Anspruch beginnt erst am 1. des Monats der Einreichung
					verfuegungZeitabschnitt.getGueltigkeit().setGueltigBis(firstOfMonthDesEinreichungsMonats.minusDays(1));
					verfuegungZeitabschnitt.setZuSpaetEingereicht(true);
					// Sicherstellen, dass nicht der ganze Zeitraum vor dem Einreichungsdatum liegt
					if (verfuegungZeitabschnitt.getGueltigkeit().getGueltigBis().isAfter(verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb())) {
						einreichungsfristAbschnitte.add(verfuegungZeitabschnitt);
					}
				}
			}
		}
		return einreichungsfristAbschnitte;
	}
}

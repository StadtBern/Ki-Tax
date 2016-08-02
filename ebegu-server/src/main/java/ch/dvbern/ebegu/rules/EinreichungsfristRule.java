package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
 */
public class EinreichungsfristRule extends AbstractEbeguRule {

	public EinreichungsfristRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.EINREICHUNGSFRIST, RuleType.REDUKTIONSREGEL, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		return new ArrayList<>();
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {

	}
}

package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * TODO (hefr) Ist dies eine "Regel"???
 * Regeln bezüglich Familiensituation:
 * Die Änderung der Familiensituation hat nur einen Einfluss, falls sich dadurch die Situation der Gesuchsteller ändert.
 * Bei der Innerhalb einer Periode sind diesbezüglich folgende Konstellationen erlaubt:
 * - Wechsel von einem Gesuchsteller auf zwei Gesuchsteller (z.B. Heirat)
 * - Wechsel von zwei Gesuchsteller auf einen Gesuchsteller (z.B. nach einer Scheidung)
 *
 * - Nicht möglich ist die Änderung des Gesuchstellers 2. Gesuchsteller 1 muss immer die angemeldete Person sein.
 * 		Der Name und Vorname wird aus dem IAM übernommen. Innerhalb einer Periode kann die Anzahl der Gesuchsteller
 * 		nur einmal angepasst werden.
 * 	- Bei einem Wechsel von einem Gesuchsteller auf zwei Gesuchsteller werden bei der Mutation die Stammdaten des
 * 		Gesuchstellers 2 entfernt. Das Erwerbspensum bleibt bestehen und wird bis hin zum Ereignis (z.B. Scheidung) berücksichtigt.
 * - Bei der Berechnung des neuen Anspruchs gelten die Regeln immer auf den Eintritt des Ereignisses der Änderung der Familiensituation!
 * - Unabhängig von Einreichungsdatum
 */
public class FamiliensituationRule extends AbstractEbeguRule {

	public FamiliensituationRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.FAMILIENSITUATION, RuleType.REDUKTIONSREGEL, validityPeriod);
	}

	@Nonnull
	@Override
	protected Collection<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull BetreuungspensumContainer betreuungspensumContainer, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {
		return null;
	}

	@Override
	protected void executeRule(@Nonnull BetreuungspensumContainer betreuungspensumContainer, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {

	}
}

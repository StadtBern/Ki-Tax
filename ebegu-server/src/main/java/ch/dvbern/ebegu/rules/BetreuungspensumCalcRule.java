package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;

/**
 * Regel für die Betreuungspensen. Sie beachtet:
 * - Anspruch aus Betreuungspensum darf nicht höher sein als Erwerbspensum
 * - Nur relevant für Kita, Tageseltern-Kleinkinder, die anderen bekommen so viel wie sie wollen
 * - Falls Kind eine Fachstelle hat, gilt das Pensum der Fachstelle
 * Verweis 16.9.3
 */
public class BetreuungspensumCalcRule extends AbstractCalcRule {

	public BetreuungspensumCalcRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.BETREUUNGSPENSUM, RuleType.GRUNDREGEL_CALC, validityPeriod);
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		// Betreuungspensum darf nie mehr als 100% sein
		int betreuungspensum = verfuegungZeitabschnitt.getBetreuungspensum();
		if (betreuungspensum > 100) { // sollte nie passieren
			betreuungspensum = 100;
		}
		// Fachstelle: Wird in einer separaten Rule behandelt
		// Anspruch setzen fuer Schulkinder; bei Kleinkindern muss nichts gemacht werden
		if (betreuung.getBetreuungsangebotTyp().isAngebotJugendamtSchulkind()) {
			// Schulkind-Angebote: Sie erhalten IMMER soviel, wie sie wollen. Der Restanspruch wird nicht tangiert
			verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(betreuungspensum);
		}
	}
}

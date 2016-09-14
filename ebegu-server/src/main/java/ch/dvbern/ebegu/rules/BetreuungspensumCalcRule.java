package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.MathUtil;

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
		if (betreuungspensum > 100) {
			betreuungspensum = 100;
			verfuegungZeitabschnitt.addBemerkung(RuleKey.BETREUUNGSPENSUM , MsgKey.BETREUUNGSPENSUM_MSG);
		}
		// Fachstelle: Wird in einer separaten Rule behandelt
		int pensumFachstelle = verfuegungZeitabschnitt.getFachstellenpensum();
		int roundedPensumFachstelle = MathUtil.roundIntToTens(pensumFachstelle);
		if (roundedPensumFachstelle <= 0) {
			// Keine Fachstelle
			if (betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()) {
				// Kita und Tageseltern-Kleinkinder:
				// Sie bekommen maximal soviel, wie der (Rest-) Anspruch ist
				int anspruchRest = verfuegungZeitabschnitt.getAnspruchspensumRest();
				if (betreuungspensum > anspruchRest) {
					verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(anspruchRest);
					//todo homa kommentar
					//verfuegungZeitabschnitt.addBemerkung(RuleKey.BETREUUNGSPENSUM.name() + ": Betreuungspensum wurde auf 100% limitiert");

				}
			} else if (betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp().isAngebotJugendamtSchulkind()) {
				// Schulkind-Angebote: Sie erhalten IMMER soviel, wie sie wollen. Der Restanspruch wird nicht tangiert
				//TODO (team) In Excel Tests ist immer 100%, nicht gewünschtes Pensum
				verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(100);
			}
		}
	}
}

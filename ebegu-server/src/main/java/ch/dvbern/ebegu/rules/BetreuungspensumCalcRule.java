package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
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
		// Initialisierung: Wir geben mal das gewünschte Pensum. Falls dies gekürzt werden muss, so geschieht dies später
		int betreuungberechnet = verfuegungZeitabschnitt.getBetreuungspensum();
		int anspruchRest = verfuegungZeitabschnitt.getAnspruchspensumRest();
		// Das Betreuungspensum darf pro Betreuung nie mehr als 100% betragen (gilt für alle Angebote)
		if (betreuungberechnet > 100) {
			betreuungberechnet = 100;
			verfuegungZeitabschnitt.addBemerkung(RuleKey.BETREUUNGSPENSUM.name() + ": Betreuungspensum wurde auf 100% limitiert");
		}
		// Fachstelle: Überschreibt alles
		int pensumFachstelle = verfuegungZeitabschnitt.getFachstellenpensum();
		int roundedPensumFachstelle = MathUtil.roundIntToTens(pensumFachstelle);
		if (roundedPensumFachstelle > 0) {
			// Anspruch ist immer genau das Pensum der Fachstelle
			betreuungberechnet = roundedPensumFachstelle;
			// Den neuen "AnspruchRest" bestimmen:
			if (roundedPensumFachstelle > verfuegungZeitabschnitt.getBetreuungspensum()) {
				anspruchRest = roundedPensumFachstelle - verfuegungZeitabschnitt.getBetreuungspensum();
			} else {
				anspruchRest = 0;
			}
		} else if (betreuung.isAngebotKita() || betreuung.isAngebotTageselternKleinkinder()) {
			// Kita und Tageseltern-Kleinkinder: Anspruch ist das kleinere von Betreuungspensum und Erwerbspensum
			betreuungberechnet = Math.min(betreuungberechnet, verfuegungZeitabschnitt.getAnspruchberechtigtesPensum());

			// Ausserdem: Nur soviel, wie noch nicht von einer anderen Kita oder Tageseltern Kleinkinder verwendet wurde:
			betreuungberechnet = Math.min(betreuungberechnet, verfuegungZeitabschnitt.getAnspruchspensumRest());
			// Den neuen "AnspruchRest" bestimmen:
			anspruchRest = verfuegungZeitabschnitt.getAnspruchspensumRest() - betreuungberechnet;
		}
		// Den berechneten Wert setzen, sowie den Restanspruch aktualisieren
		verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(betreuungberechnet);
		verfuegungZeitabschnitt.setAnspruchspensumRest(anspruchRest);
	}
}

package ch.dvbern.ebegu.rules.initalizer;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Hilfsklasse die nach der eigentlich Evaluation einer Betreuung angewendet wird um den Restanspruch zu uebernehmen fuer die
 * Berechnung der nachsten Betreuung.
 * Ermittelt des Restanspruch aus den übergebenen Zeitabschnitten und erstellt neue Abschnitte mit nur dieser Information
 * für die Berechnung der nächsten Betreuung. Diese werden als initiale Zeitabschnitte der nachsten Betreuung verwendet
 * Bei Angeboten fuer Schulkinder ist der Restanspruch nicht tangiert
 * Verweis 15.9.5
 *
 * <h3>Vorgehensskizze Restanspruchberechnung</h3>
 * <ul>
 *  <li>Der Restanspruch ist bei der ersten Betreuung auf -1 gesetzt</li>
 *  <li>Wir berechnen die Verfügung für diese erste Betreuung. Dabei wird in allen Regeln die den Anspruch benoetigen das Feld AnspruchberechtigtesPensum verwendet (nicht AnspruchspensumRest)</li>
 * <li> Als allerletzte Reduktionsregel läuft eine Regel die das Feld "AnspruchberechtigtesPensum" mit dem Feld<
 *   "AnspruchspensumRest" vergleicht. Wenn letzteres -1 ist gilt der Wert im Feld "AnspruchsberechtigtesPensum, ansonsten wir das Minimum der beiden Felder in das Feld "AnspruchberechtigtesPensum" gesetzt. </li>
 *  <li>Bevor die nächste Betreuung verfügt wird, berechnen wir den noch verfügbaren Restanspruch indem wir "AnspruchberechtigtesPensum" - "betreuungspensum" rechnen und das Resultat in das Feld "AnspruchspensumRest" schreiben</li>
 </ul>
 Die 2. Betreuung wird genau wie die erste durchgeführt. Nun wird allerdings die allerletzte Reduktionsregel den Anspruch reduzieren auf den gesetzten Restanspruch.
 */
public class RestanspruchInitializer {


	public RestanspruchInitializer() {
	}

	@Nonnull
	public List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> restanspruchsZeitabschnitte = new ArrayList<>();
		for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnitte) {
			VerfuegungZeitabschnitt restanspruchsAbschnitt = new VerfuegungZeitabschnitt(zeitabschnitt.getGueltigkeit());
			restanspruchUebernehmen(betreuung, zeitabschnitt, restanspruchsAbschnitt);
			restanspruchsZeitabschnitte.add(restanspruchsAbschnitt);
		}
		return restanspruchsZeitabschnitte;
	}

	protected void restanspruchUebernehmen(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt sourceZeitabschnitt, VerfuegungZeitabschnitt targetZeitabschnitt) {
		//Die  vom der letzen Berechnung uebernommenen Zeitabschnitte betrachten und den restanspruch berechnen.
		if (betreuung.getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()) {
			int anspruchberechtigtesPensum = sourceZeitabschnitt.getAnspruchberechtigtesPensum();
			int betreuungspensum = sourceZeitabschnitt.getBetreuungspensum();
			//wenn nicht der ganze anspruch gebraucht wird gibt es einen rest, ansonsten ist rest 0
			if (betreuungspensum < anspruchberechtigtesPensum) {
				targetZeitabschnitt.setAnspruchspensumRest(anspruchberechtigtesPensum - betreuungspensum);
			} else {
				targetZeitabschnitt.setAnspruchspensumRest(0);
			}
		} else if (betreuung.getBetreuungsangebotTyp().isAngebotJugendamtSchulkind()) {
			// Schulkind-Angebote: Die aktuelle Betreuung ist ein Schulkind Angebot. Diese verkleinern den Restanspruch nicht
			// der aktuelle Restanspruch wird also AS-IS auf die nachste Betreuung uebernommen
			targetZeitabschnitt.setAnspruchspensumRest(sourceZeitabschnitt.getAnspruchspensumRest());
		}
	}
}

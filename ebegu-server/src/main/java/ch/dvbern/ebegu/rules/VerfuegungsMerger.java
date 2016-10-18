package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.types.DateRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Sonderregel das Ergenis der aktuellen Berechnung mit der Vorhergehenden merged.
 * <p>
 * Anspruchsberechnungsregeln für Mutationen
 * <p>
 * Entscheidend ist, ob die Meldung des Arbeitspensum frühzeitig gemeldet wird:
 * Eine Änderung des Arbeitspensums ist rechtzeitig, falls die Änderung im Vormonat gemeldet wird.
 * <p>
 * Rechtzeitige Meldung:In diesem Fall wird der Anspruch zusammen mit dem Ereigniseintritt des Arbeitspensums angepasst.
 * <p>
 * Verspätete Meldung: Wird die Änderung des Arbeitspensums im Monat des Ereignis oder noch später gemeldet, erfolgt eine ERHÖHUNG des Anspruchs erst auf den Folgemonat
 * <p>
 * Im Falle einer Herabsetzung des Arbeitspensums, wird der Anspruch zusammen mit dem Ereigniseintritt angepasst
 * <p>
 * Dieselbe Regeln gilt für sämtliche Berechnungen des Anspruchs, d.h. auch für Fachstellen. Grundsätzlich lässt sich sagen:
 * Der Anspruch kann sich erst auf den Folgemonat des Eingangsdatum erhöhen
 * Reduktionen des Anspruchs sind auch rückwirkend erlaubt
 */
public class VerfuegungsMerger {

	private static final Logger LOG = LoggerFactory.getLogger(VerfuegungsMerger.class.getSimpleName());

	/**
	 * Um code lesbar zu halten wird die Regel PMD.CollapsibleIfStatements ausgeschaltet
	 */
	@Nonnull
	@SuppressWarnings("PMD.CollapsibleIfStatements")
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, Gesuch gesuchForMutaion) {

		// Wenn keine Mutation vorhanden ist muss nicht gemerged werden
		if (gesuchForMutaion == null) {
			return zeitabschnitte;
		}

		final Betreuung betreuungGSM = findBetreuungOnGesuchForMuation(betreuung, gesuchForMutaion);
		if (betreuungGSM == null) {
			return zeitabschnitte;
		}

		final LocalDate mutationsEingansdatum = betreuung.extractGesuch().getEingangsdatum();

		List<VerfuegungZeitabschnitt> monatsSchritte = new ArrayList<>();

		for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : zeitabschnitte) {
			final LocalDate zeitabschnittStart = verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb();
			final int anspruchberechtigtesPensum = verfuegungZeitabschnitt.getAnspruchberechtigtesPensum();

			final int anspruchberechtigtesPensumGSM = findAnspruchberechtigtesPensumAt(zeitabschnittStart, betreuungGSM);
			VerfuegungZeitabschnitt zeitabschnitt = copy(verfuegungZeitabschnitt);

			if (anspruchberechtigtesPensum > anspruchberechtigtesPensumGSM) {
				//Anspruch wird erhöht
				//Meldung rechtzeitig: In diesem Fall wird der Anspruch zusammen mit dem Ereigniseintritt des Arbeitspensums angepasst. -> keine Aenderungen
				if (!isMeldungRechzeitig(verfuegungZeitabschnitt, mutationsEingansdatum)) {
					//Meldung nicht Rechtzeitig: Der Anspruch kann sich erst auf den Folgemonat des Eingangsdatum erhöhen
					zeitabschnitt.setAnspruchberechtigtesPensum(anspruchberechtigtesPensumGSM);
					zeitabschnitt.addBemerkung(RuleKey.ANSPRUCHSBERECHNUNGSREGELN_MUTATIONEN, MsgKey.ANSPRUCHSAENDERUNG_MSG);
				}
			} else if (anspruchberechtigtesPensum < anspruchberechtigtesPensumGSM) {
				//Anspruch wird kleiner
				//Meldung rechtzeitig: In diesem Fall wird der Anspruch zusammen mit dem Ereigniseintritt des Arbeitspensums angepasst. -> keine Aenderungen
				if (!isMeldungRechzeitig(verfuegungZeitabschnitt, mutationsEingansdatum)) {
					//Meldung nicht Rechtzeitig: Reduktionen des Anspruchs sind auch rückwirkend erlaubt -> keine Aenderungen
					zeitabschnitt.addBemerkung(RuleKey.ANSPRUCHSBERECHNUNGSREGELN_MUTATIONEN, MsgKey.REDUCKTION_RUECKWIRKEND_MSG);
				}
			}
			monatsSchritte.add(zeitabschnitt);
		}

		return monatsSchritte;
	}

	private boolean isMeldungRechzeitig(VerfuegungZeitabschnitt verfuegungZeitabschnitt, LocalDate mutationsEingansdatum) {
		return verfuegungZeitabschnitt.getGueltigkeit().startsSameDay(mutationsEingansdatum) ||
			verfuegungZeitabschnitt.getGueltigkeit().startsAfter(mutationsEingansdatum);

	}

	/**
	 * Findet das anspruchberechtigtes Pensum zum Zeitpunkt des neuen Zeitabschnitt-Start
	 */
	private int findAnspruchberechtigtesPensumAt(LocalDate zeitabschnittStart, Betreuung betreuungGSM) {
		for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : betreuungGSM.getVerfuegung().getZeitabschnitte()) {
			final DateRange gueltigkeit = verfuegungZeitabschnitt.getGueltigkeit();
			if (gueltigkeit.contains(zeitabschnittStart) || gueltigkeit.startsSameDay(zeitabschnittStart)) {
				return verfuegungZeitabschnitt.getAnspruchberechtigtesPensum();
			}
		}
		LOG.error("Anspruch berechtigtes Pensum beim Gesuch für Mutation konnte nicht gefunden werden");
		return 0;
	}

	private VerfuegungZeitabschnitt copy(VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(verfuegungZeitabschnitt);
		//zeitabschnitt.add(verfuegungZeitabschnitt);
		return zeitabschnitt;
	}

	private Betreuung findBetreuungOnGesuchForMuation(Betreuung betreuung, Gesuch gesuchForMutaion) {

		for (KindContainer kindContainer : gesuchForMutaion.getKindContainers()) {
			if (kindContainer.getKindNummer().equals(betreuung.getKind().getKindNummer())) {
				for (Betreuung betreuungGSM : kindContainer.getBetreuungen()) {
					if (betreuungGSM.getBetreuungNummer().equals(betreuung.getBetreuungNummer())) {
						return betreuungGSM;
					}
				}
			}
		}
		LOG.error("Betreuung zum mergen konnte nicht gefunden werden");
		return null;
	}
}

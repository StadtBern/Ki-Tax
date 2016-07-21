package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Berechnet die hoehe des ErwerbspensumRule eines bestimmten Erwerbspensums
 * Diese Rule muss immer am Anfang kommen, d.h. sie setzt den initialen Anspruch
 * Die weiteren Rules müssen diesen Wert gegebenenfalls korrigieren.
 * Verweis 16.9.2
 */
public class ErwerbspensumRule extends AbstractEbeguRule{

	public ErwerbspensumRule(DateRange validityPeriod) {
		super(RuleKey.ERWERBSPENSUM, RuleType.GRUNDREGEL_CALC, validityPeriod);
	}

	//TODO (hefr) Achtung, es muss ncoh das Eingangsdatum beachtet werden!
//	Eine Änderung des Arbeitspensums ist rechtzeitig, falls die Änderung im Vormonat gemeldet wird. In
//	diesem Fall wird der Anspruch zusammen mit dem Ereigniseintritt des Arbeitspensums angepasst.

//	Wird die Änderung des Arbeitspensums im Monat des Ereignis oder noch später gemeldet, erfolgt
//	eine ERHÖHUNG des Anspruchs erst auf den Folgemonat:

//	Im Falle einer Herabsetzung des Arbeitspensums, wird der Anspruch zusammen mit dem
//	Ereigniseintritt angepasst

	@Override
	@Nonnull
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> erwerbspensumAbschnitte = new ArrayList<>();
		Gesuch gesuch =  betreuung.extractGesuch();
		if (gesuch.getGesuchsteller1() != null) {
			erwerbspensumAbschnitte.addAll(getErwerbspensumAbschnittForGesuchsteller(gesuch.getGesuchsteller1(), false));
		}
		if (gesuch.getGesuchsteller2() != null) {
			erwerbspensumAbschnitte.addAll(getErwerbspensumAbschnittForGesuchsteller(gesuch.getGesuchsteller2(), true));
		}
		return erwerbspensumAbschnitte;
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		Objects.requireNonNull(betreuung.extractGesuch(), "Gesuch muss gesetzt sein");
		Objects.requireNonNull(betreuung.extractGesuch().getFamiliensituation(), "Familiensituation muss gesetzt sein");
		boolean hasSecondGesuchsteller = betreuung.extractGesuch().getFamiliensituation().hasSecondGesuchsteller();
		int erwerbspensumOffset = hasSecondGesuchsteller ? 100 : 0;
		// Erwerbspensum ist immer die erste Rule, d.h. es wird das Erwerbspensum mal als Anspruch angenommen
		// Das Erwerbspensum muss PRO GESUCHSTELLER auf 100% limitiert werden
		int erwerbspensum1 = verfuegungZeitabschnitt.getErwerbspensumGS1();
		if (erwerbspensum1 > 100) {
			erwerbspensum1 = 100;
			verfuegungZeitabschnitt.addBemerkung(RuleKey.ERWERBSPENSUM.name() + ": Erwerbspensum GS 1 wurde auf 100% limitiert");
		}
		int erwerbspensum2 = verfuegungZeitabschnitt.getErwerbspensumGS2();
		if (erwerbspensum2 > 100) {
			erwerbspensum2 = 100;
			verfuegungZeitabschnitt.addBemerkung(RuleKey.ERWERBSPENSUM.name() + ": Erwerbspensum GS 2 wurde auf 100% limitiert");
		}
		int anspruch = erwerbspensum1 + erwerbspensum2 - erwerbspensumOffset;
		if (anspruch <= 0) {
			anspruch = 0;
			verfuegungZeitabschnitt.addBemerkung(RuleKey.ERWERBSPENSUM.name() + ": Anspruch wurde aufgrund Erwerbspensum auf 0% gesetzt");
		}
		verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(anspruch);
		if (verfuegungZeitabschnitt.getAnspruchspensumRest() == -1) { //wurde schon mal ein Rest berechnet?
			// Dies ist die erste Betreuung dieses Kindes. Wir initialisieren den "Rest" auf das Erwerbspensum
			verfuegungZeitabschnitt.setAnspruchspensumRest(anspruch);
		}
	}

	/**
	 * geht durch die Erwerpspensen des Gesuchstellers und gibt Abschnitte zurueck
	 * @param gesuchsteller Der Gesuchsteller dessen Erwerbspensumcontainers zu Abschnitte konvertiert werden
	 * @param gs2 handelt es sich um gesuchsteller1 -> false oder gesuchsteller2 -> true
	 * @return
	 */
	@Nonnull
	private List<VerfuegungZeitabschnitt> getErwerbspensumAbschnittForGesuchsteller(@Nonnull Gesuchsteller gesuchsteller, boolean gs2) {
		List<VerfuegungZeitabschnitt> ewpAbschnitte = new ArrayList<>();
		Set<ErwerbspensumContainer> ewpContainers = gesuchsteller.getErwerbspensenContainers();
		for (ErwerbspensumContainer erwerbspensumContainer : ewpContainers) {
			Erwerbspensum erwerbspensumJA = erwerbspensumContainer.getErwerbspensumJA();
			ewpAbschnitte.add(toVerfuegungZeitabschnitt(erwerbspensumJA, gs2));
		}
		return ewpAbschnitte;
	}

	/**
	 * Konvertiert ein Erwerbspensum in einen Zeitabschnitt von entsprechender dauer und erwerbspensumGS1 (falls gs2=false)
	 * oder erwerpspensuGS2 (falls gs2=true)
	 * @param erwerbspensum
	 * @param gs2
	 * @return
	 */
	@Nonnull
	private VerfuegungZeitabschnitt toVerfuegungZeitabschnitt(@Nonnull Erwerbspensum erwerbspensum, boolean gs2) {
		VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(erwerbspensum.getGueltigkeit());
		int erwerbspensumTotal = 0;
		erwerbspensumTotal += erwerbspensum.getPensum();
		if (erwerbspensum.getZuschlagsprozent() != null) {
			erwerbspensumTotal += erwerbspensum.getZuschlagsprozent();
		}
		// Wir merken uns hier den eingegebenen Wert, auch wenn dieser (mit Zuschlag) über 100% liegt
		if (gs2) {
			zeitabschnitt.setErwerbspensumGS2(erwerbspensumTotal);
		} else {
			zeitabschnitt.setErwerbspensumGS1(erwerbspensumTotal);
		}
		return zeitabschnitt;
	}
}

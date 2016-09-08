package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Berechnet die hoehe des ErwerbspensumRule eines bestimmten Erwerbspensums
 * Diese Rule muss immer am Anfang kommen, d.h. sie setzt den initialen Anspruch
 * Die weiteren Rules müssen diesen Wert gegebenenfalls korrigieren.
 * Verweis 16.9.2
 */
public class ErwerbspensumAbschnittRule extends AbstractAbschnittRule {

	public ErwerbspensumAbschnittRule(DateRange validityPeriod) {
		super(RuleKey.ERWERBSPENSUM, RuleType.GRUNDREGEL_DATA, validityPeriod);
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

	/**
	 * geht durch die Erwerpspensen des Gesuchstellers und gibt Abschnitte zurueck
	 * @param gesuchsteller Der Gesuchsteller dessen Erwerbspensumcontainers zu Abschnitte konvertiert werden
	 * @param gs2 handelt es sich um gesuchsteller1 -> false oder gesuchsteller2 -> true
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

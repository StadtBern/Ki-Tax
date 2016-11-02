package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

	@Override
	@Nonnull
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		List<VerfuegungZeitabschnitt> erwerbspensumAbschnitte = new ArrayList<>();
		Gesuch gesuch =  betreuung.extractGesuch();
		if (gesuch.getGesuchsteller1() != null) {
			erwerbspensumAbschnitte.addAll(getErwerbspensumAbschnittForGesuchsteller(gesuch, gesuch.getGesuchsteller1(), false));
		}
		if (gesuch.getGesuchsteller2() != null) {
			erwerbspensumAbschnitte.addAll(getErwerbspensumAbschnittForGesuchsteller(gesuch, gesuch.getGesuchsteller2(), true));
		}
		return erwerbspensumAbschnitte;
	}

	/**
	 * geht durch die Erwerpspensen des Gesuchstellers und gibt Abschnitte zurueck
	 * @param gesuchsteller Der Gesuchsteller dessen Erwerbspensumcontainers zu Abschnitte konvertiert werden
	 * @param gs2 handelt es sich um gesuchsteller1 -> false oder gesuchsteller2 -> true
	 */
	@Nonnull
	private List<VerfuegungZeitabschnitt> getErwerbspensumAbschnittForGesuchsteller(@Nonnull Gesuch gesuch, @Nonnull Gesuchsteller gesuchsteller, boolean gs2) {
		List<VerfuegungZeitabschnitt> ewpAbschnitte = new ArrayList<>();
		Set<ErwerbspensumContainer> ewpContainers = gesuchsteller.getErwerbspensenContainersNotEmpty();
		for (ErwerbspensumContainer erwerbspensumContainer : ewpContainers) {
			Erwerbspensum erwerbspensumJA = erwerbspensumContainer.getErwerbspensumJA();
			final VerfuegungZeitabschnitt zeitabschnitt = toVerfuegungZeitabschnitt(gesuch, erwerbspensumJA, gs2);
			if (zeitabschnitt != null) {
				ewpAbschnitte.add(zeitabschnitt);
			}
		}
		return ewpAbschnitte;
	}

	/**
	 * Konvertiert ein Erwerbspensum in einen Zeitabschnitt von entsprechender dauer und erwerbspensumGS1 (falls gs2=false)
	 * oder erwerpspensuGS2 (falls gs2=true)
	 */
	@Nullable
	private VerfuegungZeitabschnitt toVerfuegungZeitabschnitt(@Nonnull Gesuch gesuch, @Nonnull Erwerbspensum erwerbspensum, boolean gs2) {
		final DateRange gueltigkeit = erwerbspensum.getGueltigkeit();

		int erwerbspensumTotal = 0;
		erwerbspensumTotal += erwerbspensum.getPensum();
		if (erwerbspensum.getZuschlagsprozent() != null) {
			erwerbspensumTotal += erwerbspensum.getZuschlagsprozent();
		}
		// Wir merken uns hier den eingegebenen Wert, auch wenn dieser (mit Zuschlag) über 100% liegt
		if (gs2 && gesuch.isMutation() && gesuch.getFamiliensituationErstgesuch() != null && gesuch.getFamiliensituation() != null) {
			if (!gesuch.getFamiliensituationErstgesuch().hasSecondGesuchsteller() && gesuch.getFamiliensituation().hasSecondGesuchsteller()) {
				// 1GS to 2GS
				if (gueltigkeit.getGueltigBis().isAfter(gesuch.getFamiliensituation().getAenderungPer())
					&& gueltigkeit.getGueltigAb().isBefore(gesuch.getFamiliensituation().getAenderungPer())) {
						gueltigkeit.setGueltigAb(gesuch.getFamiliensituation().getAenderungPer());
				}
			}
			else if (gesuch.getFamiliensituationErstgesuch().hasSecondGesuchsteller() && !gesuch.getFamiliensituation().hasSecondGesuchsteller()
				&& gueltigkeit.getGueltigAb().isBefore(gesuch.getFamiliensituation().getAenderungPer())
				&& gueltigkeit.getGueltigBis().isAfter(gesuch.getFamiliensituation().getAenderungPer())) {
				// 2GS to 1GS
				gueltigkeit.setGueltigBis(gesuch.getFamiliensituation().getAenderungPer().minusDays(1));
			}
			return createZeitAbschnittForGS2(gueltigkeit, erwerbspensumTotal);
		}
		else if (gs2 && !gesuch.isMutation()) {
			return createZeitAbschnittForGS2(gueltigkeit, erwerbspensumTotal);
		}
		else if (!gs2) {
			VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(gueltigkeit);
			zeitabschnitt.setErwerbspensumGS1(erwerbspensumTotal);
			return zeitabschnitt;
		}

		return null;
	}

	@Nonnull
	private VerfuegungZeitabschnitt createZeitAbschnittForGS2(DateRange gueltigkeit, int erwerbspensumTotal) {
		VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(gueltigkeit);
		zeitabschnitt.setErwerbspensumGS1(null);
		zeitabschnitt.setErwerbspensumGS2(erwerbspensumTotal);
		return zeitabschnitt;
	}
}

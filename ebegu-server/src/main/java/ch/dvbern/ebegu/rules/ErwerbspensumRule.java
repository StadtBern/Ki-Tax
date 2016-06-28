package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Berechnet die hoehe des Betreeungspensum einer bestimmten Betreuung.
 * Diese Rule muss immer am Anfang kommen, d.h. sie setzt das gewünschte Betreuungspensum mal als Anspruchberechtigt.
 * Die weiteren Rules müssen diesen Wert gegebenenfalls korrigieren.
 */
public class ErwerbspensumRule extends AbstractEbeguRule{

	public ErwerbspensumRule(DateRange validityPeriod) {
		super(RuleKey.ERWERBSPENSUM, RuleType.GRUNDREGEL, validityPeriod);
	}

	@Override
	@Nonnull
	protected Collection<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull BetreuungspensumContainer betreuungspensumContainer, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {
		List<VerfuegungZeitabschnitt> erwerbspensumAbschnitte = new ArrayList<>();
		Gesuch gesuch =  betreuungspensumContainer.extractGesuch();
		if (gesuch.getGesuchsteller1() != null) {
			erwerbspensumAbschnitte.addAll(getErwerbspensumAbschnittForGesuchsteller(gesuch.getGesuchsteller1(), false));
		}
		if (gesuch.getGesuchsteller2() != null) {
			erwerbspensumAbschnitte.addAll(getErwerbspensumAbschnittForGesuchsteller(gesuch.getGesuchsteller2(), true));
		}
		return erwerbspensumAbschnitte;
	}

	@Override
	protected void executeRule(@Nonnull BetreuungspensumContainer betreuungspensumContainer, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		Objects.requireNonNull(betreuungspensumContainer.extractGesuch(), "Gesuch muss gesetzt sein");
		Objects.requireNonNull(betreuungspensumContainer.extractGesuch().getFamiliensituation(), "Familiensituation muss gesetzt sein");
		boolean hasSecondGesuchsteller = betreuungspensumContainer.extractGesuch().getFamiliensituation().hasSecondGesuchsteller();
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
		verfuegungZeitabschnitt.setAnspruchspensumOriginal(anspruch);
	}

	@Nonnull
	private List<VerfuegungZeitabschnitt> getErwerbspensumAbschnittForGesuchsteller(@Nonnull Gesuchsteller gesuchsteller, boolean gs2) {
		List<VerfuegungZeitabschnitt> erwerbspensumAbschnitte = new ArrayList<>();
		Set<ErwerbspensumContainer> erwerbspensenContainersGS1 = gesuchsteller.getErwerbspensenContainers();
		for (ErwerbspensumContainer erwerbspensumContainer : erwerbspensenContainersGS1) {
			Erwerbspensum erwerbspensumJA = erwerbspensumContainer.getErwerbspensumJA();
			erwerbspensumAbschnitte.add(toVerfuegungZeitabschnitt(erwerbspensumJA, gs2));
		}
		return erwerbspensumAbschnitte;
	}

	@Nonnull
	private VerfuegungZeitabschnitt toVerfuegungZeitabschnitt(@Nonnull Erwerbspensum erwerbspensum, boolean gs2) {
		VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(erwerbspensum.getGueltigkeit());
		int erwerbspensumTotal = 0;
		if (erwerbspensum.getPensum() != null) {
			erwerbspensumTotal += erwerbspensum.getPensum();
		}
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

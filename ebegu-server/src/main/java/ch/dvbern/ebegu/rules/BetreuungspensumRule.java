package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungspensum;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Regel für die Betreuungspensen. Sie beachtet:
 * - Anspruch aus Betreuungspensum darf nicht höher sein als Erwerbspensum
 * - Nur relevant für Kita, Tageseltern-Kleinkinder, die anderen bekommen so viel wie sie wollen
 * - Falls Kind eine Fachstelle hat, gilt das Pensum der Fachstelle
 */
public class BetreuungspensumRule extends AbstractEbeguRule {

	public BetreuungspensumRule(@Nonnull DateRange validityPeriod) {
		super(RuleKey.BETREUUNGSPENSUM, RuleType.GRUNDREGEL, validityPeriod);
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {
		List<VerfuegungZeitabschnitt> betreuungspensumAbschnitte = new ArrayList<>();
		Set<BetreuungspensumContainer> betreuungspensen = betreuung.getBetreuungspensumContainers();
		for (BetreuungspensumContainer betreuungspensumContainer : betreuungspensen) {
			Betreuungspensum betreuungspensum = betreuungspensumContainer.getBetreuungspensumJA();
			betreuungspensumAbschnitte.add(toVerfuegungZeitabschnitt(betreuungspensum));
		}
		return betreuungspensumAbschnitte;
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
		if (betreuung.getKind().getKindJA().getPensumFachstelle() != null) {
			int pensumFachstelle = betreuung.getKind().getKindJA().getPensumFachstelle().getPensum();
			// Anspruch ist immer genau das Pensum der Fachstelle
			betreuungberechnet = pensumFachstelle;
			// Den neuen "AnspruchRest" bestimmen:
			if (pensumFachstelle > verfuegungZeitabschnitt.getBetreuungspensum()) {
				anspruchRest = pensumFachstelle - verfuegungZeitabschnitt.getBetreuungspensum();
			} else {
				anspruchRest = 0;
			}
		}
		// Kita und Tageseltern-Kleinkinder: Anspruch ist das kleinere von Betreuungspensum und Erwerbspensum
		else if (betreuung.isAngebotKita() || betreuung.isAngebotTageselternKleinkinder()) {
			if (verfuegungZeitabschnitt.getAnspruchspensumOriginal() < betreuungberechnet) {
				betreuungberechnet = verfuegungZeitabschnitt.getAnspruchspensumOriginal();
			}
			// Ausserdem: Nur soviel, wie noch nicht von einer anderen Kita oder Tageseltern Kleinkinder verwendet wurde:
			if (verfuegungZeitabschnitt.getAnspruchspensumRest() < betreuungberechnet) {
				betreuungberechnet = verfuegungZeitabschnitt.getAnspruchspensumRest();
			}
			// Den neuen "AnspruchRest" bestimmen:
			anspruchRest = verfuegungZeitabschnitt.getAnspruchspensumRest() - betreuungberechnet;
		}
		// Den berechneten Wert setzen, sowie den Restanspruch aktualisieren
		verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(betreuungberechnet);
		if (anspruchRest <= 0) {
			anspruchRest = 0;
		}
		verfuegungZeitabschnitt.setAnspruchspensumRest(anspruchRest);
	}

	@Nonnull
	private VerfuegungZeitabschnitt toVerfuegungZeitabschnitt(@Nonnull Betreuungspensum betreuungspensum) {
		VerfuegungZeitabschnitt zeitabschnitt = new VerfuegungZeitabschnitt(betreuungspensum.getGueltigkeit());
		zeitabschnitt.setBetreuungspensum(betreuungspensum.getPensum());
		return zeitabschnitt;
	}
}

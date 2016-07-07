package ch.dvbern.ebegu.dto;

import ch.dvbern.ebegu.entities.FinanzielleSituation;
import ch.dvbern.ebegu.entities.Gesuch;

import java.math.BigDecimal;

/**
 * DTO für die Resultate der Berechnungen der Finanziellen Situation
 */
public class FinanzielleSituationResultateDTO extends AbstractFinanzielleSituationResultateDTO {


	public FinanzielleSituationResultateDTO(Gesuch gesuch, double familiengroesse, BigDecimal famGroesseAbz) {
		super(familiengroesse, famGroesseAbz);

		if (gesuch != null) {
			if (gesuch.getGesuchsteller1() != null) {
				final FinanzielleSituation finanzielleSituationGS1 = getFinanzielleSituationGS1(gesuch);
				calculateProGesuchsteller(finanzielleSituationGS1, calculateNettoJahresLohn(finanzielleSituationGS1));
			}
			if (gesuch.getGesuchsteller2() != null) {
				final FinanzielleSituation finanzielleSituationGS2 = getFinanzielleSituationGS2(gesuch);
				calculateProGesuchsteller(finanzielleSituationGS2, calculateNettoJahresLohn(finanzielleSituationGS2));
			}
			calculateZusammen();
		}
		initToZero();
	}

	private BigDecimal calculateNettoJahresLohn(FinanzielleSituation finanzielleSituation) {
		return finanzielleSituation.getNettolohn();
	}

	private FinanzielleSituation getFinanzielleSituationGS1(Gesuch gesuch) {
		if (gesuch.getGesuchsteller1() != null) {
			return gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getFinanzielleSituationSV();
		}
		return null;
	}

	private FinanzielleSituation getFinanzielleSituationGS2(Gesuch gesuch) {
		if (gesuch.getGesuchsteller2() != null) {
			return gesuch.getGesuchsteller2().getFinanzielleSituationContainer().getFinanzielleSituationSV();
		}
		return null;
	}


}

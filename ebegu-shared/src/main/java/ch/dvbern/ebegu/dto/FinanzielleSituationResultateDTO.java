package ch.dvbern.ebegu.dto;

import ch.dvbern.ebegu.entities.FinanzielleSituation;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;

import java.math.BigDecimal;

/**
 * DTO für die Resultate der Berechnungen der Finanziellen Situation
 */
public class FinanzielleSituationResultateDTO extends AbstractFinanzielleSituationResultateDTO {


	public FinanzielleSituationResultateDTO(Gesuch gesuch, double familiengroesse, BigDecimal famGroesseAbz) {
		super(familiengroesse, famGroesseAbz);

		if (gesuch != null) {
			final FinanzielleSituation finanzielleSituationGS1 = getFinanzielleSituationGS(gesuch.getGesuchsteller1());
			setGeschaeftsgewinnDurchschnittGesuchsteller1(calcGeschaeftsgewinnDurchschnitt(finanzielleSituationGS1));

			final FinanzielleSituation finanzielleSituationGS2 = getFinanzielleSituationGS(gesuch.getGesuchsteller2());
			setGeschaeftsgewinnDurchschnittGesuchsteller2(calcGeschaeftsgewinnDurchschnitt(finanzielleSituationGS2));

			calculateZusammen(finanzielleSituationGS1, calculateNettoJahresLohn(finanzielleSituationGS1),
				finanzielleSituationGS2, calculateNettoJahresLohn(finanzielleSituationGS2));
		}
		initToZero();
	}

	private BigDecimal calculateNettoJahresLohn(FinanzielleSituation finanzielleSituation) {
		if(finanzielleSituation!= null) {
			return finanzielleSituation.getNettolohn();
		}
		return BigDecimal.ZERO;
	}

	private FinanzielleSituation getFinanzielleSituationGS(Gesuchsteller gesuchsteller) {
		if (gesuchsteller != null && gesuchsteller.getFinanzielleSituationContainer() != null) {
			return gesuchsteller.getFinanzielleSituationContainer().getFinanzielleSituationSV();
		}
		return null;
	}

}

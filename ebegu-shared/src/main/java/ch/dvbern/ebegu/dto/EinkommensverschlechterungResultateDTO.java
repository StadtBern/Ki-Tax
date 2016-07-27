package ch.dvbern.ebegu.dto;

import ch.dvbern.ebegu.entities.Einkommensverschlechterung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import org.apache.commons.lang3.Validate;

import java.math.BigDecimal;

/**
 * DTO für die Resultate der Berechnungen der Einkommensverschlechterung
 */
public class EinkommensverschlechterungResultateDTO extends AbstractFinanzielleSituationResultateDTO {


	public EinkommensverschlechterungResultateDTO(Gesuch gesuch, double familiengroesse, BigDecimal famGroesseAbz, int basisJahrPlus) {
		super(familiengroesse, famGroesseAbz);

		if (gesuch != null) {
			final Einkommensverschlechterung einkommensverschlechterungGS1 = getEinkommensverschlechterungGS(gesuch.getGesuchsteller1(), basisJahrPlus);
			setGeschaeftsgewinnDurchschnittGesuchsteller1(calcGeschaeftsgewinnDurchschnitt(einkommensverschlechterungGS1));

			final Einkommensverschlechterung einkommensverschlechterungGS2 = getEinkommensverschlechterungGS(gesuch.getGesuchsteller2(), basisJahrPlus);
			setGeschaeftsgewinnDurchschnittGesuchsteller2(calcGeschaeftsgewinnDurchschnitt(einkommensverschlechterungGS2));

			calculateZusammen(einkommensverschlechterungGS1, calculateNettoJahresLohn(einkommensverschlechterungGS1),
				einkommensverschlechterungGS2, calculateNettoJahresLohn(einkommensverschlechterungGS2));
		}
		initToZero();
	}

	private BigDecimal calculateNettoJahresLohn(Einkommensverschlechterung einkommensverschlechterung) {
		BigDecimal total = BigDecimal.ZERO;
		if (einkommensverschlechterung != null) {
			total = add(total, einkommensverschlechterung.getNettolohnJan());
			total = add(total, einkommensverschlechterung.getNettolohnFeb());
			total = add(total, einkommensverschlechterung.getNettolohnMrz());
			total = add(total, einkommensverschlechterung.getNettolohnApr());
			total = add(total, einkommensverschlechterung.getNettolohnMai());
			total = add(total, einkommensverschlechterung.getNettolohnJun());
			total = add(total, einkommensverschlechterung.getNettolohnJul());
			total = add(total, einkommensverschlechterung.getNettolohnAug());
			total = add(total, einkommensverschlechterung.getNettolohnSep());
			total = add(total, einkommensverschlechterung.getNettolohnOkt());
			total = add(total, einkommensverschlechterung.getNettolohnNov());
			total = add(total, einkommensverschlechterung.getNettolohnDez());
			total = add(total, einkommensverschlechterung.getNettolohnZus());
		}
		return total;
	}

	private Einkommensverschlechterung getEinkommensverschlechterungGS(Gesuchsteller gesuchsteller, int basisJahrPlus) {
		if (gesuchsteller != null) {
			Validate.notNull(gesuchsteller.getEinkommensverschlechterungContainer());
			if (basisJahrPlus == 2) {
				return gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2();
			} else {
				return gesuchsteller.getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1();
			}
		}
		return null;
	}

}

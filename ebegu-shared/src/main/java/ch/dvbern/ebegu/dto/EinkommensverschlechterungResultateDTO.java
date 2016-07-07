package ch.dvbern.ebegu.dto;

import ch.dvbern.ebegu.entities.Einkommensverschlechterung;
import ch.dvbern.ebegu.entities.FinanzielleSituation;
import ch.dvbern.ebegu.entities.Gesuch;
import org.apache.commons.lang3.Validate;

import java.math.BigDecimal;

/**
 * DTO für die Resultate der Berechnungen der Finanziellen Situation
 */
public class EinkommensverschlechterungResultateDTO extends AbstractFinanzielleSituationResultateDTO {


	public EinkommensverschlechterungResultateDTO(Gesuch gesuch, double familiengroesse, BigDecimal famGroesseAbz, int basisJahrPlus) {
		super(familiengroesse, famGroesseAbz);

		if (gesuch != null) {
			if (gesuch.getGesuchsteller1() != null) {
				final Einkommensverschlechterung einkommensverschlechterungGS1 = getEinkommensverschlechterungGS1(gesuch,basisJahrPlus);
				calculateProGesuchsteller(einkommensverschlechterungGS1, calculateNettoJahresLohn(einkommensverschlechterungGS1));
			}
			if (gesuch.getGesuchsteller2() != null) {
				final Einkommensverschlechterung einkommensverschlechterungGS2 = getEinkommensverschlechterungGS2(gesuch,basisJahrPlus);
				calculateProGesuchsteller(einkommensverschlechterungGS2, calculateNettoJahresLohn(einkommensverschlechterungGS2));
			}
			calculateZusammen();
		}
		initToZero();
	}

	private BigDecimal calculateNettoJahresLohn(Einkommensverschlechterung einkommensverschlechterung) {
		BigDecimal total = BigDecimal.ZERO;
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
		return total;
	}

	private Einkommensverschlechterung getEinkommensverschlechterungGS1(Gesuch gesuch, int basisJahrPlus) {
		if (gesuch.getGesuchsteller1() != null) {
			Validate.notNull(gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer());
			if(basisJahrPlus == 2) {
				return gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2();
			}else{
				return gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1();
			}
		}
		return null;
	}

	private Einkommensverschlechterung getEinkommensverschlechterungGS2(Gesuch gesuch, int basisJahrPlus) {
		if (gesuch.getGesuchsteller2() != null) {
			Validate.notNull(gesuch.getGesuchsteller2().getEinkommensverschlechterungContainer());
			if(basisJahrPlus == 2) {
				return gesuch.getGesuchsteller2().getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2();
			}else{
				return gesuch.getGesuchsteller2().getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1();
			}
		}
		return null;	}


}

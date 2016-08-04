package ch.dvbern.ebegu.dto;

import java.math.BigDecimal;

/**
 * DTO für die Resultate der Berechnungen der Finanziellen Situation
 */
public class FinanzielleSituationResultateDTO extends AbstractFinanzielleSituationResultateDTO {


	public FinanzielleSituationResultateDTO(double familiengroesse, BigDecimal famGroesseAbz) {
		super(familiengroesse, famGroesseAbz);
		initToZero();
	}

}

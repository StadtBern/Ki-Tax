package ch.dvbern.ebegu.dto;

import java.math.BigDecimal;

/**
 * DTO für die Resultate der Berechnungen der Einkommensverschlechterung
 */
public class EinkommensverschlechterungResultateDTO extends AbstractFinanzielleSituationResultateDTO {


	public EinkommensverschlechterungResultateDTO(double familiengroesse, BigDecimal famGroesseAbz) {
		super(familiengroesse, famGroesseAbz);
		initToZero();
	}

}

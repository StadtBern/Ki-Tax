package ch.dvbern.ebegu.api.dtos;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Betreuung Container
 */
@XmlRootElement(name = "betreuungspensum")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxBetreuungspensumContainer extends JaxAbstractDTO {

	private static final long serialVersionUID = -8912537586244581785L;

	@Valid
	private JaxBetreuungspensum betreuungspensumGS;

	@Valid
	private JaxBetreuungspensum betreuungspensumJA;

	public JaxBetreuungspensum getBetreuungspensumGS() {
		return betreuungspensumGS;
	}

	public void setBetreuungspensumGS(JaxBetreuungspensum betreuungspensumGS) {
		this.betreuungspensumGS = betreuungspensumGS;
	}

	public JaxBetreuungspensum getBetreuungspensumJA() {
		return betreuungspensumJA;
	}

	public void setBetreuungspensumJA(JaxBetreuungspensum betreuungspensumJA) {
		this.betreuungspensumJA = betreuungspensumJA;
	}
}

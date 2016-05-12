package ch.dvbern.ebegu.api.dtos;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Kind Container
 */
@XmlRootElement(name = "kind")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxKindContainer extends JaxAbstractDTO {

	private static final long serialVersionUID = -8912537186244981782L;

	@Valid
	private JaxKind kindGS;

	@Valid
	private JaxKind kindJA;

	public JaxKind getKindGS() {
		return kindGS;
	}

	public void setKindGS(JaxKind kindGS) {
		this.kindGS = kindGS;
	}

	public JaxKind getKindJA() {
		return kindJA;
	}

	public void setKindJA(JaxKind kindJA) {
		this.kindJA = kindJA;
	}
}

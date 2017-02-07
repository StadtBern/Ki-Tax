package ch.dvbern.ebegu.api.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * DTO fuer Daten des BetreuungsmitteilungPensum
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxBetreuungsmitteilungPensum extends JaxAbstractPensumDTO {

	private Boolean nichtEingetreten = false;

	private static final long serialVersionUID = -8012537546244511785L;

	public Boolean getNichtEingetreten() {
		return nichtEingetreten;
	}

	public void setNichtEingetreten(Boolean nichtEingetreten) {
		this.nichtEingetreten = nichtEingetreten;
	}
}

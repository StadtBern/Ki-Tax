package ch.dvbern.ebegu.api.dtos;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Faelle
 */
@XmlRootElement(name = "gesuch")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxGesuch extends JaxAbstractDTO {

	private static final long serialVersionUID = -1217019901364130097L;

	@NotNull
	private JaxFall fall;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public JaxFall getFall() {
		return fall;
	}

	public void setFall(JaxFall fall) {
		this.fall = fall;
	}

}


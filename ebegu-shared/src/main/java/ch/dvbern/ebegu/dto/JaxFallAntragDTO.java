package ch.dvbern.ebegu.dto;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for FallAntrag
 * All data for this class are taken from the Fall. This will replace an AntragDTO when wen need to return a dto
 * for a Fall that has no Gesuch yet
 */
@XmlRootElement(name = "pendenz")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFallAntragDTO extends JaxAbstractAntragDTO {

	private static final long serialVersionUID = -1277026654457135397L;

	@NotNull
	private String fallID;

	public JaxFallAntragDTO() {
		super(JaxFallAntragDTO.class.getSimpleName());
	}

	public String getFallID() {
		return fallID;
	}

	public void setFallID(String fallID) {
		this.fallID = fallID;
	}
}

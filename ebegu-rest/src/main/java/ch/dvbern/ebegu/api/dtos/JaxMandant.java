package ch.dvbern.ebegu.api.dtos;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Mandanten
 */
@XmlRootElement(name = "mandant")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxMandant extends JaxAbstractDTO {

	private static final long serialVersionUID = -1297019601664134592L;

	@NotNull
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

package ch.dvbern.ebegu.api.resource.dtos;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Application Propertie
 */
@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxApplicationProperties extends JaxAbstractDTO {

	private static final long serialVersionUID = -2243403693436143445L;
	@NotNull
	private String value = null;

	@NotNull
	private String name = null;


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

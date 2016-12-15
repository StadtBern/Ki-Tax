package ch.dvbern.ebegu.api.dtos;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by imanol on 17.03.16.
 * DTO fuer Institution
 */
@XmlRootElement(name = "institution")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxInstitution extends JaxAbstractDTO {

	private static final long serialVersionUID = -1393677898323418626L;

	@NotNull
	private String name;

	private JaxTraegerschaft traegerschaft;
	@NotNull
	private JaxMandant mandant;

	// just to communicate with client
	private boolean synchronizedWithOpenIdm = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JaxTraegerschaft getTraegerschaft() {
		return traegerschaft;
	}

	public void setTraegerschaft(JaxTraegerschaft traegerschaft) {
		this.traegerschaft = traegerschaft;
	}

	public JaxMandant getMandant() {
		return mandant;
	}

	public void setMandant(JaxMandant mandant) {
		this.mandant = mandant;
	}

	public boolean isSynchronizedWithOpenIdm() {
		return synchronizedWithOpenIdm;
	}

	public void setSynchronizedWithOpenIdm(boolean synchronizedWithOpenIdm) {
		this.synchronizedWithOpenIdm = synchronizedWithOpenIdm;
	}
}

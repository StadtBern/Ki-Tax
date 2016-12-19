package ch.dvbern.ebegu.api.dtos;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by imanol on 17.03.16.
 * DTO fuer Traegerschaft
 */
@XmlRootElement(name = "traegerschaft")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxTraegerschaft extends JaxAbstractDTO {

	private static final long serialVersionUID = -1093676498323618626L;

	@NotNull
	private String name;

	@NotNull
	private Boolean active = true;

	@NotNull
	private String mail;

	// just to communicate with client
	private boolean synchronizedWithOpenIdm = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public boolean isSynchronizedWithOpenIdm() {
		return synchronizedWithOpenIdm;
	}

	public void setSynchronizedWithOpenIdm(boolean synchronizedWithOpenIdm) {
		this.synchronizedWithOpenIdm = synchronizedWithOpenIdm;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
}

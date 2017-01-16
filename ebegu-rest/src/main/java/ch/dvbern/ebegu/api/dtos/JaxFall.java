package ch.dvbern.ebegu.api.dtos;

import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Faelle
 */
@XmlRootElement(name = "fall")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFall extends JaxAbstractDTO {

	private static final long serialVersionUID = -1297019901664130597L;

	private long fallNummer;

	private JaxAuthLoginElement verantwortlicher;

	@Min(1)
	private Integer nextNumberKind = 1;
	private String besitzerUsername; //

	public long getFallNummer() {
		return fallNummer;
	}

	public void setFallNummer(long fallNummer) {
		this.fallNummer = fallNummer;
	}

	public JaxAuthLoginElement getVerantwortlicher() {
		return verantwortlicher;
	}

	public void setVerantwortlicher(JaxAuthLoginElement verantwortlicher) {
		this.verantwortlicher = verantwortlicher;
	}

	public Integer getNextNumberKind() {
		return nextNumberKind;
	}

	public void setNextNumberKind(Integer nextNumberKind) {
		this.nextNumberKind = nextNumberKind;
	}

	public void setBesitzerUsername(String besitzerUsername) {
		this.besitzerUsername = besitzerUsername;
	}

	public String getBesitzerUsername() {
		return besitzerUsername;
	}
}

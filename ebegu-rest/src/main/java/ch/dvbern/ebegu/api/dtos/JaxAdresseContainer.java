package ch.dvbern.ebegu.api.dtos;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by imanol on 17.03.16.
 * DTO fuer Adresse
 */
@XmlRootElement(name = "adresse")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAdresseContainer extends JaxAbstractDTO {

	private static final long serialVersionUID = -7917011641387130097L;

	@Valid
	private JaxAdresse adresseJA;

	@Valid
	private JaxAdresse adresseGS;


	public JaxAdresse getAdresseJA() {
		return adresseJA;
	}

	public void setAdresseJA(JaxAdresse adresseJA) {
		this.adresseJA = adresseJA;
	}

	public JaxAdresse getAdresseGS() {
		return adresseGS;
	}

	public void setAdresseGS(JaxAdresse adresseGS) {
		this.adresseGS = adresseGS;
	}
}

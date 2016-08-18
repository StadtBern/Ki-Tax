package ch.dvbern.ebegu.api.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tempDokument")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxTempDokument extends JaxAbstractDTO {

	private static final long serialVersionUID = 1118235796540488553L;

	private String accessToken;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}

package ch.dvbern.ebegu.api.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "downloadFile")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxDownloadFile extends JaxFile {

	private static final long serialVersionUID = 1118235796540488553L;

	//only accessToken needed on server-side

	private String accessToken;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}

package ch.dvbern.ebegu.api.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Jax Element for Response of OpenAm, gives result and page counts
 */
@XmlRootElement(name = "openAmResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxOpenAmResponse implements Serializable {


	private static final long serialVersionUID = 3556645949176582367L;

	private String tokenId;
	private String successUrl;

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}
}

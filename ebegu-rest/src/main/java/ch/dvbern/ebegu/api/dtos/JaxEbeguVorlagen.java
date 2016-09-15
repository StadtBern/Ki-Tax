package ch.dvbern.ebegu.api.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO fuer Dokumente
 */
@XmlRootElement(name = "ebeguVorlagen")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxEbeguVorlagen  implements Serializable {

	private static final long serialVersionUID = 7948406646374709376L;

	private List<JaxEbeguVorlage> ebeguVorlageList = new ArrayList<JaxEbeguVorlage>();

	public List<JaxEbeguVorlage> getEbeguVorlageList() {
		return ebeguVorlageList;
	}

	public void setEbeguVorlageList(List<JaxEbeguVorlage> ebeguVorlageList) {
		this.ebeguVorlageList = ebeguVorlageList;
	}
}

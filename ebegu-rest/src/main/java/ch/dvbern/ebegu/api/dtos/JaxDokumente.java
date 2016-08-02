package ch.dvbern.ebegu.api.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO fuer Dokumente
 */
@XmlRootElement(name = "dokumente")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxDokumente {

	private static final long serialVersionUID = -1297019901664130597L;

	private Set<JaxDokumentGrund> dokumentGruende = new HashSet<>();

	public Set<JaxDokumentGrund> getDokumentGruende() {
		return dokumentGruende;
	}

	public void setDokumentGruende(Set<JaxDokumentGrund> dokumentGruende) {
		this.dokumentGruende = dokumentGruende;
	}
}

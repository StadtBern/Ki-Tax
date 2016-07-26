package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.enums.DokumentTyp;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dokument")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxDokument {

	@NotNull
	private String dokumentName;

	@NotNull
	private DokumentTyp dokumentTyp;

	public String getDokumentName() {
		return dokumentName;
	}

	public void setDokumentName(String dokumentName) {
		this.dokumentName = dokumentName;
	}

	public DokumentTyp getDokumentTyp() {
		return dokumentTyp;
	}

	public void setDokumentTyp(DokumentTyp dokumentTyp) {
		this.dokumentTyp = dokumentTyp;
	}
}

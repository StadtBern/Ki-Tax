package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.enums.DokumentTyp;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dokument")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxDokument extends JaxAbstractDTO {

	private static final long serialVersionUID = 1118235796540488553L;

	private String dokumentName;

	private String dokumentPfad;

	private String dokumentSize;

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

	public String getDokumentPfad() {
		return dokumentPfad;
	}

	public void setDokumentPfad(String dokumentPfad) {
		this.dokumentPfad = dokumentPfad;
	}

	public String getDokumentSize() {
		return dokumentSize;
	}

	public void setDokumentSize(String dokumentSize) {
		this.dokumentSize = dokumentSize;
	}

}

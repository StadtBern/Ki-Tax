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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		JaxDokument that = (JaxDokument) o;

		if (dokumentName != null ? !dokumentName.equals(that.dokumentName) : that.dokumentName != null) return false;
		if (dokumentPfad != null ? !dokumentPfad.equals(that.dokumentPfad) : that.dokumentPfad != null) return false;
		return dokumentTyp == that.dokumentTyp;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (dokumentName != null ? dokumentName.hashCode() : 0);
		result = 31 * result + (dokumentPfad != null ? dokumentPfad.hashCode() : 0);
		result = 31 * result + (dokumentTyp != null ? dokumentTyp.hashCode() : 0);
		return result;
	}
}

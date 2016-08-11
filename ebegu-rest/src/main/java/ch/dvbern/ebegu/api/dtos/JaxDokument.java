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

	public String getDokumentName() {
		return dokumentName;
	}

	public void setDokumentName(String dokumentName) {
		this.dokumentName = dokumentName;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		JaxDokument that = (JaxDokument) o;

		if (dokumentName != null ? !dokumentName.equals(that.dokumentName) : that.dokumentName != null) return false;
		if (dokumentPfad != null ? !dokumentPfad.equals(that.dokumentPfad) : that.dokumentPfad != null) return false;
		return dokumentSize != null ? dokumentSize.equals(that.dokumentSize) : that.dokumentSize == null;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (dokumentName != null ? dokumentName.hashCode() : 0);
		result = 31 * result + (dokumentPfad != null ? dokumentPfad.hashCode() : 0);
		result = 31 * result + (dokumentSize != null ? dokumentSize.hashCode() : 0);
		return result;
	}
}

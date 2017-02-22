package ch.dvbern.ebegu.dto.dataexport.v1;

import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Objects;

/**
 * This is a DTO that is used to export the relevant Information about a {@link ch.dvbern.ebegu.entities.Betreuung}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BetreuungExportDTO {

	private BetreuungsangebotTyp betreuungsArt;
	private InstitutionExportDTO institution;

	public BetreuungsangebotTyp getBetreuungsArt() {
		return betreuungsArt;
	}

	public void setBetreuungsArt(BetreuungsangebotTyp betreuungsArt) {
		this.betreuungsArt = betreuungsArt;
	}

	public InstitutionExportDTO getInstitution() {
		return institution;
	}

	public void setInstitution(InstitutionExportDTO institution) {
		this.institution = institution;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BetreuungExportDTO that = (BetreuungExportDTO) o;
		return getBetreuungsArt() == that.getBetreuungsArt() &&
			Objects.equals(getInstitution(), that.getInstitution());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getBetreuungsArt(), getInstitution());
	}
}

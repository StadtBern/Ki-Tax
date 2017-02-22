package ch.dvbern.ebegu.dto.dataexport.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Objects;

/**
 * This is a DTO that is used to export the relevant Information about a {@link ch.dvbern.ebegu.entities.Institution}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class InstitutionExportDTO {

	/**
	 * unique identifier for a institution.
	 */
	private String id;
	private String name;
	private String traegerschaft;
	private AdresseExportDTO adresse;

	public InstitutionExportDTO(String instID, String name, String traegerschaft, AdresseExportDTO adresse) {
		this.id = instID;
		this.name = name;
		this.traegerschaft = traegerschaft;
		this.adresse = adresse;
	}


	public InstitutionExportDTO() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTraegerschaft() {
		return traegerschaft;
	}

	public void setTraegerschaft(String traegerschaft) {
		this.traegerschaft = traegerschaft;
	}

	public AdresseExportDTO getAdresse() {
		return adresse;
	}

	public void setAdresse(AdresseExportDTO adresse) {
		this.adresse = adresse;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		InstitutionExportDTO that = (InstitutionExportDTO) o;
		return Objects.equals(getId(), that.getId()) &&
			Objects.equals(getName(), that.getName()) &&
			Objects.equals(getTraegerschaft(), that.getTraegerschaft()) &&
			Objects.equals(getAdresse(), that.getAdresse());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getName(), getTraegerschaft(), getAdresse());
	}
}

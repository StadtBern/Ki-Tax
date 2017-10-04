/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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

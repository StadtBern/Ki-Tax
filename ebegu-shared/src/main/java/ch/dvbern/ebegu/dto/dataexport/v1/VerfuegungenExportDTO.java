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
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Objects;

/**
 * This is a DTO that is used to export the relevant Information about a {@link ch.dvbern.ebegu.entities.Verfuegung}.
 * It contains all the Verfuegungen belonging to a single Antrag
 * This Object is at the root of the Export and contains all Verfuegungen for a Given Antrag
 */

@XmlRootElement(name = "verfuegungenExport")
@XmlAccessorType(XmlAccessType.FIELD)
public class VerfuegungenExportDTO {

	private static final String SCHEMA_VERSION = "1.0"; //converter for Version 1

	private List<VerfuegungExportDTO> verfuegungen;


	public List<VerfuegungExportDTO> getVerfuegungen() {
		return verfuegungen;
	}

	public void setVerfuegungen(List<VerfuegungExportDTO> verfuegungen) {
		this.verfuegungen = verfuegungen;
	}

	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VerfuegungenExportDTO that = (VerfuegungenExportDTO) o;
		return Objects.equals(getSchemaVersion(), that.getSchemaVersion()) &&
			Objects.equals(getVerfuegungen(), that.getVerfuegungen());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getSchemaVersion(), getVerfuegungen());
	}
}

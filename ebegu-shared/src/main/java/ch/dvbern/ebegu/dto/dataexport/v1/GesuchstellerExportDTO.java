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

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * This is a DTO that is used to export the relevant Information about a {@link ch.dvbern.ebegu.entities.Gesuchsteller}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GesuchstellerExportDTO {

	private String vorname;
	private String nachname;
	private String email;

	public GesuchstellerExportDTO() {
	}

	public GesuchstellerExportDTO(String vorname, String nachname, String mail) {
		this.vorname = vorname;
		this.nachname = nachname;
		this.email = mail;

	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		GesuchstellerExportDTO that = (GesuchstellerExportDTO) o;
		return Objects.equals(getVorname(), that.getVorname()) &&
			Objects.equals(getNachname(), that.getNachname()) &&
			Objects.equals(getEmail(), that.getEmail());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getVorname(), getNachname(), getEmail());
	}
}

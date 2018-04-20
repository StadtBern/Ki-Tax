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

import ch.dvbern.ebegu.enums.Land;

/**
 * This is a DTO that is used to export the relevant Information about a {@link ch.dvbern.ebegu.entities.Adresse}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AdresseExportDTO {

	private String strasse;
	private String hausnummer;
	private String adresszusatz;
	private String ort;
	private String plz;
	private Land land;

	public AdresseExportDTO(String strasse, String hausnummer, String zusatzzeile, String ort, String plz, Land land) {
		this.strasse = strasse;
		this.hausnummer = hausnummer;
		this.adresszusatz = zusatzzeile;
		this.ort = ort;
		this.plz = plz;
		this.land = land;
	}

	public AdresseExportDTO() {
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getHausnummer() {
		return hausnummer;
	}

	public void setHausnummer(String hausnummer) {
		this.hausnummer = hausnummer;
	}

	public String getAdresszusatz() {
		return adresszusatz;
	}

	public void setAdresszusatz(String adresszusatz) {
		this.adresszusatz = adresszusatz;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public Land getLand() {
		return land;
	}

	public void setLand(Land land) {
		this.land = land;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AdresseExportDTO that = (AdresseExportDTO) o;
		return Objects.equals(getStrasse(), that.getStrasse()) &&
			Objects.equals(getHausnummer(), that.getHausnummer()) &&
			Objects.equals(getAdresszusatz(), that.getAdresszusatz()) &&
			Objects.equals(getOrt(), that.getOrt()) &&
			Objects.equals(getPlz(), that.getPlz()) &&
			getLand() == that.getLand();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getStrasse(), getHausnummer(), getAdresszusatz(), getOrt(), getPlz(), getLand());
	}
}

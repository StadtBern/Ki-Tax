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

package ch.dvbern.ebegu.tets.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Root XML Klasse zum speichern aller Verfuegungszeitabschnitte in XML file
 */
@XmlRootElement(name = "verfuegungszeitAbschnitte")
@XmlAccessorType(XmlAccessType.FIELD)
public class VerfuegungszeitabschnitteData {

	private Integer nummer;

	private String nameKind;

	private String nameBetreung;

	private List<VerfuegungZeitabschnittData> verfuegungszeitabschnitte = new ArrayList<>();

	public List<VerfuegungZeitabschnittData> getVerfuegungszeitabschnitte() {
		return verfuegungszeitabschnitte;
	}

	public void setVerfuegungszeitabschnitte(List<VerfuegungZeitabschnittData> verfuegungszeitabschnitte) {
		this.verfuegungszeitabschnitte = verfuegungszeitabschnitte;
	}

	public Integer getNummer() {
		return nummer;
	}

	public void setNummer(Integer nummer) {
		this.nummer = nummer;
	}

	public String getNameKind() {
		return nameKind;
	}

	public void setNameKind(String nameKind) {
		this.nameKind = nameKind;
	}

	public String getNameBetreung() {
		return nameBetreung;
	}

	public void setNameBetreung(String nameBetreung) {
		this.nameBetreung = nameBetreung;
	}
}

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

package ch.dvbern.ebegu.dto.personensuche;

import java.io.Serializable;
import java.time.LocalDate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.lib.date.converters.LocalDateXMLConverter;

/**
 * Enum für Einwohnercodes aus dem EWK
 */
@XmlRootElement(name = "ewkEinwohnercode")
@XmlAccessorType(XmlAccessType.FIELD)
public class EWKEinwohnercode implements Serializable {

	private static final long serialVersionUID = 4199345504567527849L;

	private String code;

	private String codeTxt;

	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate gueltigVon;

	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate gueltigBis;

	public EWKEinwohnercode() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeTxt() {
		return codeTxt;
	}

	public void setCodeTxt(String codeTxt) {
		this.codeTxt = codeTxt;
	}

	public LocalDate getGueltigVon() {
		return gueltigVon;
	}

	public void setGueltigVon(LocalDate gueltigVon) {
		this.gueltigVon = gueltigVon;
	}

	public LocalDate getGueltigBis() {
		return gueltigBis;
	}

	public void setGueltigBis(LocalDate gueltigBis) {
		this.gueltigBis = gueltigBis;
	}
}

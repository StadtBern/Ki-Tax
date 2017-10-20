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

package ch.dvbern.ebegu.api.dtos;

import java.time.DayOfWeek;
import java.time.LocalTime;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.enums.ModulName;
import ch.dvbern.lib.date.converters.LocalTimeXMLConverter;

/**
 * DTO fuer Module fuer die Tagesschulen
 */
@XmlRootElement(name = "modul")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxModul extends JaxAbstractDTO {

	private static final long serialVersionUID = -1893537808325618626L;

	@NotNull
	private DayOfWeek wochentag;

	@NotNull
	private ModulName modulname;

	@NotNull
	@XmlJavaTypeAdapter(LocalTimeXMLConverter.class)
	private LocalTime zeitVon = null;

	@NotNull
	@XmlJavaTypeAdapter(LocalTimeXMLConverter.class)
	private LocalTime zeitBis = null;


	public DayOfWeek getWochentag() {
		return wochentag;
	}

	public void setWochentag(DayOfWeek wochentag) {
		this.wochentag = wochentag;
	}

	public ModulName getModulname() {
		return modulname;
	}

	public void setModulname(ModulName modulname) {
		this.modulname = modulname;
	}

	public LocalTime getZeitVon() {
		return zeitVon;
	}

	public void setZeitVon(LocalTime zeitVon) {
		this.zeitVon = zeitVon;
	}

	public LocalTime getZeitBis() {
		return zeitBis;
	}

	public void setZeitBis(LocalTime zeitBis) {
		this.zeitBis = zeitBis;
	}
}

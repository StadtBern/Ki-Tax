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
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.enums.ModulTagesschuleName;
import ch.dvbern.lib.date.converters.LocalDateTimeXMLConverter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * DTO fuer Module fuer die Tagesschulen
 */
@XmlRootElement(name = "modulTagesschule")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxModulTagesschule extends JaxAbstractDTO {

	private static final long serialVersionUID = -1893537808325618626L;

	@NotNull
	private DayOfWeek wochentag;

	@NotNull
	private ModulTagesschuleName modulTagesschuleName;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime zeitVon = null;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime zeitBis = null;


	public DayOfWeek getWochentag() {
		return wochentag;
	}

	public void setWochentag(DayOfWeek wochentag) {
		this.wochentag = wochentag;
	}

	public ModulTagesschuleName getModulTagesschuleName() {
		return modulTagesschuleName;
	}

	public void setModulTagesschuleName(ModulTagesschuleName modulTagesschuleName) {
		this.modulTagesschuleName = modulTagesschuleName;
	}

	public LocalDateTime getZeitVon() {
		return zeitVon;
	}

	public void setZeitVon(LocalDateTime zeitVon) {
		this.zeitVon = zeitVon;
	}

	public LocalDateTime getZeitBis() {
		return zeitBis;
	}

	public void setZeitBis(LocalDateTime zeitBis) {
		this.zeitBis = zeitBis;
	}


	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}

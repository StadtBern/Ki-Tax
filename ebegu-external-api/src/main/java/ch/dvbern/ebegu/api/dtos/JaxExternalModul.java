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

import java.io.Serializable;
import java.time.DayOfWeek;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlRootElement;

import ch.dvbern.ebegu.api.enums.JaxExternalModulName;

/**
 * DTO für ein Tagesschul-Modul für die externe Schnittstelle
 */
@XmlRootElement(name = "anmeldungTagesschule")
public class JaxExternalModul implements Serializable {

	private static final long serialVersionUID = 5211944101244853396L;

	@Nonnull
	private DayOfWeek tag;

	@Nonnull
	private JaxExternalModulName stufe;

	public JaxExternalModul(@Nonnull DayOfWeek tag, @Nonnull JaxExternalModulName stufe) {
		this.tag = tag;
		this.stufe = stufe;
	}

	@Nonnull
	public DayOfWeek getTag() {
		return tag;
	}

	public void setTag(@Nonnull DayOfWeek tag) {
		this.tag = tag;
	}

	@Nonnull
	public JaxExternalModulName getStufe() {
		return stufe;
	}

	public void setStufe(@Nonnull JaxExternalModulName stufe) {
		this.stufe = stufe;
	}
}

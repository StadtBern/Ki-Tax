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

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;
import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_SHORT_LENGTH;

/**
 * DTO fuer Stammdaten der Fachstelle
 */
@XmlRootElement(name = "fachstelle")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFachstelle extends JaxAbstractDTO {

	private static final long serialVersionUID = -1277026901764135397L;

	@Size(min = 1, max = DB_DEFAULT_SHORT_LENGTH)
	@NotNull
	private String name;

	@Size(max = DB_DEFAULT_MAX_LENGTH)
	private String beschreibung;

	private boolean behinderungsbestaetigung;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Nullable
	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(@Nullable String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public boolean isBehinderungsbestaetigung() {
		return behinderungsbestaetigung;
	}

	public void setBehinderungsbestaetigung(boolean behinderungsbestaetigung) {
		this.behinderungsbestaetigung = behinderungsbestaetigung;
	}
}

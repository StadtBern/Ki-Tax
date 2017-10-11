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

import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Daten der Belegungen.
 */
@XmlRootElement(name = "belegung")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxBelegung extends JaxAbstractDTO {

	private static final long serialVersionUID = -1297972380574937397L;

	@NotNull
	private Set<JaxModul> module = new LinkedHashSet<>();

	public Set<JaxModul> getModule() {
		return module;
	}

	public void setModule(Set<JaxModul> module) {
		this.module = module;
	}
}

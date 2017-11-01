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

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.lib.date.converters.LocalDateXMLConverter;

/**
 * DTO fuer Daten der Belegungen.
 */
@XmlRootElement(name = "belegungTagesschule")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxBelegungTagesschule extends JaxAbstractDTO {

	private static final long serialVersionUID = -1297972380574937397L;

	@NotNull
	private Set<JaxModulTagesschule> moduleTagesschule = new LinkedHashSet<>();

	@NotNull
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate eintrittsdatum;


	public Set<JaxModulTagesschule> getModuleTagesschule() {
		return moduleTagesschule;
	}

	public void setModuleTagesschule(Set<JaxModulTagesschule> moduleTagesschule) {
		this.moduleTagesschule = moduleTagesschule;
	}

	@NotNull
	public LocalDate getEintrittsdatum() {
		return eintrittsdatum;
	}

	public void setEintrittsdatum(@NotNull LocalDate eintrittsdatum) {
		this.eintrittsdatum = eintrittsdatum;
	}
}

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
import java.time.LocalDate;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.api.enums.JaxExternalFerienName;
import ch.dvbern.lib.date.converters.LocalDateXMLConverter;

/**
 * DTO für ein Ferien-Insel Angebot für die externe Schnittstelle
 */
@XmlRootElement(name = "anmeldungTagesschule")
public class JaxExternalFerieninsel implements Serializable {

	private static final long serialVersionUID = 5211944101244853396L;

	@Nonnull
	private JaxExternalFerienName ferien;

	@Nonnull
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private List<LocalDate> tage;


	public JaxExternalFerieninsel(@Nonnull JaxExternalFerienName ferien, @Nonnull List<LocalDate> tage) {
		this.ferien = ferien;
		this.tage = tage;
	}

	@Nonnull
	public JaxExternalFerienName getFerien() {
		return ferien;
	}

	public void setFerien(@Nonnull JaxExternalFerienName ferien) {
		this.ferien = ferien;
	}

	@Nonnull
	public List<LocalDate> getTage() {
		return tage;
	}

	public void setTage(@Nonnull List<LocalDate> tage) {
		this.tage = tage;
	}
}

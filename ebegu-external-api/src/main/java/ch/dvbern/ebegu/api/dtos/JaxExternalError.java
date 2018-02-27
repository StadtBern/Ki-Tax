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

import ch.dvbern.ebegu.api.enums.JaxExternalErrorCode;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * ErrorObjekt für eine Anmeldung eines Schulamt-Angebots (Tagesschule oder Ferieninsel) für die externe Schnittstelle
 */
@XmlRootElement(name = "error")
public class JaxExternalError implements Serializable {

	private static final long serialVersionUID = 5211944101244853396L;

	@Nonnull
	private String description;

	@Nonnull
	private JaxExternalErrorCode externalErrorCode;


	public JaxExternalError(
		@Nonnull JaxExternalErrorCode externalErrorCode,
		@Nonnull String description) {

		this.externalErrorCode = externalErrorCode;
		this.description = description;
	}

	@Nonnull
	public String getDescription() {
		return description;
	}

	public void setDescription(@Nonnull String description) {
		this.description = description;
	}

	@Nonnull
	public JaxExternalErrorCode getExternalErrorCode() {
		return externalErrorCode;
	}

	public void setExternalErrorCode(@Nonnull JaxExternalErrorCode externalErrorCode) {
		this.externalErrorCode = externalErrorCode;
	}
}

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

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.api.enums.JaxBetreuungsangebotTyp;
import ch.dvbern.ebegu.api.enums.JaxBetreuungsstatus;

/**
 * Super-DTO für eine Anmeldung eines Schulamt-Angebots (Tagesschule oder Ferieninsel) für die externe Schnittstelle
 */
public abstract class JaxExternalAnmeldung implements Serializable {

	private static final long serialVersionUID = 5211944101244853396L;

	@Nonnull
	private String bgNummer;

	@Nonnull
	private JaxBetreuungsstatus betreuungsstatus;

	@Nonnull
	private JaxBetreuungsangebotTyp betreuungsangebotTyp;

	@Nonnull
	private String keyInstitution;


	protected JaxExternalAnmeldung(
		@Nonnull String bgNummer,
		@Nonnull JaxBetreuungsstatus betreuungsstatus,
		@Nonnull JaxBetreuungsangebotTyp betreuungsangebotTyp,
		@Nonnull String keyInstitution) {

		this.bgNummer = bgNummer;
		this.betreuungsstatus = betreuungsstatus;
		this.betreuungsangebotTyp = betreuungsangebotTyp;
		this.keyInstitution = keyInstitution;
	}


	@Nonnull
	public String getBgNummer() {
		return bgNummer;
	}

	public void setBgNummer(@Nonnull String bgNummer) {
		this.bgNummer = bgNummer;
	}

	@Nonnull
	public JaxBetreuungsstatus getBetreuungsstatus() {
		return betreuungsstatus;
	}

	public void setBetreuungsstatus(@Nonnull JaxBetreuungsstatus betreuungsstatus) {
		this.betreuungsstatus = betreuungsstatus;
	}

	@Nonnull
	public JaxBetreuungsangebotTyp getBetreuungsangebotTyp() {
		return betreuungsangebotTyp;
	}

	public void setBetreuungsangebotTyp(@Nonnull JaxBetreuungsangebotTyp betreuungsangebotTyp) {
		this.betreuungsangebotTyp = betreuungsangebotTyp;
	}

	@Nonnull
	public String getKeyInstitution() {
		return keyInstitution;
	}

	public void setKeyInstitution(@Nonnull String keyInstitution) {
		this.keyInstitution = keyInstitution;
	}
}

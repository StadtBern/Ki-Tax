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

import ch.dvbern.ebegu.api.enums.JaxExternalBetreuungsangebotTyp;
import ch.dvbern.ebegu.api.enums.JaxExternalBetreuungsstatus;

/**
 * Super-DTO für eine Anmeldung eines Schulamt-Angebots (Tagesschule oder Ferieninsel) für die externe Schnittstelle
 */
public abstract class JaxExternalAnmeldung implements Serializable {

	private static final long serialVersionUID = 5211944101244853396L;

	@Nonnull
	private String bgNummer;

	@Nonnull
	private JaxExternalBetreuungsstatus betreuungsstatus;

	@Nonnull
	private JaxExternalBetreuungsangebotTyp betreuungsangebotTyp;

	@Nonnull
	private String keyInstitution;

	@Nonnull
	private String kindName;

	@Nonnull
	private String kindVorname;


	protected JaxExternalAnmeldung(
		@Nonnull String bgNummer,
		@Nonnull JaxExternalBetreuungsstatus betreuungsstatus,
		@Nonnull JaxExternalBetreuungsangebotTyp betreuungsangebotTyp,
		@Nonnull String keyInstitution,
		@Nonnull String kindName,
		@Nonnull String kindVorname) {

		this.bgNummer = bgNummer;
		this.betreuungsstatus = betreuungsstatus;
		this.betreuungsangebotTyp = betreuungsangebotTyp;
		this.keyInstitution = keyInstitution;
		this.kindName = kindName;
		this.kindVorname = kindVorname;
	}


	@Nonnull
	public String getBgNummer() {
		return bgNummer;
	}

	public void setBgNummer(@Nonnull String bgNummer) {
		this.bgNummer = bgNummer;
	}

	@Nonnull
	public JaxExternalBetreuungsstatus getBetreuungsstatus() {
		return betreuungsstatus;
	}

	public void setBetreuungsstatus(@Nonnull JaxExternalBetreuungsstatus betreuungsstatus) {
		this.betreuungsstatus = betreuungsstatus;
	}

	@Nonnull
	public JaxExternalBetreuungsangebotTyp getBetreuungsangebotTyp() {
		return betreuungsangebotTyp;
	}

	public void setBetreuungsangebotTyp(@Nonnull JaxExternalBetreuungsangebotTyp betreuungsangebotTyp) {
		this.betreuungsangebotTyp = betreuungsangebotTyp;
	}

	@Nonnull
	public String getKeyInstitution() {
		return keyInstitution;
	}

	public void setKeyInstitution(@Nonnull String keyInstitution) {
		this.keyInstitution = keyInstitution;
	}

	@Nonnull
	public String getKindName() {
		return kindName;
	}

	public void setKindName(@Nonnull String kindName) {
		this.kindName = kindName;
	}

	@Nonnull
	public String getKindVorname() {
		return kindVorname;
	}

	public void setKindVorname(@Nonnull String kindVorname) {
		this.kindVorname = kindVorname;
	}
}

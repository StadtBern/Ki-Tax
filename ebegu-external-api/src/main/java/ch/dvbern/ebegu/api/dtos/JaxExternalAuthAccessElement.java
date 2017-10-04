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
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response Element fuer einen erfolgreichen Login Request
 */
@XmlRootElement(name = "externalAuthAccessDTO")
public class JaxExternalAuthAccessElement implements Serializable {

	private static final long serialVersionUID = -4958118342363368263L;
	@Nonnull
	private final String authId; // = username
	@Nonnull
	private final String authToken;
	@Nonnull
	private final String xsrfToken;
	@Nonnull
	private final String nachname;
	@Nonnull
	private final String vorname;
	@Nonnull
	private final String email;
	@Nonnull
	private final String role;

	@JsonCreator
	public JaxExternalAuthAccessElement(
		@JsonProperty("authId") @Nonnull String authId,
		@JsonProperty("authToken") @Nonnull String authToken,
		@JsonProperty("xsrfToken") @Nonnull String xsrfToken,
		@JsonProperty("nachname") @Nonnull String nachname,
		@JsonProperty("vorname") @Nonnull String vorname,
		@JsonProperty("email") @Nonnull String email,
		@JsonProperty("role") @Nonnull String role) {
		this.authId = Objects.requireNonNull(authId); // currently equals username
		this.authToken = Objects.requireNonNull(authToken);
		this.xsrfToken = Objects.requireNonNull(xsrfToken);
		this.nachname = Objects.requireNonNull(nachname);
		this.vorname = Objects.requireNonNull(vorname);
		this.email = Objects.requireNonNull(email);
		this.role = Objects.requireNonNull(role);
	}



	@Nonnull
	public String getAuthId() {
		return authId;
	}

	@Nonnull
	public String getAuthToken() {
		return authToken;
	}

	@Nonnull
	public String getXsrfToken() {
		return xsrfToken;
	}


	@Nonnull
	public String getNachname() {
		return nachname;
	}

	@Nonnull
	public String getVorname() {
		return vorname;
	}

	@Nonnull
	public String getEmail() {
		return email;
	}

	@Nonnull
	public String getRole() {
		return role;
	}

}

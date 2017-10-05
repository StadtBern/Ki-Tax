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

/**
 * Response DTO Element fuer einen erfolgreichen Login Request
 */
@XmlRootElement(name = "authAccessElement")
public class JaxAuthAccessElementCookieData implements Serializable {

	private static final long serialVersionUID = 5211944101244853396L;

	@Nonnull
	private String authId = "";
	@Nonnull
	private String nachname = "";
	@Nonnull
	private String vorname = "";
	@Nonnull
	private String email = "";
	@Nonnull
	private String role;

	public JaxAuthAccessElementCookieData() {
		// jaxb/jaxrs only
	}

	public JaxAuthAccessElementCookieData(@Nonnull String authId, @Nonnull String nachname,
		@Nonnull String vorname, @Nonnull String email, @Nonnull String role) {
		this.authId = Objects.requireNonNull(authId);
		this.nachname = Objects.requireNonNull(nachname);
		this.vorname = Objects.requireNonNull(vorname);
		this.email = Objects.requireNonNull(email);
		this.role = Objects.requireNonNull(role);
	}

	public JaxAuthAccessElementCookieData(JaxExternalAuthAccessElement access) {
		this(access.getAuthId(),
			access.getNachname(),
			access.getVorname(),
			access.getEmail(),
			access.getRole());
	}

	@Nonnull
	public String getAuthId() {
		return authId;
	}

	public void setAuthId(@Nonnull final String authId) {
		this.authId = authId;
	}

	@Nonnull
	public String getNachname() {
		return nachname;
	}

	public void setNachname(@Nonnull String nachname) {
		this.nachname = nachname;
	}

	@Nonnull
	public String getVorname() {
		return vorname;
	}

	public void setVorname(@Nonnull String vorname) {
		this.vorname = vorname;
	}

	@Nonnull
	public String getEmail() {
		return email;
	}

	public void setEmail(@Nonnull String email) {
		this.email = email;
	}

	@Nonnull
	public String getRole() {
		return role;
	}

	public void setRole(@Nonnull String role) {
		this.role = role;
	}
}

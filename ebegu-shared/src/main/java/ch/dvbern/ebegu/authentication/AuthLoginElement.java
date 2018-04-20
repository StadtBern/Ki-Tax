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
package ch.dvbern.ebegu.authentication;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.enums.UserRole;

/**
 * Wrapper fuer einen Login Request
 */
public class AuthLoginElement implements Serializable {

	private static final long serialVersionUID = 2769899329796452129L;

	@Nonnull
	private String username = "";
	@Nonnull
	private String plainTextPassword = "";
	@Nonnull
	private String nachname = "";
	@Nonnull
	private String vorname = "";
	@Nonnull
	private String email = "";
	@Nonnull
	private UserRole role;

	public AuthLoginElement(@Nonnull String username, @Nonnull String plainTextpassword, @Nonnull String nachname,
		@Nonnull String vorname, @Nonnull String email, @Nonnull UserRole role) {
		this.username = Objects.requireNonNull(username);
		this.plainTextPassword = Objects.requireNonNull(plainTextpassword);
		this.nachname = Objects.requireNonNull(nachname);
		this.vorname = Objects.requireNonNull(vorname);
		this.email = Objects.requireNonNull(email);
		this.role = Objects.requireNonNull(role);
	}

	@Nonnull
	public String getUsername() {
		return username;
	}

	public void setUsername(@Nonnull final String username) {
		this.username = username;
	}

	@Nonnull
	public String getPlainTextPassword() {
		return plainTextPassword;
	}

	public void setPlainTextPassword(@Nonnull final String password) {
		this.plainTextPassword = password;
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
	public UserRole getRole() {
		return role;
	}

	public void setRole(@Nonnull UserRole role) {
		this.role = role;
	}
}

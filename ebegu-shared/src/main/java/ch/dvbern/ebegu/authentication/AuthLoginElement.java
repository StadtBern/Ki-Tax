/*
 * Copyright (c) 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.authentication;

import ch.dvbern.ebegu.enums.UserRole;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

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

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

/**
 * Response Element fuer einen erfolgreichen Login Request
 */
public class AuthAccessElement implements Serializable {

	private static final long serialVersionUID = 5211944101244853396L;

	@Nonnull
	private final String authId;
	@Nonnull
	private final String authToken;
	@Nonnull
	private final String xsrfToken;
	@Nonnull
	private final String username;
	@Nonnull
	private final String nachname;
	@Nonnull
	private final String vorname;
	@Nonnull
	private final String email;
	@Nonnull
	private final UserRole role;

	public AuthAccessElement(@Nonnull String authId, @Nonnull String authToken, @Nonnull String xsrfToken,
							 @Nonnull String username, @Nonnull String nachname, @Nonnull String vorname,
							 @Nonnull String email, @Nonnull UserRole role) {
		this.authId = Objects.requireNonNull(authId);
		this.authToken = Objects.requireNonNull(authToken);
		this.xsrfToken = Objects.requireNonNull(xsrfToken);
		this.username = Objects.requireNonNull(username);
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
	public String getUsername() {
		return username;
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
	public UserRole getRole() {
		return role;
	}
}

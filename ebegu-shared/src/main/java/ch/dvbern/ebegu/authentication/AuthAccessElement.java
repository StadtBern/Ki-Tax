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

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.dvbern.ebegu.enums.UserRole;

/**
 * Response Element fuer einen erfolgreichen Login Request
 */
public class AuthAccessElement implements Serializable {

	private static final long serialVersionUID = 5211944101244853396L;

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
	private final UserRole role;

	@JsonCreator
	public AuthAccessElement(
		@JsonProperty ("authId") @Nonnull String authId,
		@JsonProperty ("authToken") @Nonnull String authToken,
		@JsonProperty ("xsrfToken") @Nonnull String xsrfToken,
		@JsonProperty ("nachname") @Nonnull String nachname,
		@JsonProperty ("vorname") @Nonnull String vorname,
		@JsonProperty ("email") @Nonnull String email,
		@JsonProperty ("role") @Nonnull UserRole role) {
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
	public UserRole getRole() {
		return role;
	}
}

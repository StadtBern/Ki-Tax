/*
 * Copyright (c) 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
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

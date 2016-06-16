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

import ch.dvbern.ebegu.enums.UserRole;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Response Element fuer einen erfolgreichen Login Request
 */
@XmlRootElement(name = "authAccessElement")
public class JaxAuthAccessElement implements Serializable {

	private static final long serialVersionUID = 5211944101244853396L;

	@Nonnull
	private String authId = "";

	@Nonnull
	private String userId = "";
	@Nonnull
	private String username = "";
	@Nonnull
	private String nachname = "";
	@Nonnull
	private String vorname = "";
	@Nonnull
	private String email = "";
	@Nonnull
	private Set<UserRole> roles = EnumSet.noneOf(UserRole.class);

	public JaxAuthAccessElement() {
		// jaxb/jaxrs only
	}

	public JaxAuthAccessElement(@Nonnull String authId, @Nonnull String userId, @Nonnull String username, @Nonnull String nachname,
								@Nonnull String vorname, @Nonnull String email, @Nonnull Set<UserRole> roles) {
		this.authId = Objects.requireNonNull(authId);
		this.userId = Objects.requireNonNull(userId);
		this.username = Objects.requireNonNull(username);
		this.nachname = Objects.requireNonNull(nachname);
		this.vorname = Objects.requireNonNull(vorname);
		this.email = Objects.requireNonNull(email);
		this.roles = Objects.requireNonNull(roles);
	}

	@Nonnull
	public String getAuthId() {
		return authId;
	}

	public void setAuthId(@Nonnull final String authId) {
		this.authId = authId;
	}

	@Nonnull
	public String getUserId() {
		return userId;
	}

	public void setUserId(@Nonnull String userId) {
		this.userId = userId;
	}

	@Nonnull
	public String getUsername() {
		return username;
	}

	public void setUsername(@Nonnull String username) {
		this.username = username;
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
	public Set<UserRole> getRoles() {
		return roles;
	}

	public void setRoles(@Nonnull Set<UserRole> roles) {
		this.roles = roles;
	}
}

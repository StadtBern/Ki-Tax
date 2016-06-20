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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Wrapper fuer einen Login Request
 */
@XmlRootElement(name = "authLoginElement")
public class JaxAuthLoginElement implements Serializable {

	private static final long serialVersionUID = 2769899329796452129L;

	@Nonnull
	private String username = "";
	@Nonnull
	private String password = "";
	@Nonnull
	private String userId = "";
	@Nonnull
	private String nachname = "";
	@Nonnull
	private String vorname = "";
	@Nonnull
	private String email = "";
	@Nonnull
	private Set<UserRole> roles = new HashSet<>();
	@NotNull
	private JaxMandant mandant;



	private JaxAuthLoginElement() {
		// fuer Jaxb/JaxRS
	}

	public JaxAuthLoginElement(@Nonnull String username, @Nonnull String plainTextpassword) {
		this.username = Objects.requireNonNull(username);
		this.password = Objects.requireNonNull(plainTextpassword);
	}

	@Nonnull
	public String getUsername() {
		return username;
	}

	public void setUsername(@Nonnull String username) {
		this.username = username;
	}

	@Nonnull
	public String getPassword() {
		return password;
	}

	public void setPassword(@Nonnull String password) {
		this.password = password;
	}

	@Nonnull
	public String getUserId() {
		return userId;
	}

	public void setUserId(@Nonnull String userId) {
		this.userId = userId;
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

	public JaxMandant getMandant() {
		return mandant;
	}

	public void setMandant(JaxMandant mandant) {
		this.mandant = mandant;
	}
}

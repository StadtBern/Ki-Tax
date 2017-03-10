/*
 * Copyright (c) 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.UserRole;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

@Entity
@Table(indexes = {
	@Index(columnList = "benutzer_id", name = "IX_authorisierter_benutzer"),
	@Index(columnList = "authToken,benutzer_id", name = "IX_authorisierter_benutzer_token")
})
public class AuthorisierterBenutzer extends AbstractEntity {

	private static final long serialVersionUID = 6372688971724279665L;

	@Column(nullable = false, updatable = false)
	private final LocalDateTime firstLogin = LocalDateTime.now();

	@Column(nullable = false)
	private LocalDateTime lastLogin = LocalDateTime.now();

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_authorisierter_benutzer_benutzer_id"))
	private Benutzer benutzer = null;

	/**
	 * Dies entspricht dem token aus dem cookie
	 */
	@NotNull
	@Column(updatable = false)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String authToken = null;


	/**
	 * Wiederholung von Benutzer.username damit wir nicht joinen muessen
	 */
	@NotNull
	@Column(nullable = false)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String username = null;

	/**
	 * Wiederholung von benutzer.role damit wir nicht joinen muessen
	 */
	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(updatable = false, nullable = false)
	private UserRole role;

	@Nullable
	@Column(nullable = true)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String sessionIndex;

	@Nullable
	@Column(nullable = true)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String samlNameId;

	@Nullable
	@Column(nullable = true)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String samlSPEntityID;

	@Nullable
	@Column(nullable = true)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String samlIDPEntityID;

	@PrePersist
	protected void prePersist() {
		lastLogin = LocalDateTime.now();
	}

	public Benutzer getBenutzer() {
		return benutzer;
	}

	public void setBenutzer(Benutzer benutzer) {
		this.benutzer = benutzer;
	}

	@Nonnull
	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(@Nonnull final String authToken) {
		this.authToken = authToken;
	}

	@Nonnull
	public LocalDateTime getFirstLogin() {
		return firstLogin;
	}

	@Nonnull
	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(@Nonnull final LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}


	@Nullable
	public String getSessionIndex() {
		return sessionIndex;
	}

	public void setSessionIndex(@Nullable String sessionIndex) {
		this.sessionIndex = sessionIndex;
	}

	@Nullable
	public String getSamlNameId() {
		return samlNameId;
	}

	public void setSamlNameId(@Nullable String samlNameId) {
		this.samlNameId = samlNameId;
	}

	@Nullable
	public String getSamlSPEntityID() {
		return samlSPEntityID;
	}

	public void setSamlSPEntityID(@Nullable String samlSPEntityID) {
		this.samlSPEntityID = samlSPEntityID;
	}

	@Nullable
	public String getSamlIDPEntityID() {
		return samlIDPEntityID;
	}

	public void setSamlIDPEntityID(@Nullable String samlIDPEntityID) {
		this.samlIDPEntityID = samlIDPEntityID;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("username", username)
			.append("role", role)
			.append("sessionIndex", sessionIndex)
			.toString();
	}
}

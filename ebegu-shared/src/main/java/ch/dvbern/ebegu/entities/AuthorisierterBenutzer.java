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

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_authorisierter_benutzer_benutzer_id"))
	private Benutzer benutzer = null;

	// todo team Dieses Feld muss aus Sicherheitsgrunden entfernt werden wenn das dummylogin nicht mehr benoetigt wird
	@Nullable
	@Size(max = DB_DEFAULT_MAX_LENGTH)
	private String password = null;

	@NotNull
	@Column(updatable = false)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String authToken = null;

	@PrePersist
	protected void prePersist() {
		lastLogin = LocalDateTime.now();
	}



	@Nonnull
	public String getAuthId() {
		return String.valueOf(this.benutzer.getUsername());
	}

	public Benutzer getBenutzer() {
		return benutzer;
	}

	public void setBenutzer(Benutzer benutzer) {
		this.benutzer = benutzer;
	}

	@Nullable
	public String getPassword() {
		return password;
	}

	public void setPassword(@Nullable String password) {
		this.password = password;
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
}

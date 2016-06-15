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

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Wrapper fuer einen Login Request
 */
public class AuthLoginElement implements Serializable {

	private static final long serialVersionUID = 2769899329796452129L;

	@Nonnull
	private String username = "";
	@Nonnull
	private String plainTextPassword = "";

	public AuthLoginElement(@Nonnull String username, @Nonnull String plainTextpassword) {
		this.username = Objects.requireNonNull(username);
		this.plainTextPassword = Objects.requireNonNull(plainTextpassword);
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
}

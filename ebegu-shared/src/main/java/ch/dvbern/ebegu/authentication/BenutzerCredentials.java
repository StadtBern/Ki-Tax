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

public class BenutzerCredentials {

	@Nonnull
	private final String username;

	@Nonnull
	private final String passwordEncrypted;

	@Nonnull
	private final String authToken;

	public BenutzerCredentials(@Nonnull String username, @Nonnull String passwordEncrypted, @Nonnull String authToken) {
		this.username = username;
		this.passwordEncrypted = passwordEncrypted;
		this.authToken = authToken;
	}

	@Nonnull
	public String getUsername() {
		return username;
	}

	@Nonnull
	public String getPasswordEncrypted() {
		return passwordEncrypted;
	}

	@Nonnull
	public String getAuthToken() {
		return authToken;
	}
}

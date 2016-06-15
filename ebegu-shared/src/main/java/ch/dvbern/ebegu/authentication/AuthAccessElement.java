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
 * Response Element fuer einen erfolgreichen Login Request
 */
public class AuthAccessElement implements Serializable {

	private static final long serialVersionUID = 5211944101244853396L;

	@Nonnull
	private final String authId;
	@Nonnull
	private final String userId;
	@Nonnull
	private final String authToken;
	@Nonnull
	private final String xsrfToken;
//	@Nonnull
//	private final Set<UserRole> roles;

	public AuthAccessElement(@Nonnull String authId, @Nonnull String authToken, @Nonnull String xsrfToken,
							 @Nonnull String userId) {
		this.authId = Objects.requireNonNull(authId);
		this.userId = Objects.requireNonNull(userId);
		this.authToken = Objects.requireNonNull(authToken);
		this.xsrfToken = Objects.requireNonNull(xsrfToken);
	}

	@Nonnull
	public String getAuthId() {
		return authId;
	}

	@Nonnull
	public String getUserId() {
		return userId;
	}

	@Nonnull
	public String getAuthToken() {
		return authToken;
	}

	@Nonnull
	public String getXsrfToken() {
		return xsrfToken;
	}

//	@Nonnull
//	public Set<UserRole> getRoles() {
//		return roles;
//	}
}

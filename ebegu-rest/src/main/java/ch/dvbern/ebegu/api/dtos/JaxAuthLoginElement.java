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

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

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

	public void setUsername(@Nonnull final String username) {
		this.username = username;
	}

	@Nonnull
	public String getPassword() {
		return password;
	}

	public void setPassword(@Nonnull final String password) {
		this.password = password;
	}
}

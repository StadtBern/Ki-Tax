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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Wrapper DTO fuer einen Login Request
 */
@XmlRootElement(name = "iamUser")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxIamUser extends JaxAbstractDTO {

	private static final long serialVersionUID = 2769899329796452129L;

	@Nonnull
	private String loginName = "";

	private String surname = "";

	private String givenName = "";
	@Nonnull
	private String email = "";

	private String commonName ="";
//	@Nonnull
//	private UserRole role;
//	private JaxMandant mandant;
//	@Nullable
//	private JaxTraegerschaft traegerschaft;
//	@Nullable
//	private JaxInstitution institution;


	@Nonnull
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(@Nonnull String loginName) {
		this.loginName = loginName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	@Nonnull
	public String getEmail() {
		return email;
	}

	public void setEmail(@Nonnull String email) {
		this.email = email;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
}

package ch.dvbern.ebegu.api.connector;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import ch.dvbern.ebegu.enums.UserRole;

/**
 * This Transfer Object is used to pass on Info about an external Benutzer from an external Login
 * to E-BEGU
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxExternalBenutzer implements Serializable {
	private static final long serialVersionUID = -2418503680503363364L;


	private String username;
	private String vorname;
	private String nachname;
	private String email;
	private String institutionId;
	private String traegerschaftId;
	private String mandantId;
	private UserRole role;

	private String commonName;
	private String telephoneNumber;
	private String mobile;
	private String preferredLang;
	private String postalAddress;
	private String street;
	private String postalCode;
	private String state;
	private String countryCode;
	private String country;


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMandantId() {
		return mandantId;
	}

	public void setMandantId(String mandantId) {
		this.mandantId = mandantId;
	}

	public String getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(String institutionId) {
		this.institutionId = institutionId;
	}

	public String getTraegerschaftId() {
		return traegerschaftId;
	}

	public void setTraegerschaftId(String traegerschaftId) {
		this.traegerschaftId = traegerschaftId;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	//unused attributes


	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPreferredLang() {
		return preferredLang;
	}

	public void setPreferredLang(String preferredLang) {
		this.preferredLang = preferredLang;
	}

	public String getPostalAddress() {
		return postalAddress;
	}

	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}

package ch.dvbern.ebegu.api.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.lib.date.converters.LocalDateTimeXMLConverter;

/**
 * This transfer Object is used to pass the relevant Info about a successfull login
 * from an external login system to E-BEGU
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxExternalAuthorisierterBenutzer implements Serializable {

	private static final long serialVersionUID = -5370653568368950813L;

	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime lastLogin = LocalDateTime.now();

	private JaxExternalBenutzer benutzer = null;

	/**
	 * Dies entspricht dem token aus dem cookie
	 */
	@NotNull
	private String authToken = null;

	/**
	 * Wiederholung von Benutzer.username damit wir nicht joinen muessen
	 */
	@NotNull
	private String username = null;

	/**
	 * Wiederholung von benutzer.role damit wir nicht joinen muessen
	 */
	@NotNull
	private String role;

	@Nullable
	private String sessionIndex;

	@Nullable
	private String samlNameId;

	@Nullable
	private String samlSPEntityID;

	@Nullable
	private String samlIDPEntityID;

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public JaxExternalBenutzer getBenutzer() {
		return benutzer;
	}

	public void setBenutzer(JaxExternalBenutzer benutzer) {
		this.benutzer = benutzer;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
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
}

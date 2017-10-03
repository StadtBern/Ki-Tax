package ch.dvbern.ebegu.api.dtos;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response Element fuer einen erfolgreichen Login Request
 */
@XmlRootElement(name = "externalAuthAccessDTO")
public class JaxExternalAuthAccessElement implements Serializable {

	private static final long serialVersionUID = -4958118342363368263L;
	@Nonnull
	private final String authId; // = username
	@Nonnull
	private final String authToken;
	@Nonnull
	private final String xsrfToken;
	@Nonnull
	private final String nachname;
	@Nonnull
	private final String vorname;
	@Nonnull
	private final String email;
	@Nonnull
	private final String role;

	@JsonCreator
	public JaxExternalAuthAccessElement(
		@JsonProperty("authId") @Nonnull String authId,
		@JsonProperty("authToken") @Nonnull String authToken,
		@JsonProperty("xsrfToken") @Nonnull String xsrfToken,
		@JsonProperty("nachname") @Nonnull String nachname,
		@JsonProperty("vorname") @Nonnull String vorname,
		@JsonProperty("email") @Nonnull String email,
		@JsonProperty("role") @Nonnull String role) {
		this.authId = Objects.requireNonNull(authId); // currently equals username
		this.authToken = Objects.requireNonNull(authToken);
		this.xsrfToken = Objects.requireNonNull(xsrfToken);
		this.nachname = Objects.requireNonNull(nachname);
		this.vorname = Objects.requireNonNull(vorname);
		this.email = Objects.requireNonNull(email);
		this.role = Objects.requireNonNull(role);
	}

	@Nonnull
	public String getAuthId() {
		return authId;
	}

	@Nonnull
	public String getAuthToken() {
		return authToken;
	}

	@Nonnull
	public String getXsrfToken() {
		return xsrfToken;
	}

	@Nonnull
	public String getNachname() {
		return nachname;
	}

	@Nonnull
	public String getVorname() {
		return vorname;
	}

	@Nonnull
	public String getEmail() {
		return email;
	}

	@Nonnull
	public String getRole() {
		return role;
	}

}

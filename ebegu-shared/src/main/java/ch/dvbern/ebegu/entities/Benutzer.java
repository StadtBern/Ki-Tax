package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.UserRole;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

@Entity
@Table(indexes = {
	@Index(columnList = "username,mandant_id", name = "IX_benutzer_username_mandant")
})
public class Benutzer extends AbstractEntity {

	private static final long serialVersionUID = 6372688971894279665L;

	@NotNull
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String username = null;

	@NotNull
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String userId = null;

	@NotNull
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String nachname = null;

	@NotNull
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String vorname = null;

	@NotNull
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String email = null;

	@ElementCollection
	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	@NotNull
	private Set<UserRole> roles;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_benutzer_mandant_id"))
	private Mandant mandant;



	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<UserRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<UserRole> roles) {
		this.roles = roles;
	}

	public Mandant getMandant() {
		return mandant;
	}

	public void setMandant(Mandant mandant) {
		this.mandant = mandant;
	}
}

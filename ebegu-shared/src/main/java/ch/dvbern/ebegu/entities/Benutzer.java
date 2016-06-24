package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.UserRole;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

@Entity
@Table(indexes = {
	@Index(columnList = "username,mandant_id", name = "IX_benutzer_username_mandant")
})
public class Benutzer extends AbstractEntity {

	private static final long serialVersionUID = 6372688971894279665L;

	@NotNull
	@Column(nullable = false)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String username = null;

	@NotNull
	@Column(nullable = false)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String nachname = null;

	@NotNull
	@Column(nullable = false)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String vorname = null;

	@NotNull
	@Column(nullable = false)
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	private String email = null;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	@NotNull
	private UserRole role;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_benutzer_mandant_id"))
	private Mandant mandant;



	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public Mandant getMandant() {
		return mandant;
	}

	public void setMandant(Mandant mandant) {
		this.mandant = mandant;
	}
}

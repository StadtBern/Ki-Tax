package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.validators.CheckBenutzerRoles;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

@Entity
@Table(indexes = {
	@Index(columnList = "username,mandant_id", name = "IX_benutzer_username_mandant")
})
@Audited
@AssociationOverrides({
   @AssociationOverride(name = "mandant",
	   //wird von hibernate 5.0.6 ignoriert... https://hibernate.atlassian.net/browse/HHH-10387
      joinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_benutzer_mandant_id")))
})
@CheckBenutzerRoles
public class Benutzer extends AbstractMandantEntity {

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

	@Nullable
	@ManyToOne(optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_benutzer_institution_id"))
	private Institution institution;

	@Nullable
	@ManyToOne(optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_benutzer_traegerschaft_id"))
	private Traegerschaft traegerschaft;



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

	@Nullable
	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(@Nullable Institution institution) {
		this.institution = institution;
	}

	@Nullable
	public Traegerschaft getTraegerschaft() {
		return traegerschaft;
	}

	public void setTraegerschaft(@Nullable Traegerschaft traegerschaft) {
		this.traegerschaft = traegerschaft;
	}

	public String getFullName() {
		return (this.vorname != null ? this.vorname :  "")  + " "
			+ (this.nachname != null ?  this.nachname : "");
	}
}

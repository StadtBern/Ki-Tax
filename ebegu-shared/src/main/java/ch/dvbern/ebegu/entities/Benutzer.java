package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.validators.CheckBenutzerRoles;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Objects;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

@Entity
@Table(
	uniqueConstraints = {@UniqueConstraint(columnNames = "username", name = "UK_username")},
	indexes = {@Index(columnList = "username", name = "IX_benutzer_username")
})
@Audited
@CheckBenutzerRoles
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_benutzer_mandant_id"))
	private Mandant mandant;

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

	public Mandant getMandant() {
		return mandant;
	}

	public void setMandant(Mandant mandant) {
		this.mandant = mandant;
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

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.appendSuper(super.toString())
			.append("username", username)
			.append("role", role)
			.toString();
	}

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		final Benutzer otherBenutzer = (Benutzer) other;
		return Objects.equals(getUsername(), otherBenutzer.getUsername());
	}
}

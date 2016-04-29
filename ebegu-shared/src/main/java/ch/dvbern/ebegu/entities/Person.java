package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Entity fuer personendaten
 */
@Audited
@Entity
public class Person extends AbstractEntity {

	private static final long serialVersionUID = -9032257320578372570L;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String vorname;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@NotNull
	@Column(nullable = false)
	private String nachname;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	@NotNull
	private Geschlecht geschlecht;

	@Pattern(regexp = Constants.REGEX_EMAIL, message = "{validator.constraints.Email.message}")
	@Size(min = 5, max = DB_DEFAULT_MAX_LENGTH)
	@NotNull
	@Column(nullable = false)
	private String mail;

	@Column(nullable = false)
	@NotNull
	private LocalDate geburtsdatum;

	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	@Pattern(regexp = Constants.REGEX_TELEFON_MOBILE, message = "{error_invalid_mobilenummer}")
	private String mobile;

	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	@Pattern(regexp = Constants.REGEX_TELEFON, message = "{error_invalid_mobilenummer}")
	private String telefon;

	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String telefonAusland;

	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String zpvNumber; //todo team, es ist noch offen was das genau fuer ein identifier ist



	@Valid
	@Size(min = 1)
	@Nonnull
	// es handelt sich um eine "private" Relation, das heisst Adressen koennen nie einer anderen Person zugeordnet werden
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Adresse> adressen = new ArrayList<>();

	public boolean addAdresse(@Nonnull Adresse adresse) {
		adresse.setPerson(this);
		return !adressen.contains(adresse) && adressen.add(adresse);
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

	public Geschlecht getGeschlecht() {
		return geschlecht;
	}

	public void setGeschlecht(Geschlecht geschlecht) {
		this.geschlecht = geschlecht;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public LocalDate getGeburtsdatum() {
		return geburtsdatum;
	}

	public void setGeburtsdatum(LocalDate geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}

	public String getTelefonAusland() {
		return telefonAusland;
	}

	public void setTelefonAusland(String telefonAusland) {
		this.telefonAusland = telefonAusland;
	}

	public String getZpvNumber() {
		return zpvNumber;
	}

	public void setZpvNumber(String zpvNumber) {
		this.zpvNumber = zpvNumber;
	}


	@Nonnull
	public List<Adresse> getAdressen() {
		return adressen;
	}

	public void setAdressen(@Nonnull List<Adresse> adressen) {
		this.adressen = adressen;
	}
}

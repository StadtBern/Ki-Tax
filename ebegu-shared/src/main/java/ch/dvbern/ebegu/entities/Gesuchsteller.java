package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Entity fuer gesuchstellerdaten
 */
@Audited
@Entity
public class Gesuchsteller extends AbstractPersonEntity {

	private static final long serialVersionUID = -9032257320578372570L;

	@Pattern(regexp = Constants.REGEX_EMAIL, message = "{validator.constraints.Email.message}")
	@Size(min = 5, max = DB_DEFAULT_MAX_LENGTH)
	@NotNull
	@Column(nullable = false)
	private String mail;

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

	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gesuchsteller")
	private FinanzielleSituationContainer finanzielleSituationContainer;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gesuchsteller")
	private Set<ErwerbspensumContainer> erwerbspensenContainers = new HashSet<>();


	@Valid
	@Size(min = 1)
	@Nonnull
	// es handelt sich um eine "private" Relation, das heisst Adressen koennen nie einer anderen Gesuchsteller zugeordnet werden
	@OneToMany(mappedBy = "gesuchsteller", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Adresse> adressen = new ArrayList<>();


	public boolean addAdresse(@Nonnull Adresse adresse) {
		adresse.setGesuchsteller(this);
		return !adressen.contains(adresse) && adressen.add(adresse);
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
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

	public FinanzielleSituationContainer getFinanzielleSituationContainer() {
		return finanzielleSituationContainer;
	}

	public Set<ErwerbspensumContainer> getErwerbspensenContainers() {
		return erwerbspensenContainers;
	}

	public void setErwerbspensenContainers(Set<ErwerbspensumContainer> erwerbspensenContainers) {
		this.erwerbspensenContainers = erwerbspensenContainers;
	}

	public void setFinanzielleSituationContainer(FinanzielleSituationContainer finanzielleSituationContainer) {
		this.finanzielleSituationContainer = finanzielleSituationContainer;
		if (finanzielleSituationContainer != null &&
			(finanzielleSituationContainer.getGesuchsteller() == null || !finanzielleSituationContainer.getGesuchsteller().equals(this))) {
			finanzielleSituationContainer.setGesuchsteller(this);
		}
	}

	public boolean addErwerbspensumContainer(ErwerbspensumContainer erwerbspensumToAdd) {
		erwerbspensumToAdd.setGesuchsteller(this);
		return !erwerbspensenContainers.contains(erwerbspensumToAdd) &&
			erwerbspensenContainers.add(erwerbspensumToAdd);
	}
}

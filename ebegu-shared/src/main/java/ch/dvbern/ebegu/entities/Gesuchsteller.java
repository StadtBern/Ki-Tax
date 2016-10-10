package ch.dvbern.ebegu.entities;


import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

	@Nullable
	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gesuchsteller")
	private FinanzielleSituationContainer finanzielleSituationContainer;

	@Nullable
	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gesuchsteller")
	private EinkommensverschlechterungContainer einkommensverschlechterungContainer;

	@Nonnull
	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gesuchsteller")
	private Set<ErwerbspensumContainer> erwerbspensenContainers = new HashSet<>();

	@Valid
	@Size(min = 1)
	@Nonnull
	// es handelt sich um eine "private" Relation, das heisst Adressen koennen nie einer anderen Gesuchsteller zugeordnet werden
	@OneToMany(mappedBy = "gesuchsteller", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GesuchstellerAdresse> adressen = new ArrayList<>();

	@NotNull
	private boolean diplomatenstatus;


	public Gesuchsteller() {
	}

	public Gesuchsteller(@Nonnull Gesuchsteller toCopy) {
		super(toCopy);
		this.mail = toCopy.mail;
		this.mobile = toCopy.mobile;
		this.telefon = toCopy.telefon;
		this.telefonAusland = toCopy.telefonAusland;
		this.zpvNumber = toCopy.zpvNumber;
		if (toCopy.finanzielleSituationContainer != null) {
			this.finanzielleSituationContainer = new FinanzielleSituationContainer(toCopy.finanzielleSituationContainer, this);
		}
		if (toCopy.einkommensverschlechterungContainer != null) {
			this.einkommensverschlechterungContainer = new EinkommensverschlechterungContainer(toCopy.einkommensverschlechterungContainer, this);
		}
		for (ErwerbspensumContainer erwerbspensumContainer : toCopy.erwerbspensenContainers) {
			this.addErwerbspensumContainer(new ErwerbspensumContainer(erwerbspensumContainer, this));
		}
		for (GesuchstellerAdresse gesuchstellerAdresse : toCopy.adressen) {
			this.addAdresse(new GesuchstellerAdresse(gesuchstellerAdresse, this));
		}
		this.diplomatenstatus = toCopy.diplomatenstatus;
	}

	public boolean addAdresse(@Nonnull final GesuchstellerAdresse gesuchstellerAdresse) {
		gesuchstellerAdresse.setGesuchsteller(this);
		return !adressen.contains(gesuchstellerAdresse) && adressen.add(gesuchstellerAdresse);
	}

	public String getMail() {
		return mail;
	}

	public void setMail(final String mail) {
		this.mail = mail;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(final String mobile) {
		this.mobile = mobile;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(final String telefon) {
		this.telefon = telefon;
	}

	public String getTelefonAusland() {
		return telefonAusland;
	}

	public void setTelefonAusland(final String telefonAusland) {
		this.telefonAusland = telefonAusland;
	}

	public String getZpvNumber() {
		return zpvNumber;
	}

	public void setZpvNumber(final String zpvNumber) {
		this.zpvNumber = zpvNumber;
	}

	@Nonnull
	public List<GesuchstellerAdresse> getAdressen() {
		return adressen;
	}

	public void setAdressen(@Nonnull final List<GesuchstellerAdresse> adressen) {
		this.adressen = adressen;
	}

	@Nullable
	public FinanzielleSituationContainer getFinanzielleSituationContainer() {
		return finanzielleSituationContainer;
	}

	@Nonnull
	public Set<ErwerbspensumContainer> getErwerbspensenContainers() {
		return erwerbspensenContainers;
	}

	public void setErwerbspensenContainers(@Nonnull final Set<ErwerbspensumContainer> erwerbspensenContainers) {
		this.erwerbspensenContainers = erwerbspensenContainers;
	}

	public boolean isDiplomatenstatus() {
		return diplomatenstatus;
	}

	public void setDiplomatenstatus(final boolean diplomatenstatus) {
		this.diplomatenstatus = diplomatenstatus;
	}

	public void setFinanzielleSituationContainer(@Nullable final FinanzielleSituationContainer finanzielleSituationContainer) {
		this.finanzielleSituationContainer = finanzielleSituationContainer;
		if (finanzielleSituationContainer != null &&
			(finanzielleSituationContainer.getGesuchsteller() == null || !finanzielleSituationContainer.getGesuchsteller().equals(this))) {
			finanzielleSituationContainer.setGesuchsteller(this);
		}
	}

	public boolean addErwerbspensumContainer(final ErwerbspensumContainer erwerbspensumToAdd) {
		erwerbspensumToAdd.setGesuchsteller(this);
		return !erwerbspensenContainers.contains(erwerbspensumToAdd) &&
			erwerbspensenContainers.add(erwerbspensumToAdd);
	}

	@Nullable
	public EinkommensverschlechterungContainer getEinkommensverschlechterungContainer() {
		return einkommensverschlechterungContainer;
	}

	public void setEinkommensverschlechterungContainer(@Nullable final EinkommensverschlechterungContainer einkommensverschlechterungContainer) {
		this.einkommensverschlechterungContainer = einkommensverschlechterungContainer;
		if (einkommensverschlechterungContainer != null &&
			(einkommensverschlechterungContainer.getGesuchsteller() == null || !einkommensverschlechterungContainer.getGesuchsteller().equals(this))) {
			einkommensverschlechterungContainer.setGesuchsteller(this);
		}
	}
}

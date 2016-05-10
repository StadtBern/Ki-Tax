package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.Land;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Entitaet zum Speichern von Adressen einer Person in der Datenbank.
 */
@Audited
@Entity
public class PersonenAdresse extends AbstractDateRangedEntity {

	private static final long serialVersionUID = -7687645920281069260L;


	@NotNull
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
	private Adresse adresse = new Adresse();


	@NotNull
	@Enumerated(EnumType.STRING)
	private AdresseTyp adresseTyp = AdresseTyp.WOHNADRESSE;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_adresse_gesuchsteller_id"))
	private Gesuchsteller gesuchsteller;


	public PersonenAdresse() {
	}


	public AdresseTyp getAdresseTyp() {
		return adresseTyp;
	}

	public void setAdresseTyp(AdresseTyp adresseTyp) {
		this.adresseTyp = adresseTyp;
	}

	public Gesuchsteller getGesuchsteller() {
		return gesuchsteller;
	}

	public void setGesuchsteller(Gesuchsteller gesuchsteller) {
		this.gesuchsteller = gesuchsteller;
	}

	public Adresse getAdresse() {
		return adresse;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}

	//delegierte getter und setter
	@Nonnull
	public String getStrasse() {
		return adresse.getStrasse();
	}

	public void setLand(Land land) {
		adresse.setLand(land);
	}


	public Land getLand() {
		return adresse.getLand();
	}

	@Nullable
	public String getHausnummer() {
		return adresse.getHausnummer();
	}

	public void setZusatzzeile(@Nullable String zusatzzeile) {
		adresse.setZusatzzeile(zusatzzeile);
	}

	@Nullable
	public String getGemeinde() {
		return adresse.getGemeinde();
	}

	@Nonnull
	public String getPlz() {
		return adresse.getPlz();
	}

	public void setStrasse(@Nonnull String strasse) {
		adresse.setStrasse(strasse);
	}

	@Nullable
	public String getZusatzzeile() {
		return adresse.getZusatzzeile();
	}

	@Nonnull
	public String getOrt() {
		return adresse.getOrt();
	}

	public void setOrt(@Nonnull String ort) {
		adresse.setOrt(ort);
	}

	public void setHausnummer(@Nullable String hausnummer) {
		adresse.setHausnummer(hausnummer);
	}

	public void setGemeinde(@Nullable String gemeinde) {
		adresse.setGemeinde(gemeinde);
	}

	public void setPlz(@Nonnull String plz) {
		adresse.setPlz(plz);
	}

	@SuppressWarnings({"ObjectEquality", "OverlyComplexBooleanExpression"})
	public boolean isSame(PersonenAdresse otherAdr) {
		if (this == otherAdr) {
			return true;
		}
		if (otherAdr == null || getClass() != otherAdr.getClass()) {
			return false;
		}


		return Objects.equals(adresse, otherAdr.getAdresse()) &&
			adresseTyp == otherAdr.getAdresseTyp() &&
			Objects.equals(getGueltigkeit(), otherAdr.getGueltigkeit());

	}


}

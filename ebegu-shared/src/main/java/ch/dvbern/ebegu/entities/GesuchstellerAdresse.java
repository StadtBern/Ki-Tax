package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.types.DateRange;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Entitaet zum Speichern von Adressen einer Person in der Datenbank.
 */
@Audited
@Entity
public class GesuchstellerAdresse extends Adresse {

	private static final long serialVersionUID = -7687645920281069260L;


	@NotNull
	@Enumerated(EnumType.STRING)
	private AdresseTyp adresseTyp = AdresseTyp.WOHNADRESSE;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuchsteller_adresse_gesuchsteller_id"))
	private Gesuchsteller gesuchsteller;

	@Column(nullable = false)
	private boolean nichtInGemeinde = false;


	public GesuchstellerAdresse() {
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

	public boolean isNichtInGemeinde() {
		return nichtInGemeinde;
	}

	public void setNichtInGemeinde(boolean nichtInGemeinde) {
		this.nichtInGemeinde = nichtInGemeinde;
	}


	@SuppressWarnings({"ObjectEquality", "OverlyComplexBooleanExpression"})
	public boolean isSame(GesuchstellerAdresse otherAdr) {
		if (this == otherAdr) {
			return true;
		}
		if (otherAdr == null || getClass() != otherAdr.getClass()) {
			return false;
		}


		return super.isSame(otherAdr) &&
			adresseTyp == otherAdr.getAdresseTyp() &&
			Objects.equals(getGueltigkeit(), otherAdr.getGueltigkeit());

	}

	@Transient
	public boolean isKorrespondenzAdresse(){
		return AdresseTyp.KORRESPONDENZADRESSE.equals(this.getAdresseTyp());
	}

	public GesuchstellerAdresse copyForMutation(GesuchstellerAdresse mutation, Gesuchsteller gesuchstellerMutation) {
		super.copyForMutation(mutation);
		mutation.setAdresseTyp(this.getAdresseTyp());
		mutation.setGesuchsteller(gesuchstellerMutation);
		mutation.setNichtInGemeinde(this.nichtInGemeinde);
		return mutation;
	}
}

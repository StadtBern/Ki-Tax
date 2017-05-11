package ch.dvbern.ebegu.entities;

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
	public boolean isKorrespondenzAdresse() {
		return AdresseTyp.KORRESPONDENZADRESSE.equals(this.getAdresseTyp());
	}

	@Nonnull
	private GesuchstellerAdresse copyForMutationOrErneuerung(@Nonnull GesuchstellerAdresse mutation) {
		mutation.setAdresseTyp(this.getAdresseTyp());
		mutation.setNichtInGemeinde(this.nichtInGemeinde);
		return mutation;
	}

	@Nonnull
	public GesuchstellerAdresse copyForMutation(@Nonnull GesuchstellerAdresse mutation) {
		super.copyForMutation(mutation);
		return copyForMutationOrErneuerung(mutation);
	}

	@Nonnull
	public GesuchstellerAdresse copyForErneuerung(@Nonnull GesuchstellerAdresse mutation) {
		super.copyForErneuerung(mutation);
		return copyForMutationOrErneuerung(mutation);
	}
}

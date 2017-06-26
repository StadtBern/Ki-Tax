package ch.dvbern.ebegu.entities;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

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


	@SuppressWarnings({"OverlyComplexBooleanExpression"})
	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!super.isSame(other)) {
			return false;
		}
		if (!(other instanceof GesuchstellerAdresse)) {
			return false;
		}
		final GesuchstellerAdresse otherAdr = (GesuchstellerAdresse) other;
		return getAdresseTyp() == otherAdr.getAdresseTyp() &&
			isNichtInGemeinde() == otherAdr.isNichtInGemeinde();

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

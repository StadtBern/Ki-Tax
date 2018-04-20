/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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

	@SuppressWarnings({ "OverlyComplexBooleanExpression" })
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
		return AdresseTyp.KORRESPONDENZADRESSE == this.getAdresseTyp();
	}

	@Transient
	public boolean isRechnungsAdresse() {
		return AdresseTyp.RECHNUNGSADRESSE == this.getAdresseTyp();
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

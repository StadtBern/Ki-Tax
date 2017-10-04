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

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ch.dvbern.ebegu.enums.Land;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.EbeguUtil;
import org.hibernate.envers.Audited;

/**
 * Entitaet zum Speichern von Adressen  in der Datenbank.
 */
@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Adresse extends AbstractDateRangedEntity {

	private static final long serialVersionUID = 4637260017314382780L;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nonnull
	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String strasse;

	@Size(max = Constants.DB_DEFAULT_SHORT_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_DEFAULT_SHORT_LENGTH)
	private String hausnummer;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String zusatzzeile;

	@Size(max = Constants.DB_DEFAULT_SHORT_LENGTH)
	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_SHORT_LENGTH)
	private String plz;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nonnull
	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String ort;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Land land = Land.CH;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String gemeinde;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String organisation;

	public Adresse() {
	}

	@Nonnull
	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(@Nonnull String strasse) {
		this.strasse = strasse;
	}

	@Nullable
	public String getHausnummer() {
		return hausnummer;
	}

	public void setHausnummer(@Nullable String hausnummer) {
		this.hausnummer = hausnummer;
	}

	@Nonnull
	public String getPlz() {
		return plz;
	}

	public void setPlz(@Nonnull String plz) {
		this.plz = plz;
	}

	@Nonnull
	public String getOrt() {
		return ort;
	}

	@Nullable
	public String getGemeinde() {
		return gemeinde;
	}

	public void setGemeinde(@Nullable String gemeinde) {
		this.gemeinde = gemeinde;
	}

	public void setOrt(@Nonnull String ort) {
		this.ort = ort;
	}

	@Nullable
	public String getZusatzzeile() {
		return zusatzzeile;
	}

	public void setZusatzzeile(@Nullable String zusatzzeile) {
		this.zusatzzeile = zusatzzeile;
	}

	public Land getLand() {
		return land;
	}

	public void setLand(Land land) {
		this.land = land;
	}

	@Nullable
	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(@Nullable String organisation) {
		this.organisation = organisation;
	}

	@SuppressWarnings("OverlyComplexMethod")
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
		if (!(other instanceof Adresse)) {
			return false;
		}
		final Adresse otherAdr = (Adresse) other;
		return Objects.equals(getStrasse(), otherAdr.getStrasse()) &&
			EbeguUtil.isSameOrNullStrings(getHausnummer(), otherAdr.getHausnummer()) &&
			EbeguUtil.isSameOrNullStrings(getZusatzzeile(), otherAdr.getZusatzzeile()) &&
			Objects.equals(getPlz(), otherAdr.getPlz()) &&
			Objects.equals(getOrt(), otherAdr.getOrt()) &&
			getLand() == otherAdr.getLand() &&
			Objects.equals(getGemeinde(), otherAdr.getGemeinde()) &&
			EbeguUtil.isSameOrNullStrings(getOrganisation(), otherAdr.getOrganisation());
	}

	public Adresse copyForMutation(Adresse mutation) {
		super.copyForMutation(mutation);
		copyForMutationOrErneuerung(mutation);
		return mutation;
	}

	public Adresse copyForErneuerung(Adresse mutation) {
		super.copyForErneuerung(mutation);
		copyForMutationOrErneuerung(mutation);
		return mutation;
	}

	private void copyForMutationOrErneuerung(Adresse mutation) {
		mutation.setStrasse(this.getStrasse());
		mutation.setHausnummer(this.getHausnummer());
		mutation.setZusatzzeile(this.getZusatzzeile());
		mutation.setPlz(this.getPlz());
		mutation.setOrt(this.getOrt());
		mutation.setLand(this.getLand());
		mutation.setGemeinde(this.getGemeinde());
		mutation.setOrganisation(this.getOrganisation());
	}
}

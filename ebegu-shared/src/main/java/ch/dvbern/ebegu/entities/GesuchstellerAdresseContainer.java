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
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.EbeguUtil;
import org.hibernate.envers.Audited;

/**
 * Container-Entity für die GesuchstellerAdressen
 */
@Audited
@Entity
public class GesuchstellerAdresseContainer extends AbstractEntity {

	private static final long serialVersionUID = -3084333639027795652L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuchstelleradresse_container_gesuchstellerContainer_id"))
	private GesuchstellerContainer gesuchstellerContainer;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuchstelleradresse_container_gesuchstellergs_id"))
	private GesuchstellerAdresse gesuchstellerAdresseGS;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuchstelleradresse_container_gesuchstellerja_id"))
	private GesuchstellerAdresse gesuchstellerAdresseJA;

	public GesuchstellerAdresseContainer() {
	}

	public GesuchstellerContainer getGesuchstellerContainer() {
		return gesuchstellerContainer;
	}

	public void setGesuchstellerContainer(GesuchstellerContainer gesuchstellerContainer) {
		this.gesuchstellerContainer = gesuchstellerContainer;
	}

	public GesuchstellerAdresse getGesuchstellerAdresseGS() {
		return gesuchstellerAdresseGS;
	}

	public void setGesuchstellerAdresseGS(GesuchstellerAdresse gesuchstellerAdresseGS) {
		this.gesuchstellerAdresseGS = gesuchstellerAdresseGS;
	}

	public GesuchstellerAdresse getGesuchstellerAdresseJA() {
		return gesuchstellerAdresseJA;
	}

	public void setGesuchstellerAdresseJA(GesuchstellerAdresse gesuchstellerAdresseJA) {
		this.gesuchstellerAdresseJA = gesuchstellerAdresseJA;
	}

	/**
	 * Fragt nach dem Wert der AdresseJA, welcher eigentlich der geltende Wert ist
	 */
	@Transient
	public boolean extractIsKorrespondenzAdresse() {
		return this.gesuchstellerAdresseJA != null && this.gesuchstellerAdresseJA.isKorrespondenzAdresse()
			|| this.gesuchstellerAdresseJA == null && this.gesuchstellerAdresseGS != null && this.gesuchstellerAdresseGS.isKorrespondenzAdresse();
	}

	/**
	 * Fragt nach dem Wert der AdresseJA, welcher eigentlich der geltende Wert ist
	 */
	@Transient
	public boolean extractIsRechnungsAdresse() {
		return this.gesuchstellerAdresseJA != null && this.gesuchstellerAdresseJA.isRechnungsAdresse()
			|| this.gesuchstellerAdresseJA == null && this.gesuchstellerAdresseGS != null && this.gesuchstellerAdresseGS.isRechnungsAdresse();
	}

	/**
	 * Extracts the value of nichtInGemeinde von gesuchstellerAdresseJA
	 */
	@Transient
	public boolean extractIsNichtInGemeinde() {
		return this.gesuchstellerAdresseJA != null && this.gesuchstellerAdresseJA.isNichtInGemeinde();
	}

	/**
	 * Extracts the Gueltigkeit von gesuchstellerAdresseJA
	 */
	public DateRange extractGueltigkeit() {
		return this.gesuchstellerAdresseJA != null ? this.gesuchstellerAdresseJA.getGueltigkeit() : null;
	}

	/**
	 * Extracts the AdresseTyp von gesuchstellerAdresseJA
	 */
	public AdresseTyp extractAdresseTyp() {
		return this.gesuchstellerAdresseJA != null ? this.gesuchstellerAdresseJA.getAdresseTyp() : null;
	}

	/**
	 * Extracts the Hausnummer von gesuchstellerAdresseJA
	 */
	public String extractHausnummer() {
		return this.gesuchstellerAdresseJA != null ? this.gesuchstellerAdresseJA.getHausnummer() : null;
	}

	/**
	 * Extracts the Strasse von gesuchstellerAdresseJA
	 */
	public String extractStrasse() {
		return this.gesuchstellerAdresseJA != null ? this.gesuchstellerAdresseJA.getStrasse() : null;
	}

	/**
	 * Extracts the Zusatzzeile von gesuchstellerAdresseJA
	 */
	public String extractZusatzzeile() {
		return this.gesuchstellerAdresseJA != null ? this.gesuchstellerAdresseJA.getZusatzzeile() : null;
	}

	/**
	 * Extracts the PLZ von gesuchstellerAdresseJA
	 */
	public String extractPlz() {
		return this.gesuchstellerAdresseJA != null ? this.gesuchstellerAdresseJA.getPlz() : null;
	}

	/**
	 * Extracts the Ort von gesuchstellerAdresseJA
	 */
	public String extractOrt() {
		return this.gesuchstellerAdresseJA != null ? this.gesuchstellerAdresseJA.getOrt() : null;
	}

	public String extractOrganisation() {
		return this.gesuchstellerAdresseJA != null ? this.gesuchstellerAdresseJA.getOrganisation() : null;
	}

	@Nonnull
	private GesuchstellerAdresseContainer copyForMutationOrErneuerung(@Nonnull GesuchstellerAdresseContainer mutation, @Nonnull GesuchstellerContainer gesuchstellerContainer) {
		mutation.setGesuchstellerContainer(gesuchstellerContainer);
		mutation.setGesuchstellerAdresseGS(null);
		return mutation;
	}

	@Nonnull
	public GesuchstellerAdresseContainer copyForMutation(@Nonnull GesuchstellerAdresseContainer mutation, @Nonnull GesuchstellerContainer gesuchstellerContainer) {
		super.copyForMutation(mutation);
		if (this.getGesuchstellerAdresseJA() != null) {
			mutation.setGesuchstellerAdresseJA(this.getGesuchstellerAdresseJA().copyForMutation(new GesuchstellerAdresse()));
		}
		return copyForMutationOrErneuerung(mutation, gesuchstellerContainer);
	}

	@Nonnull
	public GesuchstellerAdresseContainer copyForErneuerung(@Nonnull GesuchstellerAdresseContainer mutation, @Nonnull GesuchstellerContainer gesuchstellerContainer) {
		super.copyForErneuerung(mutation);
		if (this.getGesuchstellerAdresseJA() != null) {
			mutation.setGesuchstellerAdresseJA(this.getGesuchstellerAdresseJA().copyForErneuerung(new GesuchstellerAdresse()));
		}
		return copyForMutationOrErneuerung(mutation, gesuchstellerContainer);
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
		if (!(other instanceof GesuchstellerAdresseContainer)) {
			return false;
		}
		final GesuchstellerAdresseContainer otherAdresseContainer = (GesuchstellerAdresseContainer) other;
		return EbeguUtil.isSameObject(getGesuchstellerAdresseJA(), otherAdresseContainer.getGesuchstellerAdresseJA());
	}
}

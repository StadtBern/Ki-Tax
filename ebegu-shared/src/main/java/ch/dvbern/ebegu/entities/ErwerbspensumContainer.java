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
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.util.EbeguUtil;
import org.hibernate.envers.Audited;

/**
 * Container-Entity für das Erwerbspensum: Diese muss für die  Benutzertypen (GS, JA) einzeln geführt werden,
 * damit die Veränderungen / Korrekturen angezeigt werden können.
 */
@Audited
@Entity
public class ErwerbspensumContainer extends AbstractEntity {

	private static final long serialVersionUID = -3084333639027795652L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_erwerbspensum_container_gesuchstellerContainer_id"))
	private GesuchstellerContainer gesuchstellerContainer;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_erwerbspensum_container_erwerbspensumgs_id"))
	private Erwerbspensum erwerbspensumGS;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_erwerbspensum_container_erwerbspensumja_id"))
	private Erwerbspensum erwerbspensumJA;

	public ErwerbspensumContainer() {
	}

	public GesuchstellerContainer getGesuchsteller() {
		return gesuchstellerContainer;
	}

	public void setGesuchsteller(GesuchstellerContainer gesuchstellerContainer) {
		this.gesuchstellerContainer = gesuchstellerContainer;
	}

	@Nullable
	public Erwerbspensum getErwerbspensumGS() {
		return erwerbspensumGS;
	}

	public void setErwerbspensumGS(@Nullable Erwerbspensum erwerbspensumGS) {
		this.erwerbspensumGS = erwerbspensumGS;
	}

	@Nullable
	public Erwerbspensum getErwerbspensumJA() {
		return erwerbspensumJA;
	}

	public void setErwerbspensumJA(@Nullable Erwerbspensum erwerbspensumJA) {
		this.erwerbspensumJA = erwerbspensumJA;
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
		if (!(other instanceof ErwerbspensumContainer)) {
			return false;
		}
		final ErwerbspensumContainer otherErwerbspensumContainer = (ErwerbspensumContainer) other;
		return EbeguUtil.isSameObject(getErwerbspensumJA(), otherErwerbspensumContainer.getErwerbspensumJA());
	}

	public ErwerbspensumContainer copyForMutation(@Nonnull ErwerbspensumContainer mutation, @Nonnull GesuchstellerContainer gesuchstellerMutation) {
		super.copyForMutation(mutation);
		mutation.setGesuchsteller(gesuchstellerMutation);
		mutation.setErwerbspensumGS(null);
		mutation.setErwerbspensumJA(this.getErwerbspensumJA().copyForMutation(new Erwerbspensum()));
		return mutation;
	}
}

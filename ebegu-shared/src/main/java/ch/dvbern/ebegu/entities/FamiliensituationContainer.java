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

import ch.dvbern.ebegu.util.EbeguUtil;
import java.time.LocalDate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import ch.dvbern.ebegu.validationgroups.AntragCompleteValidationGroup;
import ch.dvbern.ebegu.validators.CheckFamiliensituationContainerComplete;
import org.hibernate.envers.Audited;

/**
 * Entitaet zum Speichern von FamiliensituationContainer in der Datenbank.
 */
@CheckFamiliensituationContainerComplete(groups = AntragCompleteValidationGroup.class)
@Audited
@Entity
public class FamiliensituationContainer extends AbstractEntity {

	private static final long serialVersionUID = 6696130722316500745L;

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_familiensituation_container_familiensituation_JA_id"))
	private Familiensituation familiensituationJA;

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_familiensituation_container_familiensituation_GS_id"))
	private Familiensituation familiensituationGS;

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_familiensituation_container_familiensituation_erstgesuch_id"))
	private Familiensituation familiensituationErstgesuch;

	public FamiliensituationContainer() {
	}

	@Nonnull
	public FamiliensituationContainer copyForMutation(@Nonnull FamiliensituationContainer mutation, boolean toCopyisMutation) {
		super.copyForMutation(mutation);
		mutation.setFamiliensituationGS(null);
		mutation.setFamiliensituationJA(getFamiliensituationJA().copyForMutation(new Familiensituation()));
		if (toCopyisMutation) {
			mutation.setFamiliensituationErstgesuch(this.getFamiliensituationErstgesuch().copyForMutation(new Familiensituation()));
		} else { // beim ErstGesuch holen wir direkt die normale Familiensituation
			mutation.setFamiliensituationErstgesuch(this.getFamiliensituationJA().copyForMutation(new Familiensituation()));
		}
		return mutation;
	}

	@Nonnull
	public FamiliensituationContainer copyForErneuerung(@Nonnull FamiliensituationContainer folgeEntity) {
		super.copyForErneuerung(folgeEntity);
		folgeEntity.setFamiliensituationGS(null);
		folgeEntity.setFamiliensituationJA(getFamiliensituationJA().copyForErneuerung(new Familiensituation()));
		return folgeEntity;
	}

	@Nullable
	public Familiensituation getFamiliensituationJA() {
		return familiensituationJA;
	}

	public void setFamiliensituationJA(@Nullable Familiensituation familiensituationJA) {
		this.familiensituationJA = familiensituationJA;
	}

	@Nullable
	public Familiensituation getFamiliensituationGS() {
		return familiensituationGS;
	}

	public void setFamiliensituationGS(@Nullable Familiensituation familiensituationGS) {
		this.familiensituationGS = familiensituationGS;
	}

	@Nullable
	public Familiensituation getFamiliensituationErstgesuch() {
		return familiensituationErstgesuch;
	}

	public void setFamiliensituationErstgesuch(@Nullable Familiensituation familiensituationErstgesuch) {
		this.familiensituationErstgesuch = familiensituationErstgesuch;
	}

	@Nullable
	public Familiensituation extractFamiliensituation() {
		return familiensituationJA;
	}

	@Nonnull
	public Familiensituation getFamiliensituationAm(LocalDate stichtag) {
		if (getFamiliensituationJA().getAenderungPer() == null || getFamiliensituationJA().getAenderungPer().isBefore(stichtag)) {
			return getFamiliensituationJA();
		} else {
			return getFamiliensituationErstgesuch();
		}
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
		if (!(other instanceof FamiliensituationContainer)) {
			return false;
		}
		final FamiliensituationContainer otherFamSitContainer = (FamiliensituationContainer) other;
		return EbeguUtil.isSameObject(getFamiliensituationJA(), otherFamSitContainer.getFamiliensituationJA());
	}
}

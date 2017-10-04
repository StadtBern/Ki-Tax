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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.util.EbeguUtil;
import ch.dvbern.ebegu.validationgroups.AntragCompleteValidationGroup;
import ch.dvbern.ebegu.validators.CheckFinanzielleSituationContainerComplete;
import org.hibernate.envers.Audited;

/**
 * Container-Entity für die Finanzielle Situation: Diese muss für jeden Benutzertyp (GS, JA, SV) einzeln geführt werden,
 * damit die Veränderungen / Korrekturen angezeigt werden können.
 */
@CheckFinanzielleSituationContainerComplete(groups = AntragCompleteValidationGroup.class)
@Audited
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = "gesuchsteller_container_id", name = "UK_finanzielle_situation_container_gesuchsteller")
)
public class FinanzielleSituationContainer extends AbstractEntity {

	private static final long serialVersionUID = -6504985266190035840L;

	@NotNull
	@OneToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_finanzielleSituationContainer_gesuchstellerContainer_id"), nullable = false)
	private GesuchstellerContainer gesuchstellerContainer;

	@NotNull
	@Column(nullable = false)
	private Integer jahr;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_finanzielleSituationContainer_finanzielleSituationGS_id"), nullable = true)
	private FinanzielleSituation finanzielleSituationGS;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_finanzielleSituationContainer_finanzielleSituationJA_id"), nullable = true)
	private FinanzielleSituation finanzielleSituationJA;

	public FinanzielleSituationContainer() {
	}

	public GesuchstellerContainer getGesuchsteller() {
		return gesuchstellerContainer;
	}

	public void setGesuchsteller(GesuchstellerContainer gesuchstellerContainer) {
		this.gesuchstellerContainer = gesuchstellerContainer;
	}

	public Integer getJahr() {
		return jahr;
	}

	public void setJahr(Integer jahr) {
		this.jahr = jahr;
	}

	public FinanzielleSituation getFinanzielleSituationGS() {
		return finanzielleSituationGS;
	}

	public void setFinanzielleSituationGS(FinanzielleSituation finanzielleSituationGS) {
		this.finanzielleSituationGS = finanzielleSituationGS;
	}

	public FinanzielleSituation getFinanzielleSituationJA() {
		return finanzielleSituationJA;
	}

	public void setFinanzielleSituationJA(FinanzielleSituation finanzielleSituationJA) {
		this.finanzielleSituationJA = finanzielleSituationJA;
	}

	public FinanzielleSituationContainer copyForMutation(FinanzielleSituationContainer mutation, @Nonnull GesuchstellerContainer gesuchstellerMutation) {
		super.copyForMutation(mutation);
		mutation.setGesuchsteller(gesuchstellerMutation);
		mutation.setJahr(this.getJahr());
		mutation.setFinanzielleSituationGS(null);
		mutation.setFinanzielleSituationJA(this.getFinanzielleSituationJA().copyForMutation(new FinanzielleSituation()));
		return mutation;
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
		if (!(other instanceof FinanzielleSituationContainer)) {
			return false;
		}
		final FinanzielleSituationContainer otherFinSitContainer = (FinanzielleSituationContainer) other;
		return Objects.equals(getJahr(), otherFinSitContainer.getJahr()) &&
			EbeguUtil.isSameObject(getFinanzielleSituationJA(), otherFinSitContainer.getFinanzielleSituationJA());
	}
}

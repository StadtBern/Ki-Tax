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

import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Diese Entitaet speichert den Status von jedem Schritt im Wizzard (left menu). Ausserdem eine Bemerkung fuer jeden
 * Schritt kann auch gespeichert werden.
 */
@Entity
@Audited
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"wizardStepName", "gesuch_id"}, name = "UK_wizardstep_gesuch_stepname")
)
public class WizardStep extends AbstractEntity {

	private static final long serialVersionUID = -9032284720578372570L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_wizardstep_gesuch_id"), nullable = false)
	private Gesuch gesuch;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private WizardStepName wizardStepName;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private WizardStepStatus wizardStepStatus = WizardStepStatus.UNBESUCHT;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String bemerkungen;

	@NotNull
	@Column(nullable = false)
	private Boolean verfuegbar = false;


	public Gesuch getGesuch() {
		return gesuch;
	}

	public void setGesuch(Gesuch gesuch) {
		this.gesuch = gesuch;
	}

	public WizardStepName getWizardStepName() {
		return wizardStepName;
	}

	public void setWizardStepName(WizardStepName stepName) {
		this.wizardStepName = stepName;
	}

	public WizardStepStatus getWizardStepStatus() {
		return wizardStepStatus;
	}

	public void setWizardStepStatus(WizardStepStatus stepStatus) {
		this.wizardStepStatus = stepStatus;
	}

	@Nullable
	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(@Nullable String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}

	public Boolean getVerfuegbar() {
		return verfuegbar;
	}

	public void setVerfuegbar(Boolean verfuegbar) {
		this.verfuegbar = verfuegbar;
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
		if (!(other instanceof WizardStep)) {
			return false;
		}
		final WizardStep otherWizardStep = (WizardStep) other;
		return Objects.equals(getWizardStepName(), otherWizardStep.getWizardStepName()) &&
			Objects.equals(getWizardStepStatus(), otherWizardStep.getWizardStepStatus()) &&
			Objects.equals(getBemerkungen(), otherWizardStep.getBemerkungen()) &&
			Objects.equals(getVerfuegbar(), otherWizardStep.getVerfuegbar());
	}
}

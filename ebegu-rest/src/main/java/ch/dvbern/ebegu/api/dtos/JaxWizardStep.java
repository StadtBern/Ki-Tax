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

package ch.dvbern.ebegu.api.dtos;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import ch.dvbern.ebegu.util.Constants;

/**
 * DTO fuer JaxWizardStep
 */
@XmlRootElement(name = "wzardStep")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxWizardStep extends JaxAbstractDTO {

	private static final long serialVersionUID = -1217019901364138697L;

	@NotNull
	private String gesuchId; // we don't need the complete gesuch but just its Id

	@NotNull
	private WizardStepName wizardStepName;

	@NotNull
	private WizardStepStatus wizardStepStatus;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	private String bemerkungen;

	@NotNull
	private boolean verfuegbar;



	public String getGesuchId() {
		return gesuchId;
	}

	public void setGesuchId(String gesuchId) {
		this.gesuchId = gesuchId;
	}

	public WizardStepName getWizardStepName() {
		return wizardStepName;
	}

	public void setWizardStepName(WizardStepName wizardStepName) {
		this.wizardStepName = wizardStepName;
	}

	public WizardStepStatus getWizardStepStatus() {
		return wizardStepStatus;
	}

	public void setWizardStepStatus(WizardStepStatus wizardStepStatus) {
		this.wizardStepStatus = wizardStepStatus;
	}

	@Nullable
	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(@Nullable String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}

	public boolean isVerfuegbar() {
		return verfuegbar;
	}

	public void setVerfuegbar(boolean verfuegbar) {
		this.verfuegbar = verfuegbar;
	}
}

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

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer die FinanzielleSituation, so verhindern wir, dass das ganze gesuch mitgegeben werden muss wenn wir berechnen wollen
 */
@XmlRootElement(name = "jaxFinSitModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFinanzModel {

	@Valid
	private JaxFinanzielleSituationContainer finanzielleSituationContainerGS1;

	@Valid
	private JaxFinanzielleSituationContainer finanzielleSituationContainerGS2;

	private JaxEinkommensverschlechterungContainer einkommensverschlechterungContainerGS1;

	private JaxEinkommensverschlechterungContainer einkommensverschlechterungContainerGS2;

	private JaxEinkommensverschlechterungInfoContainer einkommensverschlechterungInfoContainer;

	private boolean gemeinsameSteuererklaerung;

	public JaxFinanzielleSituationContainer getFinanzielleSituationContainerGS1() {
		return finanzielleSituationContainerGS1;
	}

	public void setFinanzielleSituationContainerGS1(JaxFinanzielleSituationContainer finanzielleSituationContainerGS1) {
		this.finanzielleSituationContainerGS1 = finanzielleSituationContainerGS1;
	}

	public JaxFinanzielleSituationContainer getFinanzielleSituationContainerGS2() {
		return finanzielleSituationContainerGS2;
	}

	public void setFinanzielleSituationContainerGS2(JaxFinanzielleSituationContainer finanzielleSituationContainerGS2) {
		this.finanzielleSituationContainerGS2 = finanzielleSituationContainerGS2;
	}

	public boolean isGemeinsameSteuererklaerung() {
		return gemeinsameSteuererklaerung;
	}

	public void setGemeinsameSteuererklaerung(boolean gemeinsameSteuererklaerung) {
		this.gemeinsameSteuererklaerung = gemeinsameSteuererklaerung;
	}

	public JaxEinkommensverschlechterungContainer getEinkommensverschlechterungContainerGS1() {
		return einkommensverschlechterungContainerGS1;
	}

	public void setEinkommensverschlechterungContainerGS1(JaxEinkommensverschlechterungContainer einkommensverschlechterungContainerGS1) {
		this.einkommensverschlechterungContainerGS1 = einkommensverschlechterungContainerGS1;
	}

	public JaxEinkommensverschlechterungContainer getEinkommensverschlechterungContainerGS2() {
		return einkommensverschlechterungContainerGS2;
	}

	public void setEinkommensverschlechterungContainerGS2(JaxEinkommensverschlechterungContainer einkommensverschlechterungContainerGS2) {
		this.einkommensverschlechterungContainerGS2 = einkommensverschlechterungContainerGS2;
	}

	public JaxEinkommensverschlechterungInfoContainer getEinkommensverschlechterungInfoContainer() {
		return einkommensverschlechterungInfoContainer;
	}

	public void setEinkommensverschlechterungInfoContainer(JaxEinkommensverschlechterungInfoContainer einkommensverschlechterungInfoContainer) {
		this.einkommensverschlechterungInfoContainer = einkommensverschlechterungInfoContainer;
	}
}


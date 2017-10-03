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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Stammdaten der GesuchstellerContainer (kennt adresse)
 */
@XmlRootElement(name = "gesuchsteller")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxGesuchstellerContainer extends JaxAbstractDTO {

	private static final long serialVersionUID = -1217011301387130097L;

	@Valid
	private JaxGesuchsteller gesuchstellerGS;

	@Valid
	private JaxGesuchsteller gesuchstellerJA;

	//Adressen
	@NotNull
	@Valid
	private List<JaxAdresseContainer> adressen;

	@Valid
	private JaxAdresseContainer alternativeAdresse;

	@Valid
	private JaxFinanzielleSituationContainer finanzielleSituationContainer;

	@Valid
	@Nullable
	private JaxEinkommensverschlechterungContainer einkommensverschlechterungContainer;

	private Collection<JaxErwerbspensumContainer> erwerbspensenContainers = new LinkedHashSet<>();


	public JaxGesuchsteller getGesuchstellerGS() {
		return gesuchstellerGS;
	}

	public void setGesuchstellerGS(JaxGesuchsteller gesuchstellerGS) {
		this.gesuchstellerGS = gesuchstellerGS;
	}

	public JaxGesuchsteller getGesuchstellerJA() {
		return gesuchstellerJA;
	}

	public void setGesuchstellerJA(JaxGesuchsteller gesuchstellerJA) {
		this.gesuchstellerJA = gesuchstellerJA;
	}

	public List<JaxAdresseContainer> getAdressen() {
		return adressen;
	}

	public void setAdressen(final List<JaxAdresseContainer> adressen) {
		this.adressen = adressen;
	}

	public JaxAdresseContainer getAlternativeAdresse() {
		return alternativeAdresse;
	}

	public void setAlternativeAdresse(final JaxAdresseContainer alternativeAdresse) {
		this.alternativeAdresse = alternativeAdresse;
	}

	public JaxFinanzielleSituationContainer getFinanzielleSituationContainer() {
		return finanzielleSituationContainer;
	}

	public void setFinanzielleSituationContainer(final JaxFinanzielleSituationContainer finanzielleSituationContainer) {
		this.finanzielleSituationContainer = finanzielleSituationContainer;
	}

	public Collection<JaxErwerbspensumContainer> getErwerbspensenContainers() {
		return erwerbspensenContainers;
	}

	public void setErwerbspensenContainers(final Collection<JaxErwerbspensumContainer> erwerbspensenContainers) {
		this.erwerbspensenContainers = erwerbspensenContainers;
	}

	@Nullable
	public JaxEinkommensverschlechterungContainer getEinkommensverschlechterungContainer() {
		return einkommensverschlechterungContainer;
	}

	public void setEinkommensverschlechterungContainer(@Nullable JaxEinkommensverschlechterungContainer einkommensverschlechterungContainer) {
		this.einkommensverschlechterungContainer = einkommensverschlechterungContainer;
	}

	public void addAdresse(JaxAdresseContainer adresse) {
		if (adressen == null) {
			adressen = new ArrayList<>();
		}
		adressen.add(adresse);
	}
}

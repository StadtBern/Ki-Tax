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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer FamiliensituationenContainer
 */
@XmlRootElement(name = "familiensituationContainer")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFamiliensituationContainer extends JaxAbstractDTO {

	private static final long serialVersionUID = 5217224327005193232L;

	private JaxFamiliensituation familiensituationJA;

	private JaxFamiliensituation familiensituationGS;

	private JaxFamiliensituation familiensituationErstgesuch;

	public JaxFamiliensituation getFamiliensituationJA() {
		return familiensituationJA;
	}

	public void setFamiliensituationJA(JaxFamiliensituation familiensituationJA) {
		this.familiensituationJA = familiensituationJA;
	}

	public JaxFamiliensituation getFamiliensituationGS() {
		return familiensituationGS;
	}

	public void setFamiliensituationGS(JaxFamiliensituation familiensituationGS) {
		this.familiensituationGS = familiensituationGS;
	}

	public JaxFamiliensituation getFamiliensituationErstgesuch() {
		return familiensituationErstgesuch;
	}

	public void setFamiliensituationErstgesuch(JaxFamiliensituation familiensituationErstgesuch) {
		this.familiensituationErstgesuch = familiensituationErstgesuch;
	}
}

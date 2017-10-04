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
 * Created by imanol on 17.03.16.
 * DTO fuer Adresse
 */
@XmlRootElement(name = "adresse")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAdresseContainer extends JaxAbstractDTO {

	private static final long serialVersionUID = -7917011641387130097L;

	@Valid
	private JaxAdresse adresseJA;

	@Valid
	private JaxAdresse adresseGS;


	public JaxAdresse getAdresseJA() {
		return adresseJA;
	}

	public void setAdresseJA(JaxAdresse adresseJA) {
		this.adresseJA = adresseJA;
	}

	public JaxAdresse getAdresseGS() {
		return adresseGS;
	}

	public void setAdresseGS(JaxAdresse adresseGS) {
		this.adresseGS = adresseGS;
	}
}

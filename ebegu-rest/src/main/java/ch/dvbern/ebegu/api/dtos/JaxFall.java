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

import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Faelle
 */
@XmlRootElement(name = "fall")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFall extends JaxAbstractDTO {

	private static final long serialVersionUID = -1297019901664130597L;

	private long fallNummer;

	private JaxAuthLoginElement verantwortlicher;

	@Min(1)
	private Integer nextNumberKind = 1;

	private JaxAuthLoginElement besitzer; //


	public long getFallNummer() {
		return fallNummer;
	}

	public void setFallNummer(long fallNummer) {
		this.fallNummer = fallNummer;
	}

	public JaxAuthLoginElement getVerantwortlicher() {
		return verantwortlicher;
	}

	public void setVerantwortlicher(JaxAuthLoginElement verantwortlicher) {
		this.verantwortlicher = verantwortlicher;
	}

	public Integer getNextNumberKind() {
		return nextNumberKind;
	}

	public void setNextNumberKind(Integer nextNumberKind) {
		this.nextNumberKind = nextNumberKind;
	}

	public void setBesitzer(JaxAuthLoginElement besitzer) {
		this.besitzer = besitzer;
	}

	public JaxAuthLoginElement getBesitzer() {
		return besitzer;
	}
}

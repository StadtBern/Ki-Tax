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

package ch.dvbern.ebegu.dto.personensuche;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO für Resultate aus dem EWK
 */
@XmlRootElement(name = "ewkResultat")
@XmlAccessorType(XmlAccessType.FIELD)
public class EWKResultat implements Serializable {

	private static final long serialVersionUID = 3663123555068820247L;

	private int maxResultate;

	private int anzahlResultate;

	private List<EWKPerson> personen = new ArrayList<>();

	public EWKResultat() {
	}

	public int getMaxResultate() {
		return maxResultate;
	}

	public void setMaxResultate(int maxResultate) {
		this.maxResultate = maxResultate;
	}

	public int getAnzahlResultate() {
		return anzahlResultate;
	}

	public void setAnzahlResultate(int anzahlResultate) {
		this.anzahlResultate = anzahlResultate;
	}

	public List<EWKPerson> getPersonen() {
		return personen;
	}

	public void setPersonen(List<EWKPerson> personen) {
		this.personen = personen;
	}
}

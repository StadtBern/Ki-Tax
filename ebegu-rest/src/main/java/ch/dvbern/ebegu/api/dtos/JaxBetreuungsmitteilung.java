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
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Stammdaten der Betreuungsmitteilung
 */
@XmlRootElement(name = "betreuungsmitteilung")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxBetreuungsmitteilung extends JaxMitteilung {

	private static final long serialVersionUID = -1297781341675137397L;

	@NotNull
	private List<JaxBetreuungsmitteilungPensum> betreuungspensen = new ArrayList<>();

	@NotNull
	private Boolean applied = false;

	public List<JaxBetreuungsmitteilungPensum> getBetreuungspensen() {
		return betreuungspensen;
	}

	public void setBetreuungspensen(List<JaxBetreuungsmitteilungPensum> betreuungspensen) {
		this.betreuungspensen = betreuungspensen;
	}

	public Boolean getApplied() {
		return applied;
	}

	public void setApplied(Boolean applied) {
		this.applied = applied;
	}
}

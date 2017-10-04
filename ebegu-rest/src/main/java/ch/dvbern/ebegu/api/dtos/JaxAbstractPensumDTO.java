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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Superklasse fuer ein Pensum
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAbstractPensumDTO extends JaxAbstractDateRangedDTO {

	private static final long serialVersionUID = -7598194821332548948L;

	@Min(0)
	@Max(100)
	@NotNull
	private Integer pensum;

	public Integer getPensum() {
		return pensum;
	}

	public void setPensum(Integer pensum) {
		this.pensum = pensum;
	}
}

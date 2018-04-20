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

package ch.dvbern.ebegu.dto.suchfilter.smarttable;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Leider generiert SmartTable  ein verschachteltes JSON Objekt fuer die Suchpredicates. Daher muessen wir das hier nachbauen
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class AntragSearchDTO implements Serializable {

	private static final long serialVersionUID = 4561877549058241575L;
	private AntragPredicateObjectDTO predicateObject;

	public AntragSearchDTO() {
		this.predicateObject = new AntragPredicateObjectDTO();
	}

	public AntragPredicateObjectDTO getPredicateObject() {
		return predicateObject;
	}

	public void setPredicateObject(AntragPredicateObjectDTO predicateObject) {
		this.predicateObject = predicateObject;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("predicateObject", predicateObject)
			.toString();
	}
}

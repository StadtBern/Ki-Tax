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
 * * Klasse zum deserialisieren/serialisieren des Sortfilters in Java  (smarttable)
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class SortDTO implements Serializable {

	private static final long serialVersionUID = -742377991134129869L;
	/**
	 * definiert den Namen des Feldes nach dem sortiert werden soll. Allenfalls koennen wir hier auch ein Enum machen
	 */
	private String predicate;

	private Boolean reverse = Boolean.FALSE;

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("predicate", predicate)
			.toString();
	}

	public Boolean getReverse() {
		return reverse;
	}

	public void setReverse(Boolean reverse) {
		this.reverse = reverse;
	}
}

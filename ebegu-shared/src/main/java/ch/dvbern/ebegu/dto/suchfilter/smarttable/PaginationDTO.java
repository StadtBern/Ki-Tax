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

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * Paginationobjekt von Smarttable, beinhaltet den startindex, die anzahl resultate pro page (number) und das total
 * gefundener resultate
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class PaginationDTO implements Serializable {

	private static final long serialVersionUID = 7555063492094787968L;
	private Integer number;
	private Integer start;
	private Long totalItemCount;

	private Integer numberOfPages;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Long getTotalItemCount() {
		return totalItemCount;
	}

	public void setTotalItemCount(Long totalItemCount) {
		this.totalItemCount = totalItemCount;
	}

	public Integer getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("number", number)
			.append("start", start)
			.append("totalItemCount", totalItemCount)
			.toString();
	}
}

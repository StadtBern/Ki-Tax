/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Aggregat Klasse zum deserialisieren/serialisieren des gesamten SmartTable-Filterobjekts
 */
@XmlRootElement(name = "benutzerSucheFilter")
@XmlAccessorType(XmlAccessType.FIELD)
public class BenutzerTableFilterDTO implements Serializable {

	private static final long serialVersionUID = 404959569485575365L;
	private PaginationDTO pagination;

	private BenutzerSearchDTO search;

	private SortDTO sort;

	public PaginationDTO getPagination() {
		return pagination;
	}

	public void setPagination(PaginationDTO pagination) {
		this.pagination = pagination;
	}

	public BenutzerSearchDTO getSearch() {
		return search;
	}

	public void setSearch(BenutzerSearchDTO search) {
		this.search = search;
	}

	public SortDTO getSort() {
		return sort;
	}

	public void setSort(SortDTO sort) {
		this.sort = sort;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("pagination", pagination)
			.append("search", search)
			.append("sort", sort)
			.toString();
	}
}

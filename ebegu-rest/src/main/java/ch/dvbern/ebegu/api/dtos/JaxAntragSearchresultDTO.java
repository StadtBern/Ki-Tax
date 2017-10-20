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

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import ch.dvbern.ebegu.dto.JaxAntragDTO;
import ch.dvbern.ebegu.dto.suchfilter.smarttable.PaginationDTO;

/**
 * Bei der Table mit Pagination muss die Totalanzahl Resultate vom Server mit zuruckgegeben werden im Resultat
 */
@XmlRootElement(name = "pendenz")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAntragSearchresultDTO implements Serializable {

	private static final long serialVersionUID = 3939072050781289382L;
	private List<JaxAntragDTO> antragDTOs;
	private PaginationDTO paginationDTO;

	public List<JaxAntragDTO> getAntragDTOs() {
		return antragDTOs;
	}

	public void setAntragDTOs(List<JaxAntragDTO> antragDTOs) {
		this.antragDTOs = antragDTOs;
	}

	public PaginationDTO getPaginationDTO() {
		return paginationDTO;
	}

	public void setPaginationDTO(PaginationDTO paginationDTO) {
		this.paginationDTO = paginationDTO;
	}
}

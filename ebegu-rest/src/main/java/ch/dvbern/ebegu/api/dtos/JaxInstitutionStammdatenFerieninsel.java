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

package ch.dvbern.ebegu.api.dtos;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * DTO fuer InstitutionStammdatenFerieninsel
 */
@XmlRootElement(name = "institutionStammdatenFerieninsel")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxInstitutionStammdatenFerieninsel extends JaxAbstractDTO {

	private static final long serialVersionUID = 6958218086966611467L;

	@Nullable
	@Size(max = DB_DEFAULT_MAX_LENGTH)
	private String ausweichstandortSommerferien;

	@Nullable
	@Size(max = DB_DEFAULT_MAX_LENGTH)
	private String ausweichstandortHerbstferien;

	@Nullable
	@Size(max = DB_DEFAULT_MAX_LENGTH)
	private String ausweichstandortSportferien;

	@Nullable
	@Size(max = DB_DEFAULT_MAX_LENGTH)
	private String ausweichstandortFruehlingsferien;

	@Nullable
	public String getAusweichstandortSommerferien() {
		return ausweichstandortSommerferien;
	}

	public void setAusweichstandortSommerferien(@Nullable String ausweichstandortSommerferien) {
		this.ausweichstandortSommerferien = ausweichstandortSommerferien;
	}

	@Nullable
	public String getAusweichstandortHerbstferien() {
		return ausweichstandortHerbstferien;
	}

	public void setAusweichstandortHerbstferien(@Nullable String ausweichstandortHerbstferien) {
		this.ausweichstandortHerbstferien = ausweichstandortHerbstferien;
	}

	@Nullable
	public String getAusweichstandortSportferien() {
		return ausweichstandortSportferien;
	}

	public void setAusweichstandortSportferien(@Nullable String ausweichstandortSportferien) {
		this.ausweichstandortSportferien = ausweichstandortSportferien;
	}

	@Nullable
	public String getAusweichstandortFruehlingsferien() {
		return ausweichstandortFruehlingsferien;
	}

	public void setAusweichstandortFruehlingsferien(@Nullable String ausweichstandortFruehlingsferien) {
		this.ausweichstandortFruehlingsferien = ausweichstandortFruehlingsferien;
	}
}

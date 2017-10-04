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

package ch.dvbern.ebegu.reporting.kanton.mitarbeiterinnen;

import java.math.BigDecimal;

/**
 * DTO fuer die statistik der MitarbeiterInnen
 */
public class MitarbeiterinnenDataRow {

	private String name;
	private String vorname;
	private BigDecimal verantwortlicheGesuche;
	private BigDecimal verfuegungenAusgestellt;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public BigDecimal getVerantwortlicheGesuche() {
		return verantwortlicheGesuche;
	}

	public void setVerantwortlicheGesuche(BigDecimal verantwortlicheGesuche) {
		this.verantwortlicheGesuche = verantwortlicheGesuche;
	}

	public BigDecimal getVerfuegungenAusgestellt() {
		return verfuegungenAusgestellt;
	}

	public void setVerfuegungenAusgestellt(BigDecimal verfuegungenAusgestellt) {
		this.verfuegungenAusgestellt = verfuegungenAusgestellt;
	}
}

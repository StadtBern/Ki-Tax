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

package ch.dvbern.ebegu.reporting.gesuchstichtag;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class GesuchStichtagDataRow {

	private String bgNummer;
	private Integer gesuchLaufNr;
	private String institution;
	private String betreuungsTyp;
	private String periode;
	private Integer nichtFreigegeben;
	private Integer mahnungen;
	private Integer beschwerde;

	public GesuchStichtagDataRow(String bgNummer, Integer gesuchLaufNr, String institution, String betreuungsTyp, String periode, Integer nichtFreigegeben, Integer mahnungen, Integer beschwerde) {
		this.bgNummer = bgNummer;
		this.gesuchLaufNr = gesuchLaufNr;
		this.institution = institution;
		this.betreuungsTyp = betreuungsTyp;
		this.periode = periode;
		this.nichtFreigegeben = nichtFreigegeben;
		this.mahnungen = mahnungen;
		this.beschwerde = beschwerde;
	}

	@SuppressFBWarnings("NM_CONFUSING")
	public String getBgNummer() {
		return bgNummer;
	}

	public void setBgNummer(String bgNummer) {
		this.bgNummer = bgNummer;
	}

	public Integer getGesuchLaufNr() {
		return gesuchLaufNr;
	}

	public void setGesuchLaufnummer(Integer gesuchLaufNr) {
		this.gesuchLaufNr = gesuchLaufNr;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getBetreuungsTyp() {
		return betreuungsTyp;
	}

	public void setBetreuungsTyp(String betreuungsTyp) {
		this.betreuungsTyp = betreuungsTyp;
	}

	public String getPeriode() {
		return periode;
	}

	public void setPeriode(String periode) {
		this.periode = periode;
	}

	public Integer getNichtFreigegeben() {
		return nichtFreigegeben;
	}

	public void setNichtFreigegeben(Integer nichtFreigegeben) {
		this.nichtFreigegeben = nichtFreigegeben;
	}

	public Integer getMahnungen() {
		return mahnungen;
	}

	public void setMahnungen(Integer mahnungen) {
		this.mahnungen = mahnungen;
	}

	public Integer getBeschwerde() {
		return beschwerde;
	}

	public void setBeschwerde(Integer beschwerde) {
		this.beschwerde = beschwerde;
	}
}

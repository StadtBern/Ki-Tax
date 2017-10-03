
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

package ch.dvbern.ebegu.dto.personensuche;

import java.io.Serializable;
import java.time.LocalDate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.lib.date.converters.LocalDateXMLConverter;


/**
 * DTO für Adressen aus dem EWK
 */
@XmlRootElement(name = "ewkAdresse")
@XmlAccessorType(XmlAccessType.FIELD)
public class EWKAdresse implements Serializable {

	private static final long serialVersionUID = -2070439419700535368L;

	protected String adresstyp;

    protected String adresstypTxt;

	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
    protected LocalDate gueltigVon;

	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
    protected LocalDate gueltigBis;

    protected String coName;

    protected String postfach;

    protected String bfSGemeinde;

    protected String strasse;

    protected String hausnummer;

    protected String postleitzahl;

    protected String ort;

    protected String kanton;

    protected String land;


	public EWKAdresse() {
	}

	public String getAdresstyp() {
		return adresstyp;
	}

	public void setAdresstyp(String adresstyp) {
		this.adresstyp = adresstyp;
	}

	public String getAdresstypTxt() {
		return adresstypTxt;
	}

	public void setAdresstypTxt(String adresstypTxt) {
		this.adresstypTxt = adresstypTxt;
	}

	public LocalDate getGueltigVon() {
		return gueltigVon;
	}

	public void setGueltigVon(LocalDate gueltigVon) {
		this.gueltigVon = gueltigVon;
	}

	public LocalDate getGueltigBis() {
		return gueltigBis;
	}

	public void setGueltigBis(LocalDate gueltigBis) {
		this.gueltigBis = gueltigBis;
	}

	public String getCoName() {
		return coName;
	}

	public void setCoName(String coName) {
		this.coName = coName;
	}

	public String getPostfach() {
		return postfach;
	}

	public void setPostfach(String postfach) {
		this.postfach = postfach;
	}

	public String getBfSGemeinde() {
		return bfSGemeinde;
	}

	public void setBfSGemeinde(String bfSGemeinde) {
		this.bfSGemeinde = bfSGemeinde;
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getHausnummer() {
		return hausnummer;
	}

	public void setHausnummer(String hausnummer) {
		this.hausnummer = hausnummer;
	}

	public String getPostleitzahl() {
		return postleitzahl;
	}

	public void setPostleitzahl(String postleitzahl) {
		this.postleitzahl = postleitzahl;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getKanton() {
		return kanton;
	}

	public void setKanton(String kanton) {
		this.kanton = kanton;
	}

	public String getLand() {
		return land;
	}

	public void setLand(String land) {
		this.land = land;
	}
}

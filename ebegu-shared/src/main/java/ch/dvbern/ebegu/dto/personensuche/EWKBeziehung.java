
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
@XmlRootElement(name = "ewkBeziehung")
@XmlAccessorType(XmlAccessType.FIELD)
public class EWKBeziehung implements Serializable {

	private static final long serialVersionUID = 8602528362565753981L;

	protected String beziehungstyp;

	protected String beziehungstypTxt;

	protected String personID;

	protected String nachname;

	protected String ledigname;

	protected String vorname;

	protected String rufname;

	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	protected LocalDate geburtsdatum;

	protected EWKAdresse adresse;

	public EWKBeziehung() {
	}

	public String getBeziehungstyp() {
		return beziehungstyp;
	}

	public void setBeziehungstyp(String beziehungstyp) {
		this.beziehungstyp = beziehungstyp;
	}

	public String getBeziehungstypTxt() {
		return beziehungstypTxt;
	}

	public void setBeziehungstypTxt(String beziehungstypTxt) {
		this.beziehungstypTxt = beziehungstypTxt;
	}

	public String getPersonID() {
		return personID;
	}

	public void setPersonID(String personID) {
		this.personID = personID;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getLedigname() {
		return ledigname;
	}

	public void setLedigname(String ledigname) {
		this.ledigname = ledigname;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getRufname() {
		return rufname;
	}

	public void setRufname(String rufname) {
		this.rufname = rufname;
	}

	public LocalDate getGeburtsdatum() {
		return geburtsdatum;
	}

	public void setGeburtsdatum(LocalDate geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}

	public EWKAdresse getAdresse() {
		return adresse;
	}

	public void setAdresse(EWKAdresse adresse) {
		this.adresse = adresse;
	}
}

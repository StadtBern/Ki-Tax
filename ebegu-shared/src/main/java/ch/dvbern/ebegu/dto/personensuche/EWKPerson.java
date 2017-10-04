
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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.lib.date.converters.LocalDateXMLConverter;


/**
 * DTO für Personen aus dem EWK
 */
@XmlRootElement(name = "ewkPerson")
@XmlAccessorType(XmlAccessType.FIELD)
public class EWKPerson implements Serializable {

	private static final long serialVersionUID = -3920969107353572301L;

	private String personID;

	private List<EWKEinwohnercode> einwohnercodes = new ArrayList<>();

	private String nachname;

	private String ledigname;

	private String vorname;

	private String rufname;

	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate geburtsdatum;

	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate zuzugsdatum;

	private String nationalitaet;

	private String zivilstand;

	private String zivilstandTxt;

	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate zivilstandsdatum;

	private Geschlecht geschlecht;

	private String bewilligungsart;

	private String bewilligungsartTxt;

	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate bewilligungBis;

	private List<EWKAdresse> adressen = new ArrayList<>();

    private List<EWKBeziehung> beziehungen = new ArrayList<>();

	public EWKPerson() {
	}

	public String getPersonID() {
		return personID;
	}

	public void setPersonID(String personID) {
		this.personID = personID;
	}

	public List<EWKEinwohnercode> getEinwohnercodes() {
		return einwohnercodes;
	}

	public void setEinwohnercodes(List<EWKEinwohnercode> einwohnercodes) {
		this.einwohnercodes = einwohnercodes;
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

	public LocalDate getZuzugsdatum() {
		return zuzugsdatum;
	}

	public void setZuzugsdatum(LocalDate zuzugsdatum) {
		this.zuzugsdatum = zuzugsdatum;
	}

	public String getNationalitaet() {
		return nationalitaet;
	}

	public void setNationalitaet(String nationalitaet) {
		this.nationalitaet = nationalitaet;
	}

	public String getZivilstand() {
		return zivilstand;
	}

	public void setZivilstand(String zivilstand) {
		this.zivilstand = zivilstand;
	}

	public String getZivilstandTxt() {
		return zivilstandTxt;
	}

	public void setZivilstandTxt(String zivilstandTxt) {
		this.zivilstandTxt = zivilstandTxt;
	}

	public LocalDate getZivilstandsdatum() {
		return zivilstandsdatum;
	}

	public void setZivilstandsdatum(LocalDate zivilstandsdatum) {
		this.zivilstandsdatum = zivilstandsdatum;
	}

	public Geschlecht getGeschlecht() {
		return geschlecht;
	}

	public void setGeschlecht(Geschlecht geschlecht) {
		this.geschlecht = geschlecht;
	}

	public String getBewilligungsart() {
		return bewilligungsart;
	}

	public void setBewilligungsart(String bewilligungsart) {
		this.bewilligungsart = bewilligungsart;
	}

	public String getBewilligungsartTxt() {
		return bewilligungsartTxt;
	}

	public void setBewilligungsartTxt(String bewilligungsartTxt) {
		this.bewilligungsartTxt = bewilligungsartTxt;
	}

	public LocalDate getBewilligungBis() {
		return bewilligungBis;
	}

	public void setBewilligungBis(LocalDate bewilligungBis) {
		this.bewilligungBis = bewilligungBis;
	}

	public List<EWKAdresse> getAdressen() {
		return adressen;
	}

	public void setAdressen(List<EWKAdresse> adressen) {
		this.adressen = adressen;
	}

	public List<EWKBeziehung> getBeziehungen() {
		return beziehungen;
	}

	public void setBeziehungen(List<EWKBeziehung> beziehungen) {
		this.beziehungen = beziehungen;
	}
}

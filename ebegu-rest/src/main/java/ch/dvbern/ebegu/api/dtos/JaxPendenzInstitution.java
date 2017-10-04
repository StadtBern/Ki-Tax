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

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.lib.date.converters.LocalDateXMLConverter;

/**
 * DTO fuer Pendenzenliste der Institutionen. Es kann pro Antrag mehrere solcher Pendenzen geben. Naemlich fuer jede
 * Betreuung
 */
@XmlRootElement(name = "pendenz")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxPendenzInstitution {

	private static final long serialVersionUID = -1277026654764135397L;

	@NotNull
	private String betreuungsNummer;

	@NotNull
	private String betreuungsId;

	@NotNull
	private String gesuchId;

	@NotNull
	private String kindId;

	@NotNull
	private String name;

	@NotNull
	private String vorname;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate geburtsdatum;

	@NotNull
	private String typ;

	@NotNull
	private JaxGesuchsperiode gesuchsperiode;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate eingangsdatum = null;

	@NotNull
	private BetreuungsangebotTyp betreuungsangebotTyp;

	@NotNull
	private JaxInstitution institution;


	public String getBetreuungsNummer() {
		return betreuungsNummer;
	}

	public void setBetreuungsNummer(String betreuungsNummer) {
		this.betreuungsNummer = betreuungsNummer;
	}

	public String getBetreuungsId() {
		return betreuungsId;
	}

	public void setBetreuungsId(String betreuungsId) {
		this.betreuungsId = betreuungsId;
	}

	public String getGesuchId() {
		return gesuchId;
	}

	public void setGesuchId(String gesuchId) {
		this.gesuchId = gesuchId;
	}

	public String getKindId() {
		return kindId;
	}

	public void setKindId(String kindId) {
		this.kindId = kindId;
	}

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

	public LocalDate getGeburtsdatum() {
		return geburtsdatum;
	}

	public void setGeburtsdatum(LocalDate geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	public JaxGesuchsperiode getGesuchsperiode() {
		return gesuchsperiode;
	}

	public void setGesuchsperiode(JaxGesuchsperiode gesuchsperiode) {
		this.gesuchsperiode = gesuchsperiode;
	}

	public LocalDate getEingangsdatum() {
		return eingangsdatum;
	}

	public void setEingangsdatum(LocalDate eingangsdatum) {
		this.eingangsdatum = eingangsdatum;
	}

	public BetreuungsangebotTyp getBetreuungsangebotTyp() {
		return betreuungsangebotTyp;
	}

	public void setBetreuungsangebotTyp(BetreuungsangebotTyp betreuungsangebotTyp) {
		this.betreuungsangebotTyp = betreuungsangebotTyp;
	}

	public JaxInstitution getInstitution() {
		return institution;
	}

	public void setInstitution(JaxInstitution institution) {
		this.institution = institution;
	}
}

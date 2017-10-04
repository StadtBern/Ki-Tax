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

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.converters.LandConverter;
import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.enums.Land;

/**
 * Created by imanol on 17.03.16.
 * DTO fuer Adresse
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAdresse extends JaxAbstractDateRangedDTO {


	private static final long serialVersionUID = -1093677998323618626L;

	@NotNull
	private String strasse;
	private String hausnummer;

	private String zusatzzeile;
	@NotNull
	private String plz;
	@NotNull
	private String ort;

	@NotNull
	@XmlJavaTypeAdapter(LandConverter.class)
	private Land land = Land.CH;

	private String gemeinde;

	private AdresseTyp adresseTyp;

	private boolean nichtInGemeinde;

	@Nullable
	private String organisation;

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

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getOrt() {
		return ort;
	}

	public String getGemeinde() {
		return gemeinde;
	}

	public void setGemeinde(String gemeinde) {
		this.gemeinde = gemeinde;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getZusatzzeile() {
		return zusatzzeile;
	}

	public void setZusatzzeile(String zusatzzeile) {
		this.zusatzzeile = zusatzzeile;
	}

	public Land getLand() {
		return land;
	}

	public void setLand(Land land) {
		this.land = land;
	}

	public void setAdresseTyp(AdresseTyp adresseTyp) {
		this.adresseTyp = adresseTyp;
	}

	public AdresseTyp getAdresseTyp() {
		return adresseTyp;
	}

	public boolean isNichtInGemeinde() {
		return nichtInGemeinde;
	}

	public void setNichtInGemeinde(boolean nichtInGemeinde) {
		this.nichtInGemeinde = nichtInGemeinde;
	}

	@Nullable
	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(@Nullable String organisation) {
		this.organisation = organisation;
	}
}

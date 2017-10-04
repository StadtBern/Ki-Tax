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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import ch.dvbern.ebegu.util.Constants;

/**
 * DTO fuer Verfuegungen
 */
@XmlRootElement(name = "verfuegung")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxVerfuegung extends JaxAbstractDTO {

	private static final long serialVersionUID = 3359889270785929022L;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	private String generatedBemerkungen;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	private String manuelleBemerkungen;

	@Nonnull
	private List<JaxVerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();

	private boolean kategorieNormal = false;

	private boolean kategorieMaxEinkommen = false;

	private boolean kategorieKeinPensum = false;

	private boolean kategorieZuschlagZumErwerbspensum = false;

	private boolean kategorieNichtEintreten = false;

	@Nullable
	public String getGeneratedBemerkungen() {
		return generatedBemerkungen;
	}

	public void setGeneratedBemerkungen(@Nullable String generatedBemerkungen) {
		this.generatedBemerkungen = generatedBemerkungen;
	}

	@Nullable
	public String getManuelleBemerkungen() {
		return manuelleBemerkungen;
	}

	public void setManuelleBemerkungen(@Nullable String manuelleBemerkungen) {
		this.manuelleBemerkungen = manuelleBemerkungen;
	}

	@Nonnull
	public List<JaxVerfuegungZeitabschnitt> getZeitabschnitte() {
		return zeitabschnitte;
	}

	public void setZeitabschnitte(@Nonnull List<JaxVerfuegungZeitabschnitt> zeitabschnitte) {
		this.zeitabschnitte = zeitabschnitte;
	}

	public boolean isKategorieNormal() {
		return kategorieNormal;
	}

	public void setKategorieNormal(boolean kategorieNormal) {
		this.kategorieNormal = kategorieNormal;
	}

	public boolean isKategorieMaxEinkommen() {
		return kategorieMaxEinkommen;
	}

	public void setKategorieMaxEinkommen(boolean kategorieMaxEinkommen) {
		this.kategorieMaxEinkommen = kategorieMaxEinkommen;
	}

	public boolean isKategorieKeinPensum() {
		return kategorieKeinPensum;
	}

	public void setKategorieKeinPensum(boolean kategorieKeinPensum) {
		this.kategorieKeinPensum = kategorieKeinPensum;
	}

	public boolean isKategorieZuschlagZumErwerbspensum() {
		return kategorieZuschlagZumErwerbspensum;
	}

	public void setKategorieZuschlagZumErwerbspensum(boolean kategorieZuschlagZumErwerbspensum) {
		this.kategorieZuschlagZumErwerbspensum = kategorieZuschlagZumErwerbspensum;
	}

	public boolean isKategorieNichtEintreten() {
		return kategorieNichtEintreten;
	}

	public void setKategorieNichtEintreten(boolean kategorieNichtEintreten) {
		this.kategorieNichtEintreten = kategorieNichtEintreten;
	}
}

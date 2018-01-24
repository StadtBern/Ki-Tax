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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Daten der Anmeldung
 */
@XmlRootElement(name = "anmeldung")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAnmeldungDTO {

	private static final long serialVersionUID = -1227022381675937697L;

	@NotNull
	private JaxBetreuung betreuung;

	@NotNull
	private String kindContainerId;

	@Nullable
	private Integer wohnhaftImGleichenHaushalt;

	@NotNull
	private Boolean additionalKindQuestions;

	@Nullable
	private Boolean mutterspracheDeutsch;

	@Nullable
	private Boolean einschulung;

	public JaxBetreuung getBetreuung() {
		return betreuung;
	}

	public void setBetreuung(JaxBetreuung betreuung) {
		this.betreuung = betreuung;
	}

	public String getKindContainerId() {
		return kindContainerId;
	}

	public void setKindContainerId(String kindContainerId) {
		this.kindContainerId = kindContainerId;
	}

	@Nullable
	public Integer getWohnhaftImGleichenHaushalt() {
		return wohnhaftImGleichenHaushalt;
	}

	public void setWohnhaftImGleichenHaushalt(@Nullable Integer wohnhaftImGleichenHaushalt) {
		this.wohnhaftImGleichenHaushalt = wohnhaftImGleichenHaushalt;
	}

	public Boolean getAdditionalKindQuestions() {
		return additionalKindQuestions;
	}

	public void setAdditionalKindQuestions(Boolean additionalKindQuestions) {
		this.additionalKindQuestions = additionalKindQuestions;
	}

	@Nullable
	public Boolean getMutterspracheDeutsch() {
		return mutterspracheDeutsch;
	}

	public void setMutterspracheDeutsch(@Nullable Boolean mutterspracheDeutsch) {
		this.mutterspracheDeutsch = mutterspracheDeutsch;
	}

	@Nullable
	public Boolean getEinschulung() {
		return einschulung;
	}

	public void setEinschulung(@Nullable Boolean einschulung) {
		this.einschulung = einschulung;
	}
}

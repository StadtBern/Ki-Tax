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

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;
import ch.dvbern.lib.date.converters.LocalDateXMLConverter;

/**
 * DTO fuer Familiensituationen
 */
@XmlRootElement(name = "familiensituation")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFamiliensituation extends JaxAbstractDTO {

	private static final long serialVersionUID = -1297019741664130597L;

	@NotNull
	private EnumFamilienstatus familienstatus;

	@Nullable
	private EnumGesuchstellerKardinalitaet gesuchstellerKardinalitaet;

	@NotNull
	private JaxGesuch gesuch;

	@NotNull
	private Boolean gemeinsameSteuererklaerung;

	@Nullable
	private Boolean sozialhilfeBezueger;

	@Nullable
	private Boolean verguenstigungGewuenscht;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate aenderungPer = null;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public EnumFamilienstatus getFamilienstatus() {
		return familienstatus;
	}

	public void setFamilienstatus(EnumFamilienstatus familienstatus) {
		this.familienstatus = familienstatus;
	}

	@Nullable
	public EnumGesuchstellerKardinalitaet getGesuchstellerKardinalitaet() {
		return gesuchstellerKardinalitaet;
	}

	public void setGesuchstellerKardinalitaet(@Nullable EnumGesuchstellerKardinalitaet gesuchstellerKardinalitaet) {
		this.gesuchstellerKardinalitaet = gesuchstellerKardinalitaet;
	}

	public JaxGesuch getGesuch() {
		return gesuch;
	}

	public void setGesuch(JaxGesuch gesuch) {
		this.gesuch = gesuch;
	}

	@NotNull
	public Boolean getGemeinsameSteuererklaerung() {
		return gemeinsameSteuererklaerung;
	}

	public void setGemeinsameSteuererklaerung(@NotNull Boolean gemeinsameSteuererklaerung) {
		this.gemeinsameSteuererklaerung = gemeinsameSteuererklaerung;
	}

	@Nullable
	public LocalDate getAenderungPer() {
		return aenderungPer;
	}

	public void setAenderungPer(@Nullable LocalDate aenderungPer) {
		this.aenderungPer = aenderungPer;
	}

	@Nullable
	public Boolean getSozialhilfeBezueger() {
		return sozialhilfeBezueger;
	}

	public void setSozialhilfeBezueger(@Nullable Boolean sozialhilfeBezueger) {
		this.sozialhilfeBezueger = sozialhilfeBezueger;
	}

	@Nullable
	public Boolean getVerguenstigungGewuenscht() {
		return verguenstigungGewuenscht;
	}

	public void setVerguenstigungGewuenscht(@Nullable Boolean verguenstigungGewuenscht) {
		this.verguenstigungGewuenscht = verguenstigungGewuenscht;
	}

}

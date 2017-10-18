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

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.api.enums.JaxAntragstatus;
import ch.dvbern.ebegu.api.enums.JaxTarifart;
import ch.dvbern.lib.date.converters.LocalDateXMLConverter;

/**
 * DTO für die Finanzielle Situation für die externe Schnittstelle
 */
@XmlRootElement(name = "finanzielleSituation")
public class JaxExternalFinanzielleSituation implements Serializable {

	private static final long serialVersionUID = 5211944101244853396L;

	@Nonnull
	private Long fallNummer;

	@Nonnull
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate stichtag;

	@Nonnull
	private BigDecimal massgebendesEinkommen;

	@Nonnull
	private JaxAntragstatus antragStatus;

	@Nonnull
	private JaxTarifart tarifart;

	//TODO (team) Massgebendes Einkommen VOR Familienabzug?
	//TODO (team) Müsste hier nicht die Rechnungsadresse noch rein?


	public JaxExternalFinanzielleSituation(
		@Nonnull Long fallNummer,
		@Nonnull LocalDate stichtag,
		@Nonnull BigDecimal massgebendesEinkommen,
		@Nonnull JaxAntragstatus antragStatus,
		@Nonnull JaxTarifart tarifart) {

		this.fallNummer = fallNummer;
		this.stichtag = stichtag;
		this.massgebendesEinkommen = massgebendesEinkommen;
		this.antragStatus = antragStatus;
		this.tarifart = tarifart;
	}

	@Nonnull
	public Long getFallNummer() {
		return fallNummer;
	}

	public void setFallNummer(@Nonnull Long fallNummer) {
		this.fallNummer = fallNummer;
	}

	@Nonnull
	public LocalDate getStichtag() {
		return stichtag;
	}

	public void setStichtag(@Nonnull LocalDate stichtag) {
		this.stichtag = stichtag;
	}

	@Nonnull
	public BigDecimal getMassgebendesEinkommen() {
		return massgebendesEinkommen;
	}

	public void setMassgebendesEinkommen(@Nonnull BigDecimal massgebendesEinkommen) {
		this.massgebendesEinkommen = massgebendesEinkommen;
	}

	@Nonnull
	public JaxAntragstatus getAntragStatus() {
		return antragStatus;
	}

	public void setAntragStatus(@Nonnull JaxAntragstatus antragStatus) {
		this.antragStatus = antragStatus;
	}

	@Nonnull
	public JaxTarifart getTarifart() {
		return tarifart;
	}

	public void setTarifart(@Nonnull JaxTarifart tarifart) {
		this.tarifart = tarifart;
	}
}

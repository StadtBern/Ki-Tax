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

import ch.dvbern.ebegu.api.enums.JaxExternalAntragstatus;
import ch.dvbern.ebegu.api.enums.JaxExternalTarifart;
import ch.dvbern.lib.date.converters.LocalDateXMLConverter;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

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
	private BigDecimal massgebendesEinkommenVorAbzug;

	@Nonnull
	private BigDecimal abzug;

	@Nonnull
	private JaxExternalAntragstatus antragStatus;

	@Nonnull
	private JaxExternalTarifart tarifart;

	@Nonnull
	private JaxExternalRechnungsAdresse rechnungsAdresse;

	public JaxExternalFinanzielleSituation(
		@Nonnull Long fallNummer,
		@Nonnull LocalDate stichtag,
		@Nonnull BigDecimal massgebendesEinkommenVorAbzug,
		@Nonnull BigDecimal abzug,
		@Nonnull JaxExternalAntragstatus antragStatus,
		@Nonnull JaxExternalTarifart tarifart,
		@Nonnull JaxExternalRechnungsAdresse rechnungsAdresse) {

		this.fallNummer = fallNummer;
		this.stichtag = stichtag;
		this.massgebendesEinkommenVorAbzug = massgebendesEinkommenVorAbzug;
		this.abzug = abzug;
		this.antragStatus = antragStatus;
		this.tarifart = tarifart;
		this.rechnungsAdresse = rechnungsAdresse;
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
	public JaxExternalAntragstatus getAntragStatus() {
		return antragStatus;
	}

	public void setAntragStatus(@Nonnull JaxExternalAntragstatus antragStatus) {
		this.antragStatus = antragStatus;
	}

	@Nonnull
	public JaxExternalTarifart getTarifart() {
		return tarifart;
	}

	public void setTarifart(@Nonnull JaxExternalTarifart tarifart) {
		this.tarifart = tarifart;
	}

	@Nonnull
	public BigDecimal getMassgebendesEinkommenVorAbzug() {
		return massgebendesEinkommenVorAbzug;
	}

	public void setMassgebendesEinkommenVorAbzug(@Nonnull BigDecimal massgebendesEinkommenVorAbzug) {
		this.massgebendesEinkommenVorAbzug = massgebendesEinkommenVorAbzug;
	}

	@Nonnull
	public BigDecimal getAbzug() {
		return abzug;
	}

	public void setAbzug(@Nonnull BigDecimal abzug) {
		this.abzug = abzug;
	}

	@Nonnull
	public JaxExternalRechnungsAdresse getRechnungsAdresse() {
		return rechnungsAdresse;
	}

	public void setRechnungsAdresse(@Nonnull JaxExternalRechnungsAdresse rechnungsAdresse) {
		this.rechnungsAdresse = rechnungsAdresse;
	}
}

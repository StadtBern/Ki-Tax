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

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import ch.dvbern.ebegu.enums.ZahlungStatus;

/**
 * DTO fuer Zahlungen
 */
@XmlRootElement(name = "zahlung")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxZahlung extends JaxAbstractDTO {

	private static final long serialVersionUID = 1661454343875422672L;

	@NotNull
	private String institutionsName;

	@NotNull
	private String institutionsId;

	@NotNull
	private ZahlungStatus status;

	@NotNull
	private BigDecimal betragTotalZahlung;

	public String getInstitutionsName() {
		return institutionsName;
	}

	public void setInstitutionsName(String institutionsName) {
		this.institutionsName = institutionsName;
	}

	public ZahlungStatus getStatus() {
		return status;
	}

	public void setStatus(ZahlungStatus status) {
		this.status = status;
	}

	public BigDecimal getBetragTotalZahlung() {
		return betragTotalZahlung;
	}

	public void setBetragTotalZahlung(BigDecimal betragTotalZahlung) {
		this.betragTotalZahlung = betragTotalZahlung;
	}

	public String getInstitutionsId() {
		return institutionsId;
	}

	public void setInstitutionsId(String institutionsId) {
		this.institutionsId = institutionsId;
	}
}

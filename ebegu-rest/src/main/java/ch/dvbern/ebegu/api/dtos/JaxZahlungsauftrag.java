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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.enums.ZahlungauftragStatus;
import ch.dvbern.lib.date.converters.LocalDateTimeXMLConverter;
import ch.dvbern.lib.date.converters.LocalDateXMLConverter;

/**
 * DTO fuer Zahlungsauftrag
 */
@XmlRootElement(name = "zahlungsauftrag")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxZahlungsauftrag extends JaxAbstractDateRangedDTO {

	private static final long serialVersionUID = 5908117979039694339L;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate datumFaellig;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime datumGeneriert;

	@NotNull
	private ZahlungauftragStatus status;

	@NotNull
	private String beschrieb;

	@NotNull
	private BigDecimal betragTotalAuftrag;

	@Nonnull
	private List<JaxZahlung> zahlungen = new ArrayList<>();

	public LocalDate getDatumFaellig() {
		return datumFaellig;
	}

	public void setDatumFaellig(LocalDate datumFaellig) {
		this.datumFaellig = datumFaellig;
	}

	public LocalDateTime getDatumGeneriert() {
		return datumGeneriert;
	}

	public void setDatumGeneriert(LocalDateTime datumGeneriert) {
		this.datumGeneriert = datumGeneriert;
	}

	public ZahlungauftragStatus getStatus() {
		return status;
	}

	public void setStatus(ZahlungauftragStatus status) {
		this.status = status;
	}

	public String getBeschrieb() {
		return beschrieb;
	}

	public void setBeschrieb(String beschrieb) {
		this.beschrieb = beschrieb;
	}

	@Nonnull
	public List<JaxZahlung> getZahlungen() {
		return zahlungen;
	}

	public void setZahlungen(@Nonnull List<JaxZahlung> zahlungen) {
		this.zahlungen = zahlungen;
	}

	public BigDecimal getBetragTotalAuftrag() {
		return betragTotalAuftrag;
	}

	public void setBetragTotalAuftrag(BigDecimal betragTotalAuftrag) {
		this.betragTotalAuftrag = betragTotalAuftrag;
	}
}

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

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.lib.date.converters.LocalDateTimeXMLConverter;

/**
 * DTO fuer AntragStatusHistory
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAntragStatusHistory extends JaxAbstractDTO {

	private static final long serialVersionUID = -1297026889674146397L;

	@NotNull
	private String gesuchId;

	@NotNull
	private JaxAuthLoginElement benutzer;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime timestampVon;

	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime timestampBis;

	@NotNull
	private AntragStatusDTO status;

	public String getGesuchId() {
		return gesuchId;
	}

	public void setGesuchId(String gesuchId) {
		this.gesuchId = gesuchId;
	}

	public JaxAuthLoginElement getBenutzer() {
		return benutzer;
	}

	public void setBenutzer(JaxAuthLoginElement benutzer) {
		this.benutzer = benutzer;
	}

	public LocalDateTime getTimestampVon() {
		return timestampVon;
	}

	public void setTimestampVon(LocalDateTime timestampVon) {
		this.timestampVon = timestampVon;
	}

	public LocalDateTime getTimestampBis() {
		return timestampBis;
	}

	public void setTimestampBis(LocalDateTime timestampBis) {
		this.timestampBis = timestampBis;
	}

	public AntragStatusDTO getStatus() {
		return status;
	}

	public void setStatus(AntragStatusDTO status) {
		this.status = status;
	}
}

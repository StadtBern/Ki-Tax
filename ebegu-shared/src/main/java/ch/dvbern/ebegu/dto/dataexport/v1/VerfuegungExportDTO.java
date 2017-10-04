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

package ch.dvbern.ebegu.dto.dataexport.v1;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonFormat;

import ch.dvbern.lib.date.converters.LocalDateTimeXMLConverter;
import ch.dvbern.lib.date.converters.LocalDateXMLConverter;

/**
 * This is a DTO that is used to export the relevant Information about a {@link ch.dvbern.ebegu.entities.Verfuegung}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class VerfuegungExportDTO {

	private String refnr;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate von;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate bis;

	private int version;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime verfuegtAm;

	private KindExportDTO kind;

	private GesuchstellerExportDTO gesuchsteller;

	private BetreuungExportDTO betreuung;

	private List<ZeitabschnittExportDTO> zeitabschnitte;

	private List<ZeitabschnittExportDTO> ignorierteZeitabschnitte;

	public String getRefnr() {
		return refnr;
	}

	public void setRefnr(String refnr) {
		this.refnr = refnr;
	}

	public LocalDate getVon() {
		return von;
	}

	public void setVon(LocalDate von) {
		this.von = von;
	}

	public LocalDate getBis() {
		return bis;
	}

	public void setBis(LocalDate bis) {
		this.bis = bis;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public LocalDateTime getVerfuegtAm() {
		return verfuegtAm;
	}

	public void setVerfuegtAm(LocalDateTime verfuegtAm) {
		this.verfuegtAm = verfuegtAm;
	}

	public KindExportDTO getKind() {
		return kind;
	}

	public void setKind(KindExportDTO kind) {
		this.kind = kind;
	}

	public GesuchstellerExportDTO getGesuchsteller() {
		return gesuchsteller;
	}

	public void setGesuchsteller(GesuchstellerExportDTO gesuchsteller) {
		this.gesuchsteller = gesuchsteller;
	}

	public BetreuungExportDTO getBetreuung() {
		return betreuung;
	}

	public void setBetreuung(BetreuungExportDTO betreuung) {
		this.betreuung = betreuung;
	}

	public List<ZeitabschnittExportDTO> getZeitabschnitte() {
		return zeitabschnitte;
	}

	public void setZeitabschnitte(List<ZeitabschnittExportDTO> zeitabschnitte) {
		this.zeitabschnitte = zeitabschnitte;
	}

	public List<ZeitabschnittExportDTO> getIgnorierteZeitabschnitte() {
		return ignorierteZeitabschnitte;
	}

	public void setIgnorierteZeitabschnitte(List<ZeitabschnittExportDTO> ignorierteZeitabschnitte) {
		this.ignorierteZeitabschnitte = ignorierteZeitabschnitte;
	}

	@SuppressWarnings("OverlyComplexBooleanExpression")
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VerfuegungExportDTO that = (VerfuegungExportDTO) o;
		return getVersion() == that.getVersion() &&
			Objects.equals(getRefnr(), that.getRefnr()) &&
			Objects.equals(getVon(), that.getVon()) &&
			Objects.equals(getBis(), that.getBis()) &&
			Objects.equals(getVerfuegtAm(), that.getVerfuegtAm()) &&
			Objects.equals(getKind(), that.getKind()) &&
			Objects.equals(getGesuchsteller(), that.getGesuchsteller()) &&
			Objects.equals(getBetreuung(), that.getBetreuung()) &&
			Objects.equals(getZeitabschnitte(), that.getZeitabschnitte()) &&
			Objects.equals(getIgnorierteZeitabschnitte(), that.getIgnorierteZeitabschnitte());
	}

	@Override
	public int hashCode() {
		return Objects.hash(refnr, von, bis, version, verfuegtAm, kind, gesuchsteller, betreuung, zeitabschnitte);
	}
}

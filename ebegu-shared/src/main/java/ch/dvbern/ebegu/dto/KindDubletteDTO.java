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

package ch.dvbern.ebegu.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * DTO das Resultat einer DublettenSuche bei den Kindern.
 * GesuchId und KindNummer werden fuer die Zusammenstellung des Links gebraucht, die FallNummer zur Anzeige auf dem GUI.
 */
public class KindDubletteDTO {

	private String gesuchId;
	private long fallNummer;
	private Integer kindNummerOriginal;
	private Integer kindNummerDublette;
	private LocalDateTime timestampErstelltGesuch;

	public KindDubletteDTO(String gesuchId, long fallNummer, Integer kindNummerOriginal, Integer kindNummerDublette,
		LocalDateTime timestampErstelltGesuch) {
		this.gesuchId = gesuchId;
		this.fallNummer = fallNummer;
		this.kindNummerOriginal = kindNummerOriginal;
		this.kindNummerDublette = kindNummerDublette;
		this.timestampErstelltGesuch = timestampErstelltGesuch;
	}

	@SuppressFBWarnings("NM_CONFUSING")
	public String getGesuchId() {
		return gesuchId;
	}

	@SuppressFBWarnings("NM_CONFUSING")
	public void setGesuchId(String gesuchId) {
		this.gesuchId = gesuchId;
	}

	public long getFallNummer() {
		return fallNummer;
	}

	public void setFallNummer(long fallNummer) {
		this.fallNummer = fallNummer;
	}

	public Integer getKindNummerOriginal() {
		return kindNummerOriginal;
	}

	public void setKindNummerOriginal(Integer kindNummerOriginal) {
		this.kindNummerOriginal = kindNummerOriginal;
	}

	public Integer getKindNummerDublette() {
		return kindNummerDublette;
	}

	public void setKindNummerDublette(Integer kindNummerDublette) {
		this.kindNummerDublette = kindNummerDublette;
	}

	public LocalDateTime getTimestampErstelltGesuch() {
		return timestampErstelltGesuch;
	}

	public void setTimestampErstelltGesuch(LocalDateTime timestampErstelltGesuch) {
		this.timestampErstelltGesuch = timestampErstelltGesuch;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		KindDubletteDTO otherDublette = (KindDubletteDTO) obj;
		return getFallNummer() == otherDublette.getFallNummer() &&
			Objects.equals(getKindNummerOriginal(), otherDublette.getKindNummerOriginal());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFallNummer(), getKindNummerOriginal());
	}
}

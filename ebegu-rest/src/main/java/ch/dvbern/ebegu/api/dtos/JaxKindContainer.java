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

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Kind Container
 */
@XmlRootElement(name = "kind")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxKindContainer extends JaxAbstractDTO {

	private static final long serialVersionUID = -8912537186244981782L;

	@Valid
	private JaxKind kindGS;

	@Valid
	private JaxKind kindJA;

	@NotNull
	private Set<JaxBetreuung> betreuungen = new LinkedHashSet<>();

	@Min(1)
	private Integer kindNummer = 1;

	@Min(1)
	private Integer nextNumberBetreuung = 1;

	@Nullable
	private Boolean kindMutiert;

	public JaxKind getKindGS() {
		return kindGS;
	}

	public void setKindGS(JaxKind kindGS) {
		this.kindGS = kindGS;
	}

	public JaxKind getKindJA() {
		return kindJA;
	}

	public void setKindJA(JaxKind kindJA) {
		this.kindJA = kindJA;
	}

	public Set<JaxBetreuung> getBetreuungen() {
		return betreuungen;
	}

	public void setBetreuungen(Set<JaxBetreuung> betreuungen) {
		this.betreuungen = betreuungen;
	}

	public Integer getKindNummer() {
		return kindNummer;
	}

	public void setKindNummer(Integer kindNummer) {
		this.kindNummer = kindNummer;
	}

	public Integer getNextNumberBetreuung() {
		return nextNumberBetreuung;
	}

	public void setNextNumberBetreuung(Integer nextNumberBetreuung) {
		this.nextNumberBetreuung = nextNumberBetreuung;
	}

	public Boolean isKindMutiert() {
		return kindMutiert;
	}

	public void setKindMutiert(Boolean kindMutiert) {
		this.kindMutiert = kindMutiert;
	}
}

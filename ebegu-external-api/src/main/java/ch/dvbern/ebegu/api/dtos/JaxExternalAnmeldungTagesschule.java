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

import ch.dvbern.ebegu.api.enums.JaxExternalBetreuungsangebotTyp;
import ch.dvbern.ebegu.api.enums.JaxExternalBetreuungsstatus;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * DTO für die Anmeldung eines Tagesschule-Angebots für die externe Schnittstelle
 */
@XmlRootElement(name = "anmeldungTagesschule")
public class JaxExternalAnmeldungTagesschule extends JaxExternalAnmeldung {

	private static final long serialVersionUID = 5211944101244853396L;

	@Nonnull
	private List<JaxExternalModul> anmeldungen;

	//TODO (team) was ist "istRegistriert"?
	//TODO (team) was ist "bemerkung"?


	public JaxExternalAnmeldungTagesschule(
		@Nonnull String bgNummer,
		@Nonnull JaxExternalBetreuungsstatus betreuungsstatus,
		@Nonnull String institutionName,
		@Nonnull List<JaxExternalModul> anmeldungen) {

		super(bgNummer, betreuungsstatus, JaxExternalBetreuungsangebotTyp.TAGESSCHULE, institutionName);
		this.anmeldungen = anmeldungen;
	}

	@Nonnull
	public List<JaxExternalModul> getAnmeldungen() {
		return anmeldungen;
	}

	public void setAnmeldungen(@Nonnull List<JaxExternalModul> anmeldungen) {
		this.anmeldungen = anmeldungen;
	}
}

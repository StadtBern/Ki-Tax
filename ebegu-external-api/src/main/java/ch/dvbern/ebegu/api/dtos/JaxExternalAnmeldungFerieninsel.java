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

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlRootElement;

import ch.dvbern.ebegu.api.enums.JaxExternalBetreuungsangebotTyp;
import ch.dvbern.ebegu.api.enums.JaxExternalBetreuungsstatus;

/**
 * DTO für die Anmeldung eines Ferieninsel-Angebots für die externe Schnittstelle
 */
@XmlRootElement(name = "anmeldungFerieninsel")
public class JaxExternalAnmeldungFerieninsel extends JaxExternalAnmeldung {

	private static final long serialVersionUID = 5211944101244853396L;


	@Nonnull
	private JaxExternalFerieninsel ferieninsel;

	public JaxExternalAnmeldungFerieninsel(
		@Nonnull String bgNummer,
		@Nonnull JaxExternalBetreuungsstatus betreuungsstatus,
		@Nonnull String keyInstitution,
		@Nonnull JaxExternalFerieninsel ferieninsel,
		@Nonnull String kindName,
		@Nonnull String kindVorname) {

		super(bgNummer, betreuungsstatus, JaxExternalBetreuungsangebotTyp.FERIENINSEL, keyInstitution, kindName, kindVorname);
		this.ferieninsel = ferieninsel;
	}

	@Nonnull
	public JaxExternalFerieninsel getFerieninsel() {
		return ferieninsel;
	}

	public void setFerieninsel(@Nonnull JaxExternalFerieninsel ferieninsel) {
		this.ferieninsel = ferieninsel;
	}
}

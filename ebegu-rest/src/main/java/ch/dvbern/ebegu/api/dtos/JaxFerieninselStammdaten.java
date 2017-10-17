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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.enums.Ferienname;
import ch.dvbern.lib.date.converters.LocalDateXMLConverter;

/**
 * DTO fuer Ferieninsel-Stammdaten
 */
@XmlRootElement(name = "ferieninselStammdaten")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFerieninselStammdaten extends JaxAbstractDTO {

	private static final long serialVersionUID = -755938593616840976L;

	@NotNull
	private Ferienname ferienname;

	@NotNull
	private List<JaxFerieninselZeitraum> zeitraumList = new ArrayList<>();

	@NotNull
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate anmeldeschluss;

	@NotNull
	private JaxGesuchsperiode gesuchsperiode;

	public Ferienname getFerienname() {
		return ferienname;
	}

	public void setFerienname(Ferienname ferienname) {
		this.ferienname = ferienname;
	}

	public List<JaxFerieninselZeitraum> getZeitraumList() {
		return zeitraumList;
	}

	public void setZeitraumList(List<JaxFerieninselZeitraum> zeitraumList) {
		this.zeitraumList = zeitraumList;
	}

	public LocalDate getAnmeldeschluss() {
		return anmeldeschluss;
	}

	public void setAnmeldeschluss(LocalDate anmeldeschluss) {
		this.anmeldeschluss = anmeldeschluss;
	}

	public JaxGesuchsperiode getGesuchsperiode() {
		return gesuchsperiode;
	}

	public void setGesuchsperiode(JaxGesuchsperiode gesuchsperiode) {
		this.gesuchsperiode = gesuchsperiode;
	}
}

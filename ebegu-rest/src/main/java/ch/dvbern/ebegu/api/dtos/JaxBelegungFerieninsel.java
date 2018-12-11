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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import ch.dvbern.ebegu.enums.Ferienname;
import ch.dvbern.ebegu.util.Constants;

/**
 * DTO for the Belegung of a Ferieninsel in a Betreuung.
 */
@XmlRootElement(name = "belegungFerieninsel")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxBelegungFerieninsel extends JaxAbstractDTO {

	private static final long serialVersionUID = 1909218530553284899L;

	@NotNull
	private Ferienname ferienname;

	@NotNull
	private List<JaxBelegungFerieninselTag> tage = new ArrayList<>();

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	private String notfallAngaben;

	@Nonnull
	public Ferienname getFerienname() {
		return ferienname;
	}

	public void setFerienname(@Nonnull Ferienname ferienname) {
		this.ferienname = ferienname;
	}

	@Nonnull
	public List<JaxBelegungFerieninselTag> getTage() {
		return tage;
	}

	public void setTage(@Nonnull List<JaxBelegungFerieninselTag> tage) {
		this.tage = tage;
	}

	@Nullable
	public String getNotfallAngaben() {
		return notfallAngaben;
	}

	public void setNotfallAngaben(@Nullable String notfallAngaben) {
		this.notfallAngaben = notfallAngaben;
	}
}

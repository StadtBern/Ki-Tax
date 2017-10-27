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

package ch.dvbern.ebegu.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.enums.Ferienname;
import org.hibernate.annotations.SortNatural;
import org.hibernate.envers.Audited;

/**
 * Entity for the Belegung of a Ferieninsel in a Betreuung.
 */
@Audited
@Entity
public class BelegungFerieninsel extends AbstractEntity {

	private static final long serialVersionUID = -8403435739182708718L;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Ferienname ferienname;

	@NotNull
	@Valid
	@SortNatural
	@ManyToMany(cascade = CascadeType.ALL)
	private List<BelegungFerieninselTag> tage = new ArrayList<>();

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		//noinspection RedundantIfStatement
		if (!(other instanceof BelegungFerieninsel)) {
			return false;
		}
		BelegungFerieninsel that = (BelegungFerieninsel) other;

		boolean tageSame = this.getTage().stream().allMatch(
			(tageList) -> that.getTage().stream().anyMatch(otherPensenCont -> otherPensenCont.isSame(tageList)));

		return tageSame && Objects.equals(ferienname, that.ferienname);
	}

	public Ferienname getFerienname() {
		return ferienname;
	}

	public void setFerienname(Ferienname ferienname) {
		this.ferienname = ferienname;
	}

	public List<BelegungFerieninselTag> getTage() {
		return tage;
	}

	public void setTage(List<BelegungFerieninselTag> tage) {
		this.tage = tage;
	}
}

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.enums.Ferienname;
import ch.dvbern.ebegu.validators.CheckFerieninselStammdatenDatesOverlapping;
import org.hibernate.annotations.SortNatural;
import org.hibernate.envers.Audited;

/**
 * Entity for the Basedata of a Ferieninsel
 */
@Audited
@Entity
@CheckFerieninselStammdatenDatesOverlapping
public class FerieninselStammdaten extends AbstractEntity {

	private static final long serialVersionUID = 6703477164293147908L;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Ferienname ferienname;

	@NotNull
	@Valid
	@SortNatural
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FerieninselZeitraum> zeitraumList = new ArrayList<>();

	@NotNull
	@Column(nullable = false)
	private LocalDate anmeldeschluss;

	@NotNull
	@ManyToOne(optional = false)
	private Gesuchsperiode gesuchsperiode;

	public Ferienname getFerienname() {
		return ferienname;
	}

	public void setFerienname(Ferienname ferienname) {
		this.ferienname = ferienname;
	}

	public List<FerieninselZeitraum> getZeitraumList() {
		return zeitraumList;
	}

	public void setZeitraumList(List<FerieninselZeitraum> zeitraumList) {
		this.zeitraumList = zeitraumList;
	}

	public LocalDate getAnmeldeschluss() {
		return anmeldeschluss;
	}

	public void setAnmeldeschluss(LocalDate anmeldeschluss) {
		this.anmeldeschluss = anmeldeschluss;
	}

	public Gesuchsperiode getGesuchsperiode() {
		return gesuchsperiode;
	}

	public void setGesuchsperiode(Gesuchsperiode gesuchsperiode) {
		this.gesuchsperiode = gesuchsperiode;
	}

	@Override
	public boolean isSame(AbstractEntity other) {
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!(other instanceof FerieninselStammdaten)) {
			return false;
		}
		final FerieninselStammdaten otherFerieninselStammdaten = (FerieninselStammdaten) other;
		return Objects.equals(getFerienname(), otherFerieninselStammdaten.getFerienname()) &&
			Objects.equals(getGesuchsperiode(), otherFerieninselStammdaten.getGesuchsperiode());
	}
}

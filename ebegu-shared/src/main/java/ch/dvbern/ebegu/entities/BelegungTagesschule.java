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
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ch.dvbern.ebegu.util.Constants;
import org.hibernate.annotations.SortNatural;
import org.hibernate.envers.Audited;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Entity for the Belegung of the Tageschulangebote in a Betreuung.
 */
@Audited
@Entity
public class BelegungTagesschule extends AbstractEntity {

	private static final long serialVersionUID = -8403435739182708718L;

	@NotNull
	@Valid
	@SortNatural
	@ManyToMany
	// es darf nicht cascadeAll sein, da sonst die Module geloescht werden, wenn die Belegung geloescht wird, obwohl das Modul eigentlich zur Institutione
	// gehoert
	private Set<ModulTagesschule> moduleTagesschule = new TreeSet<>();

	@NotNull
	@Column(nullable = false)
	private LocalDate eintrittsdatum;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Nullable
	@Column
	private String planKlasse;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String bemerkung;

	@Column(nullable = false)
	private boolean abweichungZweitesSemester = false;

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
		if (!(other instanceof BelegungTagesschule)) {
			return false;
		}
		BelegungTagesschule otherBelegungTS = (BelegungTagesschule) other;
		return Objects.equals(getPlanKlasse(), otherBelegungTS.getPlanKlasse()) &&
			Objects.equals(getEintrittsdatum(), otherBelegungTS.getEintrittsdatum());
	}

	@Nonnull
	public Set<ModulTagesschule> getModuleTagesschule() {
		return moduleTagesschule;
	}

	public void setModuleTagesschule(@Nonnull Set<ModulTagesschule> module) {
		this.moduleTagesschule = module;
	}

	@Nonnull
	public LocalDate getEintrittsdatum() {
		return eintrittsdatum;
	}

	public void setEintrittsdatum(@Nonnull LocalDate eintrittsdatum) {
		this.eintrittsdatum = eintrittsdatum;
	}

	@Nullable
	public String getPlanKlasse() {
		return planKlasse;
	}

	public void setPlanKlasse(@Nullable String planKlasse) {
		this.planKlasse = planKlasse;
	}

	@Nullable
	public String getBemerkung() {
		return bemerkung;
	}

	public void setBemerkung(@Nullable String bemerkung) {
		this.bemerkung = bemerkung;
	}

	public boolean isAbweichungZweitesSemester() {
		return abweichungZweitesSemester;
	}

	public void setAbweichungZweitesSemester(boolean abweichungZweitesSemester) {
		this.abweichungZweitesSemester = abweichungZweitesSemester;
	}

	@Nonnull
	public BelegungTagesschule copyForMutation(@Nonnull BelegungTagesschule mutation, @Nonnull Betreuung parentBetreuung) {
		super.copyForMutation(mutation);
		mutation.setEintrittsdatum(LocalDate.from(eintrittsdatum));
		mutation.setPlanKlasse(this.getPlanKlasse());
		mutation.setBemerkung(this.getBemerkung());
		mutation.setAbweichungZweitesSemester(this.abweichungZweitesSemester);

		// Don't copy them, because it's a ManyToMany realation
		mutation.getModuleTagesschule().clear();
		mutation.getModuleTagesschule().addAll(moduleTagesschule);

		return mutation;
	}
}

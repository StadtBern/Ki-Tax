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

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.enums.ModulTagesschuleName;
import ch.dvbern.ebegu.validators.CheckTimeRange;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

/**
 * Entity for the Module of the Tageschulangebote.
 */
@CheckTimeRange
@Audited
@Entity
public class ModulTagesschule extends AbstractEntity implements Comparable<ModulTagesschule> {

	private static final long serialVersionUID = -8403411439182708718L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_modul_tagesschule_inst_stammdaten_tagesschule_id"), nullable = false)
	private InstitutionStammdatenTagesschule institutionStammdatenTagesschule;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	@Column(nullable = false)
	private DayOfWeek wochentag;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	@Column(nullable = false)
	private ModulTagesschuleName modulTagesschuleName;

	@Column(nullable = false)
	private LocalTime zeitVon;

	@Column(nullable = false)
	private LocalTime zeitBis;

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!(other instanceof ModulTagesschule)) {
			return false;
		}
		final ModulTagesschule otherModulTagesschule = (ModulTagesschule) other;
		return getModulTagesschuleName() == otherModulTagesschule.getModulTagesschuleName() &&
			getWochentag() == otherModulTagesschule.getWochentag() &&
			Objects.equals(getZeitVon(), otherModulTagesschule.getZeitVon()) &&
			Objects.equals(getZeitBis(), otherModulTagesschule.getZeitBis());
	}

	public DayOfWeek getWochentag() {
		return wochentag;
	}

	public void setWochentag(DayOfWeek wochentag) {
		this.wochentag = wochentag;
	}

	public ModulTagesschuleName getModulTagesschuleName() {
		return modulTagesschuleName;
	}

	public void setModulTagesschuleName(ModulTagesschuleName modulname) {
		this.modulTagesschuleName = modulname;
	}

	public LocalTime getZeitVon() {
		return zeitVon;
	}

	public void setZeitVon(LocalTime zeitVon) {
		this.zeitVon = zeitVon;
	}

	public LocalTime getZeitBis() {
		return zeitBis;
	}

	public void setZeitBis(LocalTime zeitBis) {
		this.zeitBis = zeitBis;
	}

	public InstitutionStammdatenTagesschule getInstitutionStammdatenTagesschule() {
		return institutionStammdatenTagesschule;
	}

	public void setInstitutionStammdatenTagesschule(InstitutionStammdatenTagesschule instStammdaten) {
		this.institutionStammdatenTagesschule = instStammdaten;
	}

	@Override
	public int compareTo(@Nonnull ModulTagesschule o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getZeitVon(), o.getZeitVon());
		builder.append(this.getZeitBis(), o.getZeitBis());
		builder.append(this.getWochentag(), o.getWochentag());
		builder.append(this.getModulTagesschuleName(), o.getModulTagesschuleName());
		return builder.toComparison();
	}
//
//	@Override
//	public boolean equals(@Nullable Object o) {
//		if (this == o) {
//			return true;
//		}
//
//		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
//			return false;
//		}
//
//		ModulTagesschule that = (ModulTagesschule) o;
//
//		Objects.requireNonNull(getWochentag());
//		Objects.requireNonNull(getModulTagesschuleName());
//		Objects.requireNonNull(getInstitutionStammdatenTagesschule());
//		Objects.requireNonNull(that.getWochentag());
//		Objects.requireNonNull(that.getModulTagesschuleName());
//		Objects.requireNonNull(that.getInstitutionStammdatenTagesschule());
//
//		boolean wochentag = getWochentag().equals(that.getWochentag());
//		boolean name = getModulTagesschuleName().equals(that.getModulTagesschuleName());
//		boolean instStammdatenTagesschule = getInstitutionStammdatenTagesschule().equals(that.getInstitutionStammdatenTagesschule());
//
//		return wochentag && name && instStammdatenTagesschule;
//	}
//
//	@Override
//	public int hashCode() {
//		int result  = 31* (wochentag != null ? wochentag.hashCode() : 0);
//		result = 31 * result + (modulTagesschuleName != null ? modulTagesschuleName.hashCode() : 0);
//		result = 31 * result + (zeitVon != null ? zeitVon.hashCode() : 0);
//		result = 31 * result + (zeitBis != null ? zeitBis.hashCode() : 0);
//		return result;
//	}
}

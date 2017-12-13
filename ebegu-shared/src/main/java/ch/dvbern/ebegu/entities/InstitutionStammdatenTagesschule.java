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

import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.Valid;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.SortNatural;
import org.hibernate.envers.Audited;

/**
 * Entitaet zum Speichern von InstitutionStammdatenTagesschule in der Datenbank.
 */
@Audited
@Entity
public class InstitutionStammdatenTagesschule extends AbstractEntity implements Comparable<InstitutionStammdatenTagesschule> {

	private static final long serialVersionUID = 3991623541799163623L;

	@Nullable
	@Valid
	@SortNatural
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "institutionStammdatenTagesschule")
	private Set<ModulTagesschule> moduleTagesschule = new TreeSet<>();

	public InstitutionStammdatenTagesschule() {
	}

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!(other instanceof InstitutionStammdatenTagesschule)) {
			return false;
		}
		return true;
	}

	@Nullable
	public Set<ModulTagesschule> getModuleTagesschule() {
		return moduleTagesschule;
	}

	public void setModuleTagesschule(@Nullable Set<ModulTagesschule> moduleTagesschule) {
		this.moduleTagesschule = moduleTagesschule;
	}

	@Override
	public int compareTo(InstitutionStammdatenTagesschule o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getId(), o.getId());
		return builder.toComparison();
	}
}

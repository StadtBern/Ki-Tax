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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

/**
 * Entity fuer Abwesenheit.
 */
@SuppressWarnings("ComparableImplementedButEqualsNotOverridden")
@Audited
@Entity
public class Abwesenheit extends AbstractDateRangedEntity implements Comparable<Abwesenheit> {

	private static final long serialVersionUID = -6776981643150835840L;


	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "abwesenheitJA")
	private AbwesenheitContainer abwesenheitContainer;

	public Abwesenheit() {
	}

	public AbwesenheitContainer getAbwesenheitContainer() {
		return abwesenheitContainer;
	}

	public void setAbwesenheitContainer(AbwesenheitContainer abwesenheitContainer) {
		this.abwesenheitContainer = abwesenheitContainer;
	}

	@Override
	public int compareTo(Abwesenheit o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getGueltigkeit(), o.getGueltigkeit());
		builder.append(this.getId(), o.getId());
		return builder.toComparison();
	}

	public Abwesenheit copyForMutation(Abwesenheit mutation) {
		return (Abwesenheit) super.copyForMutation(mutation);
	}

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		//noinspection SimplifiableIfStatement
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		return super.isSame(other);
	}
}

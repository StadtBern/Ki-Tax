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

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

/**
 * Entity for a Tag of a Ferieninsel-Belegung
 */
@Audited
@Entity
public class BelegungFerieninselTag extends AbstractEntity implements Comparable<BelegungFerieninselTag> {

	private static final long serialVersionUID = 6815485579662587990L;

	@NotNull
	@Column(nullable = false)
	private LocalDate tag;


	public LocalDate getTag() {
		return tag;
	}

	public void setTag(LocalDate tag) {
		this.tag = tag;
	}

	@Override
	public boolean isSame(AbstractEntity other) {
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!(other instanceof BelegungFerieninselTag)) {
			return false;
		}
		BelegungFerieninselTag that = (BelegungFerieninselTag) other;
		return Objects.equals(tag, that.tag);
	}

	@Override
	public int compareTo(@Nonnull BelegungFerieninselTag other) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(this.getTag(), other.getTag());
		compareToBuilder.append(this.getId(), other.getId());  // wenn ids nicht gleich sind wollen wir auch compare to nicht gleich
		return compareToBuilder.toComparison();
	}

	@Nonnull
	public BelegungFerieninselTag copyForMutation(@Nonnull BelegungFerieninselTag mutation) {
		super.copyForMutation(mutation);
		mutation.setTag(LocalDate.from(tag));
		return mutation;
	}
}

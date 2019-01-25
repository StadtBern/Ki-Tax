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

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

/**
 * Entity fuer BetreuungsmitteilungPensum.
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
@SuppressWarnings("ComparableImplementedButEqualsNotOverridden")
@Audited
@Entity
public class BetreuungsmitteilungPensum extends AbstractPensumEntity implements Comparable<BetreuungsmitteilungPensum> {

	private static final long serialVersionUID = -9032858720574672370L;

	@ManyToOne(optional = false)
	@NotNull
	private Betreuungsmitteilung betreuungsmitteilung;

	@NotNull
	@Column(nullable = false)
	private Integer monatlicheMittagessen;

	public Betreuungsmitteilung getBetreuungsmitteilung() {
		return betreuungsmitteilung;
	}

	public void setBetreuungsmitteilung(Betreuungsmitteilung betreuungsmitteilung) {
		this.betreuungsmitteilung = betreuungsmitteilung;
	}

	public Integer getMonatlicheMittagessen() {
		return monatlicheMittagessen;
	}

	public void setMonatlicheMittagessen(Integer monatlicheMittagessen) {
		this.monatlicheMittagessen = monatlicheMittagessen;
	}

	@SuppressWarnings("Duplicates")
	@Override
	public int compareTo(@Nonnull BetreuungsmitteilungPensum o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getGueltigkeit(), o.getGueltigkeit());
		builder.append(this.getId(), o.getId());
		return builder.toComparison();
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
		if (!super.isSame(other)) {
			return false;
		}
		if (!(other instanceof BetreuungsmitteilungPensum)) {
			return false;
		}
		final BetreuungsmitteilungPensum otherBetreuungsmitteilungPensum = (BetreuungsmitteilungPensum) other;
		return getBetreuungsmitteilung().isSame(otherBetreuungsmitteilungPensum.getBetreuungsmitteilung())
			&& Objects.equals(getMonatlicheMittagessen(), ((BetreuungsmitteilungPensum) other).getMonatlicheMittagessen());
	}
}

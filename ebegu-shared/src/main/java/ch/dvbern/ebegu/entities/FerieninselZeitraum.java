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

import javax.annotation.Nonnull;
import javax.persistence.Entity;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

/**
 * Entity for a Zeitraum of a Ferieninsel
 */
@Audited
@Entity
public class FerieninselZeitraum extends AbstractDateRangedEntity implements Comparable<FerieninselZeitraum> {

	private static final long serialVersionUID = 2918865169295094143L;

	@Override
	public int compareTo(@Nonnull FerieninselZeitraum o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getGueltigkeit().getGueltigAb(), o.getGueltigkeit().getGueltigAb());
		builder.append(this.getGueltigkeit().getGueltigBis(), o.getGueltigkeit().getGueltigBis());
		return builder.toComparison();
	}
}

/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

import java.time.LocalDate;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlRootElement;

import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;

/**
 * Wrapper DTO fuer eine Berechtigung
 */
@XmlRootElement(name = "berechtigung")
public class JaxBerechtigung extends JaxAbstractDateRangedDTO {

	private static final long serialVersionUID = 2769899329796452129L;

	@Nonnull
	private UserRole role;

	@Nullable
	private JaxTraegerschaft traegerschaft;

	@Nullable
	private JaxInstitution institution;


	@Nonnull
	public UserRole getRole() {
		return role;
	}

	public void setRole(@Nonnull UserRole role) {
		this.role = role;
	}

	@Nullable
	public JaxTraegerschaft getTraegerschaft() {
		return traegerschaft;
	}

	public void setTraegerschaft(@Nullable JaxTraegerschaft traegerschaft) {
		this.traegerschaft = traegerschaft;
	}

	@Nullable
	public JaxInstitution getInstitution() {
		return institution;
	}

	public void setInstitution(@Nullable JaxInstitution institution) {
		this.institution = institution;
	}

	public boolean isSame(@Nonnull JaxBerechtigung that) {
		if (this == that) {
			return true;
		}
		return role == that.role &&
			Objects.equals(traegerschaft, that.traegerschaft) &&
			Objects.equals(institution, that.institution);
	}

	public boolean isGueltig() {
		LocalDate dateFrom = getGueltigAb() != null ? getGueltigAb() : Constants.START_OF_TIME;
		LocalDate dateUntil = getGueltigBis() != null ? getGueltigBis() : Constants.END_OF_TIME;
		DateRange dateRange = new DateRange(dateFrom, dateUntil);
		return dateRange.contains(LocalDate.now());
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof JaxBerechtigung)) {
			return false;
		}

		JaxBerechtigung other = (JaxBerechtigung) o;

		//noinspection NonFinalFieldReferenceInEquals
		return this.role == other.role
			&& Objects.equals(this.getGueltigAb(), other.getGueltigAb())
			&& Objects.equals(this.getGueltigBis(), other.getGueltigBis());
	}

	@Override
	public int hashCode() {
		return super.hashCode()
			+ Objects.hashCode(this.getRole())
			+ Objects.hashCode(this.getGueltigAb())
			+ Objects.hashCode(this.getGueltigBis());
	}

	@Override
	public int compareTo(@Nonnull JaxAbstractDTO o) {
		if (o instanceof JaxBerechtigung) {
			final JaxBerechtigung other = (JaxBerechtigung) o;
			if (getGueltigAb() != null && other.getGueltigAb() != null) {
				return getGueltigAb().compareTo(other.getGueltigAb());
			}
			if (getGueltigAb() == null && other.getGueltigAb() == null) {
				return 0;
			}
			if (getGueltigAb() == null && other.getGueltigAb() != null) {
				return 1;
			}
			return -1;
		}
		return super.compareTo(o);
	}
}

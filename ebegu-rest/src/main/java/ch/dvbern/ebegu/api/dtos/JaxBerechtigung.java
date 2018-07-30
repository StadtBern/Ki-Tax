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

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlRootElement;

import ch.dvbern.ebegu.enums.UserRole;

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

//	@Override
//	public int compareTo(@Nonnull JaxAbstractDTO o) {
//		if (o instanceof JaxBerechtigung) {
//			JaxBerechtigung jaxBerechtigung = (JaxBerechtigung) o;
//			CompareToBuilder builder = new CompareToBuilder();
//			builder.append(this.getGueltigAb(), jaxBerechtigung.getGueltigAb());
//			builder.append(this.getId(), jaxBerechtigung.getId());
//			return builder.toComparison();
//		}
//		return -1;
//	}

	public boolean isSame(@Nonnull JaxBerechtigung other) {

		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof JaxBerechtigung)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		JaxBerechtigung that = (JaxBerechtigung) o;
		return role == that.role &&
			Objects.equals(traegerschaft, that.traegerschaft) &&
			Objects.equals(institution, that.institution);
	}

	@Override
	public int hashCode() {

		return Objects.hash(super.hashCode(), role, traegerschaft, institution);
	}
}

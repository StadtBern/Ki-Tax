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

package ch.dvbern.ebegu.entities;

import java.time.LocalDate;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.enums.Amt;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.listener.BerechtigungChangedEntityListener;
import ch.dvbern.ebegu.validators.CheckBerechtigung;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

@Entity
@EntityListeners(BerechtigungChangedEntityListener.class)
@Audited
@CheckBerechtigung
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Berechtigung extends AbstractDateRangedEntity implements Comparable<Berechtigung> {

	private static final long serialVersionUID = 6372688971894279665L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_Berechtigung_benutzer_id"))
	private Benutzer benutzer;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@NotNull
	private UserRole role;

	@Nullable
	@ManyToOne(optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_Berechtigung_institution_id"))
	private Institution institution;

	@Nullable
	@ManyToOne(optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_Berechtigung_traegerschaft_id"))
	private Traegerschaft traegerschaft;


	public Benutzer getBenutzer() {
		return benutzer;
	}

	public void setBenutzer(Benutzer benutzer) {
		this.benutzer = benutzer;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	@Nullable
	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(@Nullable Institution institution) {
		this.institution = institution;
	}

	@Nullable
	public Traegerschaft getTraegerschaft() {
		return traegerschaft;
	}

	public void setTraegerschaft(@Nullable Traegerschaft traegerschaft) {
		this.traegerschaft = traegerschaft;
	}

	@Nonnull
	public Amt getAmt() {
		if (role != null) {
			return role.getAmt();
		}
		return Amt.NONE;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.appendSuper(super.toString())
			.append("gueltigkeit", getGueltigkeit())
			.append("role", role)
			.toString();
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
		if (!(other instanceof Berechtigung)) {
			return false;
		}
		final Berechtigung otherBerechtigung = (Berechtigung) other;
		return Objects.equals(getBenutzer(), otherBerechtigung.getBenutzer())
			&& Objects.equals(getRole(), otherBerechtigung.getRole())
			&& Objects.equals(getInstitution(), otherBerechtigung.getInstitution())
			&& Objects.equals(getTraegerschaft(), otherBerechtigung.getTraegerschaft())
			&& Objects.equals(getGueltigkeit(), otherBerechtigung.getGueltigkeit())
			&& Objects.equals(getBenutzer().getGesperrt(), otherBerechtigung.getBenutzer().getGesperrt());
	}

	@Override
	public int compareTo(Berechtigung o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getGueltigkeit().getGueltigAb(), o.getGueltigkeit().getGueltigAb());
		builder.append(this.getId(), o.getId());
		return builder.toComparison();
	}

	public boolean isGueltig() {
		return getGueltigkeit().contains(LocalDate.now());
	}

	public boolean isAbgelaufen() {
		return getGueltigkeit().endsBefore(LocalDate.now());
	}
}

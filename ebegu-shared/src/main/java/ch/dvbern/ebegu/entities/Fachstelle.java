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

import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entitaet zum Speichern von Fachstellen in der Datenbank.
 */
@Audited
@Entity
public class Fachstelle extends AbstractEntity {

	private static final long serialVersionUID = -7687613920281069860L;

	@Size(max = Constants.DB_DEFAULT_SHORT_LENGTH)
	@Nonnull
	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_SHORT_LENGTH)
	private String name;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String beschreibung;

	@Column(nullable = false)
	private boolean behinderungsbestaetigung;


	public Fachstelle() {
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Nullable
	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(@Nullable String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public boolean isBehinderungsbestaetigung() {
		return behinderungsbestaetigung;
	}

	public void setBehinderungsbestaetigung(boolean behinderungsbestaetigung) {
		this.behinderungsbestaetigung = behinderungsbestaetigung;
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
		if (!(other instanceof Fachstelle)) {
			return false;
		}
		final Fachstelle otherGesuchsteller = (Fachstelle) other;
		return Objects.equals(getName(), otherGesuchsteller.getName()) &&
			Objects.equals(getBeschreibung(), otherGesuchsteller.getBeschreibung()) &&
			Objects.equals(isBehinderungsbestaetigung(), otherGesuchsteller.isBehinderungsbestaetigung());
	}
}

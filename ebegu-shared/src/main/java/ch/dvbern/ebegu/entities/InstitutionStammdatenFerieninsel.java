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

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Entitaet zum Speichern von InstitutionStammdatenFerieninsel in der Datenbank.
 * Es hat 4 Felder, ein Feld pro Feriensequenz. Wir koennen davon ausgehen, dass die Ferien immer so bleiben, wie sie jetzt definiert sind,
 * deswegen kann man es statisch machen.
 */
@Audited
@Entity
public class InstitutionStammdatenFerieninsel extends AbstractEntity implements Comparable<InstitutionStammdatenFerieninsel> {

	private static final long serialVersionUID = 3991623541799162523L;

	@Nullable
	@Size(max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = true)
	private String ausweichstandortSommerferien;

	@Nullable
	@Size(max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = true)
	private String ausweichstandortHerbstferien;

	@Nullable
	@Size(max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = true)
	private String ausweichstandortSportferien;

	@Nullable
	@Size(max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = true)
	private String ausweichstandortFruehlingsferien;




	public InstitutionStammdatenFerieninsel() {
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
		if (!(other instanceof InstitutionStammdatenFerieninsel)) {
			return false;
		}
		return true;
	}


	@Override
	public int compareTo(InstitutionStammdatenFerieninsel o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getId(), o.getId());
		return builder.toComparison();
	}

	@Nullable
	public String getAusweichstandortSommerferien() {
		return ausweichstandortSommerferien;
	}

	public void setAusweichstandortSommerferien(@Nullable String ausweichstandortSommerferien) {
		this.ausweichstandortSommerferien = ausweichstandortSommerferien;
	}

	@Nullable
	public String getAusweichstandortHerbstferien() {
		return ausweichstandortHerbstferien;
	}

	public void setAusweichstandortHerbstferien(@Nullable String ausweichstandortHerbstferien) {
		this.ausweichstandortHerbstferien = ausweichstandortHerbstferien;
	}

	@Nullable
	public String getAusweichstandortSportferien() {
		return ausweichstandortSportferien;
	}

	public void setAusweichstandortSportferien(@Nullable String ausweichstandortSportferien) {
		this.ausweichstandortSportferien = ausweichstandortSportferien;
	}

	@Nullable
	public String getAusweichstandortFruehlingsferien() {
		return ausweichstandortFruehlingsferien;
	}

	public void setAusweichstandortFruehlingsferien(@Nullable String ausweichstandortFruehlingsferien) {
		this.ausweichstandortFruehlingsferien = ausweichstandortFruehlingsferien;
	}
}

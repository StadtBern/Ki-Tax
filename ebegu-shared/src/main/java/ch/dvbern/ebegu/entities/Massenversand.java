/*
 * Copyright (C) 2018 DV Bern AG, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ch.dvbern.ebegu.util.Constants;

/**
 * Entitaet zum Speichern von Massenversänden in der Datenbank.
 */
@Entity
public class Massenversand extends AbstractEntity {

	private static final long serialVersionUID = -7687613920281069860L;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String einstellungen;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String text;

	@ManyToMany
	private List<Gesuch> gesuche = new ArrayList<>();


	public Massenversand() {
	}

	public String getEinstellungen() {
		return einstellungen;
	}

	public void setEinstellungen(String einstellungen) {
		this.einstellungen = einstellungen;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Gesuch> getGesuche() {
		return gesuche;
	}

	public void setGesuche(List<Gesuch> gesuche) {
		this.gesuche = gesuche;
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
		if (!(other instanceof Massenversand)) {
			return false;
		}
		final Massenversand otherGesuchsteller = (Massenversand) other;
		return Objects.equals(getEinstellungen(), otherGesuchsteller.getEinstellungen()) &&
			Objects.equals(getText(), otherGesuchsteller.getText());
	}
}

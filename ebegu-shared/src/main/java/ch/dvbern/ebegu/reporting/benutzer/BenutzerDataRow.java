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

package ch.dvbern.ebegu.reporting.benutzer;

import java.time.LocalDate;

import javax.annotation.Nullable;

public class BenutzerDataRow {

	private String username;
	private String vorname;
	private String nachname;
	private String email;
	private String role;
	@Nullable
	private LocalDate roleGueltigAb;
	@Nullable
	private LocalDate roleGueltigBis;
	@Nullable
	private String institution;
	@Nullable
	private String traegerschaft;
	private Boolean gesperrt;
	private Boolean isKita;
	private Boolean isTageselternKleinkind;
	private Boolean isTageselternSchulkind;
	private Boolean isTagi;
	private Boolean isTagesschule;
	private Boolean isFerieninsel;


	public BenutzerDataRow() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Nullable
	public LocalDate getRoleGueltigAb() {
		return roleGueltigAb;
	}

	public void setRoleGueltigAb(@Nullable LocalDate roleGueltigAb) {
		this.roleGueltigAb = roleGueltigAb;
	}

	@Nullable
	public LocalDate getRoleGueltigBis() {
		return roleGueltigBis;
	}

	public void setRoleGueltigBis(@Nullable LocalDate roleGueltigBis) {
		this.roleGueltigBis = roleGueltigBis;
	}

	@Nullable
	public String getInstitution() {
		return institution;
	}

	public void setInstitution(@Nullable String institution) {
		this.institution = institution;
	}

	@Nullable
	public String getTraegerschaft() {
		return traegerschaft;
	}

	public void setTraegerschaft(@Nullable String traegerschaft) {
		this.traegerschaft = traegerschaft;
	}

	public Boolean isGesperrt() {
		return gesperrt;
	}

	public void setGesperrt(Boolean gesperrt) {
		this.gesperrt = gesperrt;
	}

	public Boolean isKita() {
		return isKita;
	}

	public void setKita(Boolean isKita) {
		this.isKita = isKita;
	}

	public Boolean isTageselternKleinkind() {
		return isTageselternKleinkind;
	}

	public void setTageselternKleinkind(Boolean tageselternKleinkind) {
		isTageselternKleinkind = tageselternKleinkind;
	}

	public Boolean isTageselternSchulkind() {
		return isTageselternSchulkind;
	}

	public void setTageselternSchulkind(Boolean tageselternSchulkind) {
		isTageselternSchulkind = tageselternSchulkind;
	}

	public Boolean isTagi() {
		return isTagi;
	}

	public void setTagi(Boolean tagi) {
		isTagi = tagi;
	}

	public Boolean isTagesschule() {
		return isTagesschule;
	}

	public void setTagesschule(Boolean tagesschule) {
		isTagesschule = tagesschule;
	}

	public Boolean isFerieninsel() {
		return isFerieninsel;
	}

	public void setFerieninsel(Boolean ferieninsel) {
		isFerieninsel = ferieninsel;
	}

	public Boolean isJugendamt() {
		return (this.isKita != null && this.isKita)
			|| (this.isTageselternKleinkind != null && this.isTageselternKleinkind)
			|| (this.isTageselternSchulkind != null && this.isTageselternSchulkind)
			|| (this.isTagi != null && this.isTagi);
	}

	public Boolean isSchulamt() {
		return (this.isTagesschule != null && this.isTagesschule)
			|| (this.isFerieninsel != null && this.isFerieninsel);
	}
}

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

package ch.dvbern.ebegu.reporting.massenversand;

import java.time.LocalDate;

import javax.annotation.Nonnull;

import com.google.common.base.Strings;

/**
 * DTO fuer ein Kind einer Familie eines Massenversands
 */
public class MassenversandRepeatKindDataCol {

	private String kindName;
	private String kindVorname;
	private LocalDate kindGeburtsdatum;
	private String kindDubletten;
	private String kindInstitutionKita;
	private String kindInstitutionTagi;
	private String kindInstitutionTeKleinkind;
	private String kindInstitutionTeSchulkind;
	private String kindInstitutionTagesschule;
	private String kindInstitutionFerieninsel;
	private String kindInstitutionenWeitere;

	public String getKindName() {
		return kindName;
	}

	public void setKindName(String kindName) {
		this.kindName = kindName;
	}

	public String getKindVorname() {
		return kindVorname;
	}

	public void setKindVorname(String kindVorname) {
		this.kindVorname = kindVorname;
	}

	public LocalDate getKindGeburtsdatum() {
		return kindGeburtsdatum;
	}

	public void setKindGeburtsdatum(LocalDate kindGeburtsdatum) {
		this.kindGeburtsdatum = kindGeburtsdatum;
	}

	public String getKindDubletten() {
		return kindDubletten;
	}

	public void setKindDubletten(String kindDubletten) {
		this.kindDubletten = kindDubletten;
	}

	public String getKindInstitutionKita() {
		return kindInstitutionKita;
	}

	public void setKindInstitutionKita(String kindInstitutionKita) {
		this.kindInstitutionKita = kindInstitutionKita;
	}

	public String getKindInstitutionTagi() {
		return kindInstitutionTagi;
	}

	public void setKindInstitutionTagi(String kindInstitutionTagi) {
		this.kindInstitutionTagi = kindInstitutionTagi;
	}

	public String getKindInstitutionTeKleinkind() {
		return kindInstitutionTeKleinkind;
	}

	public void setKindInstitutionTeKleinkind(String kindInstitutionTeKleinkind) {
		this.kindInstitutionTeKleinkind = kindInstitutionTeKleinkind;
	}

	public String getKindInstitutionTeSchulkind() {
		return kindInstitutionTeSchulkind;
	}

	public void setKindInstitutionTeSchulkind(String kindInstitutionTeSchulkind) {
		this.kindInstitutionTeSchulkind = kindInstitutionTeSchulkind;
	}

	public String getKindInstitutionTagesschule() {
		return kindInstitutionTagesschule;
	}

	public void setKindInstitutionTagesschule(String kindInstitutionTagesschule) {
		this.kindInstitutionTagesschule = kindInstitutionTagesschule;
	}

	public String getKindInstitutionFerieninsel() {
		return kindInstitutionFerieninsel;
	}

	public void setKindInstitutionFerieninsel(String kindInstitutionFerieninsel) {
		this.kindInstitutionFerieninsel = kindInstitutionFerieninsel;
	}

	public String getKindInstitutionenWeitere() {
		return kindInstitutionenWeitere;
	}

	public void setKindInstitutionenWeitere(String kindInstitutionenWeitere) {
		this.kindInstitutionenWeitere = kindInstitutionenWeitere;
	}

	public void addKindDubletten(@Nonnull String dublette) {
		setKindDubletten(
			Strings.isNullOrEmpty(getKindDubletten())
				? dublette
				: getKindDubletten() + ", " + dublette
		);
	}

	private void addKindInstitutionenWeitere(@Nonnull String instName) {
		setKindInstitutionenWeitere(
			Strings.isNullOrEmpty(getKindInstitutionenWeitere())
				? instName
				: getKindInstitutionenWeitere() + ", " + instName
		);
	}

	public void setKindInstitutionKitaOrWeitere(@Nonnull String instName) {
		if (Strings.isNullOrEmpty(getKindInstitutionKita())) {
			setKindInstitutionKita(instName);
		} else {
			addKindInstitutionenWeitere(instName);
		}
	}

	public void setKindInstitutionTagiOrWeitere(@Nonnull String instName) {
		if (Strings.isNullOrEmpty(getKindInstitutionTagi())) {
			setKindInstitutionTagi(instName);
		} else {
			addKindInstitutionenWeitere(instName);
		}
	}

	public void setKindInstitutionTeKleinkindOrWeitere(@Nonnull String instName) {
		if (Strings.isNullOrEmpty(getKindInstitutionTeKleinkind())) {
			setKindInstitutionTeKleinkind(instName);
		} else {
			addKindInstitutionenWeitere(instName);
		}
	}

	public void setKindInstitutionTeSchulkindOrWeitere(@Nonnull String instName) {
		if (Strings.isNullOrEmpty(getKindInstitutionTeSchulkind())) {
			setKindInstitutionTeSchulkind(instName);
		} else {
			addKindInstitutionenWeitere(instName);
		}
	}

	public void setKindInstitutionTagesschuleOrWeitere(@Nonnull String instName) {
		if (Strings.isNullOrEmpty(getKindInstitutionTagesschule())) {
			setKindInstitutionTagesschule(instName);
		} else {
			addKindInstitutionenWeitere(instName);
		}
	}

	public void setKindInstitutionFerieninselOrWeitere(@Nonnull String instName) {
		if (Strings.isNullOrEmpty(getKindInstitutionFerieninsel())) {
			setKindInstitutionFerieninsel(instName);
		} else {
			addKindInstitutionenWeitere(instName);
		}
	}
}

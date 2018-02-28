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

package ch.dvbern.ebegu.util.testdata;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Konfiguration fuer die Erstellung einer Mutation
 */
public class MutationConfig {

	private boolean betreuungenBestaetigt;
	private boolean verfuegt;
	private LocalDate eingangsdatum;
	private boolean ignorierenInZahlungslauf;

	@Nullable
	private Integer erwerbspensum;

	private LocalDateTime timestampVerfuegt;

	private MutationConfig() {
	}

	public static MutationConfig createEmptyMutationVerfuegt(@Nonnull LocalDate eingangsdatum, @Nonnull LocalDateTime timestampVerfuegt) {
		MutationConfig config = new MutationConfig();
		config.setBetreuungenBestaetigt(true); 		// Da verfuegt, muss es auch bestaetigt sein
		config.setVerfuegt(true);
		config.setTimestampVerfuegt(timestampVerfuegt);
		config.setEingangsdatum(eingangsdatum); 	// Zwingend beim verfuegen
		config.setIgnorierenInZahlungslauf(false); 	// Wir haben keine Aenderungen. Darum kann auch nichts ignoriert werden
		config.setErwerbspensum(null);				// Null bedeutet keine Änderung
		return config;
	}

	public static MutationConfig createMutationVerfuegt(@Nonnull LocalDate eingangsdatum, @Nonnull LocalDateTime timestampVerfuegt, @Nonnull Integer erwerbspensum, boolean ignorierenInZahlungslauf) {
		MutationConfig config = new MutationConfig();
		config.setBetreuungenBestaetigt(true); 		// Da verfuegt, muss es auch bestaetigt sein
		config.setVerfuegt(true);
		config.setTimestampVerfuegt(timestampVerfuegt);
		config.setEingangsdatum(eingangsdatum); 	// Zwingend beim verfuegen
		config.setIgnorierenInZahlungslauf(ignorierenInZahlungslauf);
		config.setErwerbspensum(erwerbspensum);
		return config;
	}


	public boolean isBetreuungenBestaetigt() {
		return betreuungenBestaetigt;
	}

	public void setBetreuungenBestaetigt(boolean betreuungenBestaetigt) {
		this.betreuungenBestaetigt = betreuungenBestaetigt;
	}

	public boolean isVerfuegt() {
		return verfuegt;
	}

	public void setVerfuegt(boolean verfuegt) {
		this.verfuegt = verfuegt;
	}

	public LocalDate getEingangsdatum() {
		return eingangsdatum;
	}

	public void setEingangsdatum(LocalDate eingangsdatum) {
		this.eingangsdatum = eingangsdatum;
	}

	public boolean isIgnorierenInZahlungslauf() {
		return ignorierenInZahlungslauf;
	}

	public void setIgnorierenInZahlungslauf(boolean ignorierenInZahlungslauf) {
		this.ignorierenInZahlungslauf = ignorierenInZahlungslauf;
	}

	@Nullable
	public Integer getErwerbspensum() {
		return erwerbspensum;
	}

	public void setErwerbspensum(@Nullable Integer erwerbspensum) {
		this.erwerbspensum = erwerbspensum;
	}

	public LocalDateTime getTimestampVerfuegt() {
		return timestampVerfuegt;
	}

	public void setTimestampVerfuegt(LocalDateTime timestampVerfuegt) {
		this.timestampVerfuegt = timestampVerfuegt;
	}
}

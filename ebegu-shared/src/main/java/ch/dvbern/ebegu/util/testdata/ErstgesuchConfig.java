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

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.util.TestfallName;

/**
 * Konfiguration fuer die Erstellung eines Erstgesuchs
 */
public class ErstgesuchConfig {

	private Gesuchsperiode gesuchsperiode;
	private TestfallName testfallName;
	private boolean betreuungenBestaetigt;
	private boolean verfuegt;
	private LocalDate eingangsdatum;
	private LocalDateTime timestampVerfuegt;

	private ErstgesuchConfig() {
	}

	public static ErstgesuchConfig createErstgesuchVerfuegt(@Nonnull TestfallName testfallName, @Nonnull LocalDate eingangsdatum, @Nonnull LocalDateTime
		timestampVerfuegt) {
		ErstgesuchConfig config = new ErstgesuchConfig();
		config.setTestfallName(testfallName);
		config.setEingangsdatum(eingangsdatum);
		config.setBetreuungenBestaetigt(true);
		config.setVerfuegt(true);
		config.setTimestampVerfuegt(timestampVerfuegt);
		return config;
	}

	public static ErstgesuchConfig createErstgesuchVerfuegt(@Nonnull TestfallName testfallName, @Nonnull Gesuchsperiode gesuchsperiode, @Nonnull LocalDate
		eingangsdatum, @Nonnull LocalDateTime
		timestampVerfuegt) {
		ErstgesuchConfig config = new ErstgesuchConfig();
		config.setTestfallName(testfallName);
		config.setGesuchsperiode(gesuchsperiode);
		config.setEingangsdatum(eingangsdatum);
		config.setBetreuungenBestaetigt(true);
		config.setVerfuegt(true);
		config.setTimestampVerfuegt(timestampVerfuegt);
		return config;
	}

	public Gesuchsperiode getGesuchsperiode() {
		return gesuchsperiode;
	}

	public void setGesuchsperiode(Gesuchsperiode gesuchsperiode) {
		this.gesuchsperiode = gesuchsperiode;
	}

	public TestfallName getTestfallName() {
		return testfallName;
	}

	public void setTestfallName(TestfallName testfallName) {
		this.testfallName = testfallName;
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

	public LocalDateTime getTimestampVerfuegt() {
		return timestampVerfuegt;
	}

	public void setTimestampVerfuegt(LocalDateTime timestampVerfuegt) {
		this.timestampVerfuegt = timestampVerfuegt;
	}
}

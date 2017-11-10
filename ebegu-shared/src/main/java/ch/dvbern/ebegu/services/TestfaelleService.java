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

package ch.dvbern.ebegu.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.testfaelle.AbstractTestfall;

/**
 * Service fuer erstellen und mutieren von Testfällen
 */
public interface TestfaelleService {

	String WAELTI_DAGMAR = "1";
	String FEUTZ_IVONNE = "2";
	String PERREIRA_MARCIA = "3";
	String WALTHER_LAURA = "4";
	String LUETHI_MERET = "5";
	String BECKER_NORA = "6";
	String MEIER_MERET = "7";
	String UMZUG_AUS_IN_AUS_BERN = "8";
	String ABWESENHEIT = "9";
	String UMZUG_VOR_GESUCHSPERIODE = "10";
	String ASIV1 = "ASIV1";
	String ASIV2 = "ASIV2";
	String ASIV3 = "ASIV3";
	String ASIV4 = "ASIV4";
	String ASIV5 = "ASIV5";
	String ASIV6 = "ASIV6";
	String ASIV7 = "ASIV7";
	String ASIV8 = "ASIV8";
	String ASIV9 = "ASIV9";
	String ASIV10 = "ASIV10";

	String heirat = "1";

	@Nonnull
	StringBuilder createAndSaveTestfaelle(@Nonnull String fallid,
		boolean betreuungenBestaetigt,
		boolean verfuegen, @Nullable String gesuchsPeriodeId);

	@Nonnull
	StringBuilder createAndSaveAsOnlineGesuch(@Nonnull String fallid,
		boolean betreuungenBestaetigt,
		boolean verfuegen,
		@Nonnull String username, @Nullable String gesuchsPeriodeId);

	@Nullable
	Gesuch createAndSaveTestfaelle(@Nonnull String fallid,
		boolean betreuungenBestaetigt,
		boolean verfuegen);

	@Nullable
	Gesuch mutierenHeirat(@Nonnull Long fallNummer,
		@Nonnull String gesuchsperiodeId,
		@Nonnull LocalDate eingangsdatum, @Nonnull LocalDate aenderungPer, boolean verfuegen);

	@Nonnull
	Gesuch mutierenFinSit(@Nonnull Long fallNummer, @Nonnull String gesuchsperiodeId,
							@Nonnull LocalDate eingangsdatum, @Nonnull LocalDate aenderungPer, boolean verfuegen,
							BigDecimal nettoLohn, boolean ignorieren);

	@Nullable
	Gesuch mutierenScheidung(@Nonnull Long fallNummer,
		@Nonnull String gesuchsperiodeId,
		@Nonnull LocalDate eingangsdatum, @Nonnull LocalDate aenderungPer, boolean verfuegen);

	/**
	 * loescht alle Gesuche des Gesuchstellers mit dem gegebenen Namen
	 *
	 * @param username Username des Besitzers der Gesuche die entferntw erden sollen
	 */
	void removeGesucheOfGS(@Nonnull String username);

	/**
	 * Gibt die Institutionsstammdaten zurück, welche in den gelieferten Testfällen verwendet werden,
	 * also Brünnen und Weissenstein Kita und Tagi
	 */
	@Nonnull
	List<InstitutionStammdaten> getInstitutionsstammdatenForTestfaelle();

	@Nonnull
	Gesuch createAndSaveGesuch(@Nonnull AbstractTestfall fromTestfall, boolean verfuegen, @Nullable Benutzer besitzer);

	void gesuchVerfuegenUndSpeichern(boolean verfuegen, @Nonnull Gesuch gesuch, boolean mutation, boolean ignorierenInZahlungslauf);
}

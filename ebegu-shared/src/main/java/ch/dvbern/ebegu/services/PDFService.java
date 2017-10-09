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

import java.time.LocalDate;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.enums.Zustelladresse;
import ch.dvbern.ebegu.errors.MergeDocException;

public interface PDFService {

	@Nonnull
	byte[] generateNichteintreten(Betreuung betreuung, boolean writeProtected) throws MergeDocException;

	@Nonnull
	byte[] generateMahnung(Mahnung mahnung, Optional<Mahnung> vorgaengerMahnung, boolean writeProtected) throws MergeDocException;

	@Nonnull
	byte[] generateFreigabequittung(Gesuch gesuch, Zustelladresse zustelladresse, boolean writeProtected) throws MergeDocException;

	@Nonnull
	byte[] generateBegleitschreiben(@Nonnull Gesuch gesuch, boolean writeProtected) throws MergeDocException;

	@Nonnull
	byte[] generateFinanzielleSituation(@Nonnull Gesuch gesuch, Verfuegung famGroessenVerfuegung, boolean writeProtected) throws MergeDocException;

	@Nonnull
	byte[] generateVerfuegungForBetreuung(Betreuung betreuung, @Nullable LocalDate letzteVerfuegungDatum, boolean writeProtected) throws MergeDocException;

}

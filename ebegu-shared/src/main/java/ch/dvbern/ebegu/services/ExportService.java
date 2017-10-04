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

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.dto.dataexport.v1.VerfuegungenExportDTO;
import ch.dvbern.ebegu.util.UploadFileInfo;

/**
 * Service to export Verfuegungen for usage in other applications
 */
public interface ExportService {

	/**
	 * exports all existing Verfuegungen of the Betreuungen of a single Gesuch
	 *
	 * @param antragId ID of the Gesuch or Mutation
	 * @return DTO containing containing a list of verfuegungen belonging to the given antragId
	 */
	@Nonnull
	VerfuegungenExportDTO exportAllVerfuegungenOfAntrag(@Nonnull String antragId);

	/**
	 * exports all existing Verfuegungen of a single Betreuung
	 *
	 * @param antragId ID of the Betreuung
	 * @return DTO containing containing a list of verfuegungen belonging to the given betreuung
	 */
	@Nonnull
	VerfuegungenExportDTO exportVerfuegungOfBetreuung(String betreuungID);

	/**
	 * prepares a file containing a VerfuegungenExportDTO marshalled as JSON which contains the information
	 * of the verfuegung of the given betreuungId
	 *
	 * @param betreuungID ID of the Betreuung that should be exported
	 * @return All Information needed to download the generated file (i.e. the accessToken)
	 */
	UploadFileInfo exportVerfuegungOfBetreuungAsFile(String betreuungID);
}

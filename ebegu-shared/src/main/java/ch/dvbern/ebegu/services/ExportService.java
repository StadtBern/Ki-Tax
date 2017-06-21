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
	 * @param antragId ID of the Gesuch or Mutation
	 * @return DTO containing containing a list of verfuegungen belonging to the given antragId
	 */
	@Nonnull
	VerfuegungenExportDTO exportAllVerfuegungenOfAntrag(@Nonnull String antragId);

	/**
	 * exports all existing Verfuegungen of a single Betreuung
	 * @param antragId ID of the Betreuung
	 * @return DTO containing containing a list of verfuegungen belonging to the given betreuung
	 */
	@Nonnull
	VerfuegungenExportDTO exportVerfuegungOfBetreuung(String betreuungID);

	/**
	 * prepares a file containing a VerfuegungenExportDTO marshalled as JSON which contains the information
	 * of the verfuegung of the given betreuungId
	 * @param betreuungID ID of the Betreuung that should be exported
	 * @return All Information needed to download the generated file (i.e. the accessToken)
	 */
	UploadFileInfo exportVerfuegungOfBetreuungAsFile(String betreuungID);
}

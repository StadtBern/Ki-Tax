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

package ch.dvbern.ebegu.services;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.entities.Massenversand;
import ch.dvbern.ebegu.enums.reporting.ReportVorlage;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.reporting.ReportMassenversandService;
import ch.dvbern.ebegu.reporting.massenversand.MassenversandDataRow;
import ch.dvbern.ebegu.reporting.massenversand.MassenversandExcelConverter;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.oss.lib.excelmerger.ExcelMergeException;
import ch.dvbern.oss.lib.excelmerger.ExcelMerger;
import ch.dvbern.oss.lib.excelmerger.ExcelMergerDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jboss.ejb3.annotation.TransactionTimeout;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.ADMINISTRATOR_SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

@SuppressWarnings("unchecked")
@Stateless
@Local(ReportMassenversandService.class)
public class ReportMassenversandServiceBean extends AbstractReportServiceBean implements ReportMassenversandService {

	private static final char SEPARATOR = ';';

	private MassenversandExcelConverter massenversandExcelConverter = new MassenversandExcelConverter();

	@Inject
	private FileSaverService fileSaverService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private GesuchService gesuchService;


	@Nonnull
	@Override
	public List<MassenversandDataRow> getReportMassenversand(
		@Nonnull LocalDate datumVon,
		@Nonnull LocalDate datumBis,
		@Nonnull String gesuchPeriodeID,
		boolean inklBgGesuche,
		boolean inklMischGesuche,
		boolean inklTsGesuche,
		boolean ohneErneuerungsgesuch,
		@Nullable String text
	) {

		List<Gesuch> ermittelteGesuche = gesuchService.getGepruefteFreigegebeneGesucheForGesuchsperiode(
			datumVon,
			datumBis,
			gesuchPeriodeID,
			inklBgGesuche,
			inklMischGesuche,
			inklTsGesuche,
			ohneErneuerungsgesuch
		);
		// Wenn ein Text eingegeben wurde, wird der Massenversand gespeichert
		if (StringUtils.isNotEmpty(text) && !ermittelteGesuche.isEmpty()) {
			saveMassenversand(
				datumVon,
				datumBis,
				gesuchPeriodeID,
				inklBgGesuche,
				inklMischGesuche,
				inklTsGesuche,
				ohneErneuerungsgesuch,
				text,
				ermittelteGesuche);
		}
		//TODO Die echten Daten ermitteln!
		final List<MassenversandDataRow> reportDataMassenversand = new ArrayList<>();
		return reportDataMassenversand;
	}

	private void saveMassenversand(
		@Nonnull LocalDate datumVon,
		@Nonnull LocalDate datumBis,
		@Nullable String gesuchPeriodeID,
		boolean inklBgGesuche,
		boolean inklMischGesuche,
		boolean inklTsGesuche,
		boolean ohneErneuerungsgesuch,
		@Nonnull String text,
		@Nonnull List<Gesuch> gesuche
	) {
		Massenversand massenversand = new Massenversand();
		massenversand.setText(text);
		@SuppressWarnings("StringConcatenationMissingWhitespace")
		String einstellungen = Constants.DATE_FORMATTER.format(datumVon) + SEPARATOR
			+ Constants.DATE_FORMATTER.format(datumBis) + SEPARATOR
			+ gesuchPeriodeID + SEPARATOR
			+ inklBgGesuche + SEPARATOR
			+ inklMischGesuche + SEPARATOR
			+ inklTsGesuche + SEPARATOR
			+ ohneErneuerungsgesuch + SEPARATOR;
		massenversand.setEinstellungen(einstellungen);
		massenversand.setGesuche(gesuche);
		gesuchService.createMassenversand(massenversand);
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, ADMINISTRATOR_SCHULAMT })
	@TransactionTimeout(value = Constants.STATISTIK_TIMEOUT_MINUTES, unit = TimeUnit.MINUTES)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public UploadFileInfo generateExcelReportMassenversand(
		@Nonnull LocalDate datumVon,
		@Nonnull LocalDate datumBis,
		@Nonnull String gesuchPeriodeId,
		boolean inklBgGesuche,
		boolean inklMischGesuche,
		boolean inklTsGesuche,
		boolean ohneErneuerungsgesuch,
		@Nullable String text
	) throws ExcelMergeException {

		final ReportVorlage reportVorlage = ReportVorlage.VORLAGE_REPORT_MASSENVERSAND;

		InputStream is = ReportMassenversandServiceBean.class.getResourceAsStream(reportVorlage.getTemplatePath());
		Validate.notNull(is, VORLAGE + reportVorlage.getTemplatePath() + NICHT_GEFUNDEN);

		Workbook workbook = ExcelMerger.createWorkbookFromTemplate(is);
		Sheet sheet = workbook.getSheet(reportVorlage.getDataSheetName());

		List<MassenversandDataRow> reportData = getReportMassenversand(
			datumVon, datumBis, gesuchPeriodeId, inklBgGesuche, inklMischGesuche, inklTsGesuche,
			ohneErneuerungsgesuch, text);

		Optional<Gesuchsperiode> gesuchsperiodeOptional = gesuchsperiodeService.findGesuchsperiode(gesuchPeriodeId);
		Gesuchsperiode gesuchsperiode = gesuchsperiodeOptional.orElseThrow(() ->
			new EbeguEntityNotFoundException("findGesuch", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchPeriodeId)
		);

		ExcelMergerDTO excelMergerDTO = massenversandExcelConverter.toExcelMergerDTO(
			reportData,
			Locale.getDefault(),
			datumVon,
			datumBis,
			gesuchsperiode,
			inklBgGesuche,
			inklMischGesuche,
			inklTsGesuche,
			ohneErneuerungsgesuch,
			text);

		mergeData(sheet, excelMergerDTO, reportVorlage.getMergeFields());
		massenversandExcelConverter.applyAutoSize(sheet);

		byte[] bytes = createWorkbook(workbook);

		return fileSaverService.save(bytes,
			reportVorlage.getDefaultExportFilename(),
			Constants.TEMP_REPORT_FOLDERNAME,
			getContentTypeForExport());
	}
}

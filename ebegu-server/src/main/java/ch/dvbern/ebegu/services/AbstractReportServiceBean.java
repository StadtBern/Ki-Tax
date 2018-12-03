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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;

import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.oss.lib.excelmerger.ExcelMergeException;
import ch.dvbern.oss.lib.excelmerger.ExcelMerger;
import ch.dvbern.oss.lib.excelmerger.ExcelMergerDTO;
import ch.dvbern.oss.lib.excelmerger.mergefields.MergeField;
import ch.dvbern.oss.lib.excelmerger.mergefields.MergeFieldProvider;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import static ch.dvbern.ebegu.util.MonitoringUtil.monitor;

public abstract class AbstractReportServiceBean extends AbstractBaseService {

	protected static final String VALIDIERUNG_STICHTAG = "Das Argument 'stichtag' darf nicht leer sein";
	protected static final String VALIDIERUNG_DATUM_VON = "Das Argument 'datumVon' darf nicht leer sein";
	protected static final String VALIDIERUNG_DATUM_BIS = "Das Argument 'datumBis' darf nicht leer sein";
	protected static final String NICHT_GEFUNDEN = "' nicht gefunden";
	protected static final String VORLAGE = "Vorlage '";
	protected static final String MIME_TYPE_EXCEL = "application/vnd.ms-excel";

	protected void validateDateParams(Object datumVon, Object datumBis) {
		Validate.notNull(datumVon, VALIDIERUNG_DATUM_VON);
		Validate.notNull(datumBis, VALIDIERUNG_DATUM_BIS);
	}

	protected void validateStichtagParam(LocalDate stichtag) {
		Validate.notNull(stichtag, VALIDIERUNG_STICHTAG);
	}

	@Nonnull
	protected MimeType getContentTypeForExport() {
		try {
			return new MimeType(MIME_TYPE_EXCEL);
		} catch (MimeTypeParseException e) {
			throw new EbeguRuntimeException("getContentTypeForExport", "could not parse mime type", e, MIME_TYPE_EXCEL);
		}
	}

	protected byte[] createWorkbook(@Nonnull Workbook workbook) {
		byte[] bytes;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			baos.flush();
			bytes = baos.toByteArray();

		} catch (IOException | RuntimeException e) {
			throw new IllegalStateException("Error creating workbook", e);
		}
		return bytes;
	}

	protected void mergeData(@Nonnull Sheet sheet, @Nonnull ExcelMergerDTO excelMergerDTO, @Nonnull MergeFieldProvider[] mergeFieldProviders) throws ExcelMergeException {
		List<MergeField<?>> mergeFields = MergeFieldProvider.toMergeFields(mergeFieldProviders);
		monitor(AbstractReportServiceBean.class, String.format("mergeData (sheet=%s)", sheet.getSheetName()),
			() -> ExcelMerger.mergeData(sheet, mergeFields, excelMergerDTO));
	}
}

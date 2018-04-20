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
package ch.dvbern.ebegu.reporting.gesuchstichtag;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;

import org.apache.poi.ss.usermodel.Sheet;

import ch.dvbern.ebegu.enums.reporting.MergeFieldGesuchStichtag;
import ch.dvbern.oss.lib.excelmerger.ExcelConverter;
import ch.dvbern.oss.lib.excelmerger.ExcelMergerDTO;

import static com.google.common.base.Preconditions.checkNotNull;

@Dependent
public class GeuschStichtagExcelConverter implements ExcelConverter {

	@Override
	public void applyAutoSize(@Nonnull Sheet sheet) {
		sheet.autoSizeColumn(0); // bgNummer
		sheet.autoSizeColumn(1); // institution
		sheet.autoSizeColumn(2); // betreuungsTyp
		sheet.autoSizeColumn(3); // periode
	}

	@Nonnull
	public ExcelMergerDTO toExcelMergerDTO(@Nonnull List<GesuchStichtagDataRow> data, @Nonnull Locale lang) {
		checkNotNull(data);

		ExcelMergerDTO sheet = new ExcelMergerDTO();

		data.forEach(dataRow -> {
			ExcelMergerDTO excelRowGroup = sheet.createGroup(MergeFieldGesuchStichtag.repeatGesuchStichtagRow);
			excelRowGroup.addValue(MergeFieldGesuchStichtag.bgNummer, dataRow.getBgNummer());
			excelRowGroup.addValue(MergeFieldGesuchStichtag.gesuchLaufNr, dataRow.getGesuchLaufNr());
			excelRowGroup.addValue(MergeFieldGesuchStichtag.institution, dataRow.getInstitution());
			excelRowGroup.addValue(MergeFieldGesuchStichtag.betreuungsTyp, dataRow.getBetreuungsTyp());
			excelRowGroup.addValue(MergeFieldGesuchStichtag.periode, dataRow.getPeriode());
			excelRowGroup.addValue(MergeFieldGesuchStichtag.nichtFreigegeben, dataRow.getNichtFreigegeben());
			excelRowGroup.addValue(MergeFieldGesuchStichtag.mahnungen, dataRow.getMahnungen());
			excelRowGroup.addValue(MergeFieldGesuchStichtag.beschwerde, dataRow.getBeschwerde());
		});

		return sheet;
	}
}

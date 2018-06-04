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

package ch.dvbern.ebegu.reporting.benutzer;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;

import ch.dvbern.ebegu.enums.reporting.MergeFieldBenutzer;
import ch.dvbern.oss.lib.excelmerger.ExcelConverter;
import ch.dvbern.oss.lib.excelmerger.ExcelMergerDTO;
import org.apache.poi.ss.usermodel.Sheet;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Excel Converter fuer die Statistik von Benutzern
 */
@Dependent
public class BenutzerExcelConverter implements ExcelConverter {

	@Override
	public void applyAutoSize(@Nonnull Sheet sheet) {
	}

	@Nonnull
	public ExcelMergerDTO toExcelMergerDTO(@Nonnull List<BenutzerDataRow> data, @Nonnull Locale lang) {
		checkNotNull(data);

		ExcelMergerDTO sheet = new ExcelMergerDTO();
		sheet.addValue(MergeFieldBenutzer.stichtag, LocalDate.now());

		data.forEach(dataRow -> {
			ExcelMergerDTO excelRowGroup = sheet.createGroup(MergeFieldBenutzer.repeatBenutzerRow);
			excelRowGroup.addValue(MergeFieldBenutzer.nachname, dataRow.getNachname());
			excelRowGroup.addValue(MergeFieldBenutzer.vorname, dataRow.getVorname());
			excelRowGroup.addValue(MergeFieldBenutzer.username, dataRow.getUsername());
			excelRowGroup.addValue(MergeFieldBenutzer.email, dataRow.getEmail());
			excelRowGroup.addValue(MergeFieldBenutzer.role, dataRow.getRole());
			excelRowGroup.addValue(MergeFieldBenutzer.roleGueltigBis, dataRow.getRoleGueltigBis());
			excelRowGroup.addValue(MergeFieldBenutzer.institution, dataRow.getInstitution());
			excelRowGroup.addValue(MergeFieldBenutzer.traegerschaft, dataRow.getTraegerschaft());
			excelRowGroup.addValue(MergeFieldBenutzer.gesperrt, dataRow.isGesperrt());

			excelRowGroup.addValue(MergeFieldBenutzer.isKita, dataRow.isKita());
			excelRowGroup.addValue(MergeFieldBenutzer.isTageselternKleinkind, dataRow.isTageselternKleinkind());
			excelRowGroup.addValue(MergeFieldBenutzer.isTageselternSchulkind, dataRow.isTageselternSchulkind());
			excelRowGroup.addValue(MergeFieldBenutzer.isTagi, dataRow.isTagi());
			excelRowGroup.addValue(MergeFieldBenutzer.isTagesschule, dataRow.isTagesschule());
			excelRowGroup.addValue(MergeFieldBenutzer.isFerieninsel, dataRow.isFerieninsel());

			excelRowGroup.addValue(MergeFieldBenutzer.isJugendamt, dataRow.isJugendamt());
			excelRowGroup.addValue(MergeFieldBenutzer.isSchulamt, dataRow.isSchulamt());
		});
		return sheet;
	}
}

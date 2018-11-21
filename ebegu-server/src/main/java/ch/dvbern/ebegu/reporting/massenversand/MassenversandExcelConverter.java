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
package ch.dvbern.ebegu.reporting.massenversand;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.reporting.MergeFieldMassenversand;
import ch.dvbern.oss.lib.excelmerger.ExcelConverter;
import ch.dvbern.oss.lib.excelmerger.ExcelMergerDTO;
import com.google.common.base.Preconditions;
import org.apache.poi.ss.usermodel.Sheet;

import static com.google.common.base.Preconditions.checkNotNull;

//@Dependent
public class MassenversandExcelConverter implements ExcelConverter {

	public static final String EMPTY_STRING = "";
	private static final int MAX_KIND_COLS_IN_TEMPLATE = 10;

	@Override
	public void applyAutoSize(@Nonnull Sheet sheet) {
	}

	@Nonnull
	public ExcelMergerDTO toExcelMergerDTO(
		@Nonnull List<MassenversandDataRow> data,
		@Nonnull Locale lang,
		@Nonnull LocalDate datumVon,
		@Nonnull LocalDate datumBis,
		@Nullable Gesuchsperiode auswertungPeriode,
		boolean inklBgGesuche,
		boolean inklMischGesuche,
		boolean inklTsGesuche,
		boolean ohneErneuerungsgesuch,
		@Nullable String text) {
		checkNotNull(data);

		ExcelMergerDTO sheet = new ExcelMergerDTO();
		sheet.addValue(MergeFieldMassenversand.auswertungVon, datumVon);
		sheet.addValue(MergeFieldMassenversand.auswertungBis, datumBis);
		if (auswertungPeriode != null) {
			sheet.addValue(MergeFieldMassenversand.auswertungPeriode, auswertungPeriode.getGesuchsperiodeString());
		}
		sheet.addValue(MergeFieldMassenversand.auswertungInklBgGesuche, inklBgGesuche);
		sheet.addValue(MergeFieldMassenversand.auswertungInklMischGesuche, inklMischGesuche);
		sheet.addValue(MergeFieldMassenversand.auswertungInklTsGesuche, inklTsGesuche);
		sheet.addValue(MergeFieldMassenversand.auswertungOhneFolgegesuch, ohneErneuerungsgesuch);
		sheet.addValue(MergeFieldMassenversand.auswertungText, text);

		insertRequiredColumns(data, sheet);

		data.forEach(dataRow -> {
			ExcelMergerDTO fallRowGroup = sheet.createGroup(MergeFieldMassenversand.repeatRow);
			fallRowGroup.addValue(MergeFieldMassenversand.gesuchsperiode, dataRow.getGesuchsperiode());
			fallRowGroup.addValue(MergeFieldMassenversand.fall, dataRow.getFall());
			fallRowGroup.addValue(MergeFieldMassenversand.gs1Name, dataRow.getGs1Name());
			fallRowGroup.addValue(MergeFieldMassenversand.gs1Vorname, dataRow.getGs1Vorname());
			fallRowGroup.addValue(MergeFieldMassenversand.gs1PersonId, dataRow.getGs1PersonId());
			fallRowGroup.addValue(MergeFieldMassenversand.gs1Mail, dataRow.getGs1Mail());
			fallRowGroup.addValue(MergeFieldMassenversand.gs2Name, dataRow.getGs2Name());
			fallRowGroup.addValue(MergeFieldMassenversand.gs2Vorname, dataRow.getGs2Vorname());
			fallRowGroup.addValue(MergeFieldMassenversand.gs2PersonId, dataRow.getGs2PersonId());
			fallRowGroup.addValue(MergeFieldMassenversand.gs2Mail, dataRow.getGs2Mail());
			fallRowGroup.addValue(MergeFieldMassenversand.adresse, dataRow.getAdresse());
			fallRowGroup.addValue(MergeFieldMassenversand.einreichungsart, dataRow.getEinreichungsart());
			fallRowGroup.addValue(MergeFieldMassenversand.status, dataRow.getStatus());
			fallRowGroup.addValue(MergeFieldMassenversand.typ, dataRow.getTyp());

			dataRow.getKinderCols().forEach(kindCol -> {
				fallRowGroup.addValue(MergeFieldMassenversand.kindName, kindCol.getKindName());
				fallRowGroup.addValue(MergeFieldMassenversand.kindVorname, kindCol.getKindVorname());
				fallRowGroup.addValue(MergeFieldMassenversand.kindGeburtsdatum, kindCol.getKindGeburtsdatum());
				fallRowGroup.addValue(MergeFieldMassenversand.kindDubletten, kindCol.getKindDubletten());
				fallRowGroup.addValue(MergeFieldMassenversand.kindInstitutionKita, kindCol.getKindInstitutionKita());
				fallRowGroup.addValue(MergeFieldMassenversand.kindInstitutionTagi, kindCol.getKindInstitutionTagi());
				fallRowGroup.addValue(MergeFieldMassenversand.kindInstitutionTeKleinkind, kindCol.getKindInstitutionTeKleinkind());
				fallRowGroup.addValue(MergeFieldMassenversand.kindInstitutionTeSchulkind, kindCol.getKindInstitutionTeSchulkind());
				fallRowGroup.addValue(MergeFieldMassenversand.kindInstitutionTagesschule, kindCol.getKindInstitutionTagesschule());
				fallRowGroup.addValue(MergeFieldMassenversand.kindInstitutionFerieninsel, kindCol.getKindInstitutionFerieninsel());
				fallRowGroup.addValue(MergeFieldMassenversand.kindInstitutionenWeitere, kindCol.getKindInstitutionenWeitere());
			});
		});

		return sheet;
	}

	private void insertRequiredColumns(List<MassenversandDataRow> data, ExcelMergerDTO sheet) {
		// Die maximale Anzahl Kinder ermitteln
		int maxKinder = 0;
		for (MassenversandDataRow familie : data) {
			int kinder = familie.getKinderCols().size();
			if (kinder > maxKinder) {
				maxKinder = kinder;
			}
		}
		Preconditions.checkState(
			maxKinder <= MAX_KIND_COLS_IN_TEMPLATE,
			"Es gibt mehr Kinder als Spalten (für Kinder) die im Template zur Verfügung stehen");

		IntStream.range(0, maxKinder).forEach(i -> {
			IntStream.range(0, 11).forEach(j -> {
				// Pro Kind haben wir 11 Spalten
				sheet.addValue(MergeFieldMassenversand.repeatKind, EMPTY_STRING);
			});
		});
	}
}

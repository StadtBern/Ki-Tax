package ch.dvbern.ebegu.reporting.kanton.mitarbeiterinnen;

import ch.dvbern.lib.excelmerger.ExcelConverter;
import ch.dvbern.lib.excelmerger.ExcelMergerDTO;
import org.apache.poi.ss.usermodel.Sheet;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Excel Converter fuer die Statistik von MitarbeiterInnen
 */
@Dependent
public class MitarbeiterinnenExcelConverter  implements ExcelConverter {

	@Override
	public void applyAutoSize(@Nonnull Sheet sheet) {
	}

	@Nonnull
	public ExcelMergerDTO toExcelMergerDTO(@Nonnull List<MitarbeiterinnenDataRow> data, @Nonnull Locale lang, @Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis) {
		checkNotNull(data);

		ExcelMergerDTO sheet = new ExcelMergerDTO();
		sheet.addValue(MergeFieldMitarbeiterinnen.auswertungVon, datumVon);
		sheet.addValue(MergeFieldMitarbeiterinnen.auswertungBis, datumBis);

		data.forEach(dataRow -> {
			ExcelMergerDTO excelRowGroup = sheet.createGroup(MergeFieldMitarbeiterinnen.repeatMitarbeiterinnenRow);
			excelRowGroup.addValue(MergeFieldMitarbeiterinnen.name, dataRow.getName());
			excelRowGroup.addValue(MergeFieldMitarbeiterinnen.vorname, dataRow.getVorname());
			excelRowGroup.addValue(MergeFieldMitarbeiterinnen.verantwortlicheGesuche, dataRow.getVerantwortlicheGesuche());
			excelRowGroup.addValue(MergeFieldMitarbeiterinnen.verfuegungenAusgestellt, dataRow.getVerfuegungenAusgestellt());
		});

		return sheet;
	}
}

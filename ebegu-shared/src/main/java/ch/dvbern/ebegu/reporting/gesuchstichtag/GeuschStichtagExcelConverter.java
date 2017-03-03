/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.reporting.gesuchstichtag;

import ch.dvbern.ebegu.reporting.lib.ExcelConverter;
import ch.dvbern.ebegu.reporting.lib.ExcelMergerDTO;
import org.apache.poi.ss.usermodel.Sheet;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import java.util.List;
import java.util.Locale;

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

/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.reporting.kanton;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;

import ch.dvbern.oss.lib.excelmerger.ExcelConverter;
import ch.dvbern.oss.lib.excelmerger.ExcelMergerDTO;
import org.apache.poi.ss.usermodel.Sheet;

import static com.google.common.base.Preconditions.checkNotNull;

@Dependent
public class KantonExcelConverter implements ExcelConverter {

	@Override
	public void applyAutoSize(@Nonnull Sheet sheet) {
	}

	@Nonnull
	public ExcelMergerDTO toExcelMergerDTO(@Nonnull List<KantonDataRow> data, @Nonnull Locale lang,  @Nonnull LocalDate datumVon,  @Nonnull LocalDate datumBis) {
		checkNotNull(data);

		ExcelMergerDTO sheet = new ExcelMergerDTO();
		sheet.addValue(MergeFieldKanton.auswertungVon, datumVon);
		sheet.addValue(MergeFieldKanton.auswertungBis, datumBis);

		data.forEach(dataRow -> {
			ExcelMergerDTO excelRowGroup = sheet.createGroup(MergeFieldKanton.repeatKantonRow);
			excelRowGroup.addValue(MergeFieldKanton.bgNummer, dataRow.getBgNummer());
			excelRowGroup.addValue(MergeFieldKanton.gesuchId, dataRow.getGesuchId());
			excelRowGroup.addValue(MergeFieldKanton.name, dataRow.getName());
			excelRowGroup.addValue(MergeFieldKanton.vorname, dataRow.getVorname());
			excelRowGroup.addValue(MergeFieldKanton.geburtsdatum, dataRow.getGeburtsdatum());
			excelRowGroup.addValue(MergeFieldKanton.zeitabschnittVon, dataRow.getZeitabschnittVon());
			excelRowGroup.addValue(MergeFieldKanton.zeitabschnittBis, dataRow.getZeitabschnittBis());
			BigDecimal anspruchsPensum = dataRow.getBgPensum();
			excelRowGroup.addValue(MergeFieldKanton.bgPensum, anspruchsPensum);
			if (anspruchsPensum.compareTo(BigDecimal.ZERO) > 0) {
				excelRowGroup.addValue(MergeFieldKanton.elternbeitrag, dataRow.getElternbeitrag());
				excelRowGroup.addValue(MergeFieldKanton.verguenstigung, dataRow.getVerguenstigung());
				excelRowGroup.addValue(MergeFieldKanton.institution, dataRow.getInstitution());
				excelRowGroup.addValue(MergeFieldKanton.betreuungsTyp, dataRow.getBetreuungsTyp());
				excelRowGroup.addValue(MergeFieldKanton.oeffnungstage, dataRow.getOeffnungstage());
			} else {
				excelRowGroup.addValue(MergeFieldKanton.elternbeitrag, BigDecimal.ZERO);
				excelRowGroup.addValue(MergeFieldKanton.verguenstigung, BigDecimal.ZERO);
				excelRowGroup.addValue(MergeFieldKanton.institution, BigDecimal.ZERO);
				excelRowGroup.addValue(MergeFieldKanton.betreuungsTyp, BigDecimal.ZERO);
				excelRowGroup.addValue(MergeFieldKanton.oeffnungstage, BigDecimal.ZERO);
			}
		});

		return sheet;
	}
}

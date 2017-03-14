/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.reporting.gesuchzeitraum;

import ch.dvbern.ebegu.reporting.lib.ExcelConverter;
import ch.dvbern.ebegu.reporting.lib.ExcelMergerDTO;
import org.apache.poi.ss.usermodel.Sheet;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

@Dependent
public class GeuschZeitraumExcelConverter implements ExcelConverter {

	@Override
	public void applyAutoSize(@Nonnull Sheet sheet) {
		sheet.autoSizeColumn(0); // bgNummer
		sheet.autoSizeColumn(1); // institution
		sheet.autoSizeColumn(2); // betreuungsTyp
		sheet.autoSizeColumn(3); // periode
	}

	@Nonnull
	public ExcelMergerDTO toExcelMergerDTO(@Nonnull List<GesuchZeitraumDataRow> data, @Nonnull Locale lang) {
		checkNotNull(data);

		ExcelMergerDTO sheet = new ExcelMergerDTO();

		data.forEach(dataRow -> {
				ExcelMergerDTO excelRowGroup = sheet.createGroup(MergeFieldGesuchZeitraum.repeatGesuchZeitraumRow);
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.bgNummer, dataRow.getBgNummer());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.gesuchLaufNr, dataRow.getGesuchLaufNr());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.institution, dataRow.getInstitution());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.betreuungsTyp, dataRow.getBetreuungsTyp());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.periode, dataRow.getPeriode());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlGesuchOnline, dataRow.getAnzahlGesuchOnline());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlGesuchPapier, dataRow.getAnzahlGesuchPapier());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationOnline, dataRow.getAnzahlMutationOnline());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationPapier, dataRow.getAnzahlMutationPapier());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationAbwesenheit, dataRow.getAnzahlMutationAbwesenheit());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationBetreuung, dataRow.getAnzahlMutationBetreuung());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationDokumente, dataRow.getAnzahlMutationDokumente());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationEV, dataRow.getAnzahlMutationEV());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationEwerbspensum, dataRow.getAnzahlMutationEwerbspensum());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationFamilienSitutation, dataRow.getAnzahlMutationFamilienSitutation());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationFinanzielleSituation, dataRow.getAnzahlMutationFinanzielleSituation());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationFreigabe, dataRow.getAnzahlMutationFreigabe());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationGesuchErstellen, dataRow.getAnzahlMutationGesuchErstellen());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationGesuchsteller, dataRow.getAnzahlMutationGesuchsteller());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationKinder, dataRow.getAnzahlMutationKinder());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationUmzug, dataRow.getAnzahlMutationUmzug());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationVerfuegen, dataRow.getAnzahlMutationVerfuegen());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMahnungen, dataRow.getAnzahlMahnungen());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlBeschwerde, dataRow.getAnzahlBeschwerde());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlVerfuegungen, dataRow.getAnzahlVerfuegungen());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlVerfuegungenNormal, dataRow.getAnzahlVerfuegungenNormal());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlVerfuegungenMaxEinkommen, dataRow.getAnzahlVerfuegungenMaxEinkommen());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlVerfuegungenKeinPensum, dataRow.getAnzahlVerfuegungenKeinPensum());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlVerfuegungenZuschlagZumPensum, dataRow.getAnzahlVerfuegungenZuschlagZumPensum());
				excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlVerfuegungenNichtEintreten, dataRow.getAnzahlVerfuegungenNichtEintreten());
			});

		return sheet;
	}
}

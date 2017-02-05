/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.reporting.gesuchperiode;

import ch.dvbern.ebegu.reporting.gesuchstichtag.MergeFieldGesuchStichtag;
import ch.dvbern.ebegu.reporting.lib.ExcelConverter;
import ch.dvbern.ebegu.reporting.lib.ExcelMergerDTO;
import org.apache.poi.ss.usermodel.Sheet;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import java.util.List;
import java.util.Locale;

import static ch.dvbern.ebegu.reporting.lib.StandardConverters.INTEGER_CONVERTER;
import static com.google.common.base.Preconditions.checkNotNull;

@Dependent
public class GeuschPeriodeExcelConverter implements ExcelConverter {

	@Override
	public void applyAutoSize(@Nonnull Sheet sheet) {
		sheet.autoSizeColumn(0); // bgNummer
		sheet.autoSizeColumn(1); // institution
		sheet.autoSizeColumn(2); // betreuungsTyp
		sheet.autoSizeColumn(3); // periode
	}

	@Nonnull
	public ExcelMergerDTO toExcelMergerDTO(@Nonnull List<GesuchPeriodeDataRow> data, @Nonnull Locale lang) {
		checkNotNull(data);

		ExcelMergerDTO sheet = new ExcelMergerDTO();

		data.forEach(dataRow -> {
				ExcelMergerDTO excelRowGroup = sheet.createGroup(MergeFieldGesuchPeriode.repeatGesuchPeriodeRow);
				excelRowGroup.addValue(MergeFieldGesuchPeriode.bgNummer, dataRow.getBgNummer());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.institution, dataRow.getInstitution());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.betreuungsTyp, dataRow.getBetreuungsTyp());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.periode, dataRow.getPeriode());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlGesuchOnline, dataRow.getAnzahlGesuchOnline());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlGesuchPapier, dataRow.getAnzahlGesuchPapier());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationOnline, dataRow.getAnzahlMutationOnline());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationPapier, dataRow.getAnzahlMutationPapier());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationAbwesenheit, dataRow.getAnzahlMutationAbwesenheit());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationBetreuung, dataRow.getAnzahlMutationBetreuung());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationDokumente, dataRow.getAnzahlMutationDokumente());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationEV, dataRow.getAnzahlMutationEV());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationEwerbspensum, dataRow.getAnzahlMutationEwerbspensum());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationFamilienSitutation, dataRow.getAnzahlMutationFamilienSitutation());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationFinanzielleSituation, dataRow.getAnzahlMutationFinanzielleSituation());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationFreigabe, dataRow.getAnzahlMutationFreigabe());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationGesuchErstellen, dataRow.getAnzahlMutationGesuchErstellen());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationGesuchsteller, dataRow.getAnzahlMutationGesuchsteller());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationKinder, dataRow.getAnzahlMutationKinder());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationUmzug, dataRow.getAnzahlMutationUmzug());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMutationVerfuegen, dataRow.getAnzahlMutationVerfuegen());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlMahnungen, dataRow.getAnzahlMahnungen());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlBeschwerde, dataRow.getAnzahlBeschwerde());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlVerfuegungen, dataRow.getAnzahlVerfuegungen());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlVerfuegungenNormal, dataRow.getAnzahlVerfuegungenNormal());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlVerfuegungenMaxEinkommen, dataRow.getAnzahlVerfuegungenMaxEinkommen());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlVerfuegungenKeinPensum, dataRow.getAnzahlVerfuegungenKeinPensum());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlVerfuegungenZuschlagZumPensum, dataRow.getAnzahlVerfuegungenZuschlagZumPensum());
				excelRowGroup.addValue(MergeFieldGesuchPeriode.anzahlVerfuegungenNichtEintreten, dataRow.getAnzahlVerfuegungenNichtEintreten());
			});

		return sheet;
	}
}

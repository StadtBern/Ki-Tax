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
package ch.dvbern.ebegu.reporting.gesuchzeitraum;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;

import org.apache.poi.ss.usermodel.Sheet;

import ch.dvbern.ebegu.enums.reporting.MergeFieldGesuchZeitraum;
import ch.dvbern.oss.lib.excelmerger.ExcelConverter;
import ch.dvbern.oss.lib.excelmerger.ExcelMergerDTO;

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
			excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationEV, dataRow.getAnzahlMutationEV());
			excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationEwerbspensum, dataRow.getAnzahlMutationEwerbspensum());
			excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationFamilienSitutation, dataRow.getAnzahlMutationFamilienSitutation());
			excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationFinanzielleSituation, dataRow.getAnzahlMutationFinanzielleSituation());
			excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationGesuchsteller, dataRow.getAnzahlMutationGesuchsteller());
			excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationKinder, dataRow.getAnzahlMutationKinder());
			excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMutationUmzug, dataRow.getAnzahlMutationUmzug());
			excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlMahnungen, dataRow.getAnzahlMahnungen());
			excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlSteueramtAusgeloest, dataRow.getAnzahlSteueramtAusgeloest());
			excelRowGroup.addValue(MergeFieldGesuchZeitraum.anzahlSteueramtGeprueft, dataRow.getAnzahlSteueramtGeprueft());
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

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
package ch.dvbern.ebegu.reporting.zahlungauftrag;

import java.util.Collection;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;

import org.apache.poi.ss.usermodel.Sheet;

import ch.dvbern.ebegu.entities.Zahlung;
import ch.dvbern.ebegu.enums.reporting.MergeFieldZahlungAuftragPeriode;
import ch.dvbern.oss.lib.excelmerger.ExcelConverter;
import ch.dvbern.oss.lib.excelmerger.ExcelMergerDTO;

import static com.google.common.base.Preconditions.checkNotNull;

@Dependent
public class ZahlungAuftragPeriodeExcelConverter implements ExcelConverter {

	@Override
	public void applyAutoSize(@Nonnull Sheet sheet) {
		sheet.autoSizeColumn(0); // institution
		sheet.autoSizeColumn(1); // bezahltAm
		sheet.autoSizeColumn(2); // betragCHF
	}

	@Nonnull
	public ExcelMergerDTO toExcelMergerDTO(@Nonnull Collection<Zahlung> data, String gesuchsperiodeString, @Nonnull Locale lang) {
		checkNotNull(data);

		ExcelMergerDTO sheet = new ExcelMergerDTO();

		sheet.addValue(MergeFieldZahlungAuftragPeriode.periode, gesuchsperiodeString);

		data.stream()
			.sorted()
			.forEach(zahlung -> {
				ExcelMergerDTO excelRowGroup = sheet.createGroup(MergeFieldZahlungAuftragPeriode.repeatZahlungAuftragRow);
				excelRowGroup.addValue(MergeFieldZahlungAuftragPeriode.institution, zahlung.getInstitutionStammdaten().getInstitution().getName());
				excelRowGroup.addValue(MergeFieldZahlungAuftragPeriode.bezahltAm, zahlung.getZahlungsauftrag().getDatumFaellig());
				excelRowGroup.addValue(MergeFieldZahlungAuftragPeriode.betragCHF, zahlung.getBetragTotalZahlung());
			});

		return sheet;
	}
}

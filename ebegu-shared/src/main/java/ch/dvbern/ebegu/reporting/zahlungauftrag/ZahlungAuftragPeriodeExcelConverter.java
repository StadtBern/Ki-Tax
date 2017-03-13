/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.reporting.zahlungauftrag;

import ch.dvbern.ebegu.entities.Zahlung;
import ch.dvbern.ebegu.reporting.lib.ExcelConverter;
import ch.dvbern.ebegu.reporting.lib.ExcelMergerDTO;
import org.apache.poi.ss.usermodel.Sheet;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import java.util.Collection;
import java.util.Locale;

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

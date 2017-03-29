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

import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.Zahlung;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.enums.ZahlungspositionStatus;
import ch.dvbern.lib.excelmerger.ExcelConverter;
import ch.dvbern.lib.excelmerger.ExcelMergerDTO;
import org.apache.poi.ss.usermodel.Sheet;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

@Dependent
public class ZahlungAuftragExcelConverter implements ExcelConverter {

	@Override
	public void applyAutoSize(@Nonnull Sheet sheet) {
		sheet.autoSizeColumn(0); // institution
//		sheet.autoSizeColumn(1); // name
		sheet.autoSizeColumn(2); // vorname
		sheet.autoSizeColumn(3); // gebDatum
		sheet.autoSizeColumn(4); // verfuegung
		sheet.autoSizeColumn(5); // vonDatum
		sheet.autoSizeColumn(6); // bisDatum
		sheet.autoSizeColumn(7); // bgPensum
		sheet.autoSizeColumn(8); // betragCHF
	}

	@Nonnull
	public ExcelMergerDTO toExcelMergerDTO(@Nonnull List<Zahlung> data, @Nonnull Locale lang, UserRole userRole, Collection<Institution> allowedInst,
										   String beschrieb, LocalDateTime datumGeneriert, LocalDate datumFaellig) {
		checkNotNull(data);

		ExcelMergerDTO sheet = new ExcelMergerDTO();

		sheet.addValue(MergeFieldZahlungAuftrag.beschrieb, beschrieb);
		sheet.addValue(MergeFieldZahlungAuftrag.generiertAm, datumGeneriert);
		sheet.addValue(MergeFieldZahlungAuftrag.faelligAm, datumFaellig);

		data.stream()
			.filter(zahlung -> {
				// Filtere nur die erlaubten Instituionsdaten
				// User mit der Rolle Institution oder Traegerschaft dürfen nur "Ihre" Institutionsdaten sehen.
				return !(UserRole.SACHBEARBEITER_TRAEGERSCHAFT.equals(userRole) || UserRole.SACHBEARBEITER_INSTITUTION.equals(userRole)) ||
					allowedInst.stream().anyMatch(institution -> institution.getId().equals(zahlung.getInstitutionStammdaten().getInstitution().getId()));
			})
			.forEach(zahlung -> {
				zahlung.getZahlungspositionen().stream()
					.filter(zahlungsposition -> zahlungsposition.getVerfuegungZeitabschnitt().getBgPensum() > 0)
					.sorted()
					.forEach(zahlungsposition -> {
						ExcelMergerDTO excelRowGroup = sheet.createGroup(MergeFieldZahlungAuftrag.repeatZahlungAuftragRow);
						excelRowGroup.addValue(MergeFieldZahlungAuftrag.institution, zahlung.getInstitutionStammdaten().getInstitution().getName());
						excelRowGroup.addValue(MergeFieldZahlungAuftrag.name, zahlungsposition.getKind().getNachname());
						excelRowGroup.addValue(MergeFieldZahlungAuftrag.vorname, zahlungsposition.getKind().getVorname());
						excelRowGroup.addValue(MergeFieldZahlungAuftrag.gebDatum, zahlungsposition.getKind().getGeburtsdatum());
						excelRowGroup.addValue(MergeFieldZahlungAuftrag.verfuegung, zahlungsposition.getVerfuegungZeitabschnitt().getVerfuegung().getBetreuung().getBGNummer());
						excelRowGroup.addValue(MergeFieldZahlungAuftrag.vonDatum, zahlungsposition.getVerfuegungZeitabschnitt().getGueltigkeit().getGueltigAb());
						excelRowGroup.addValue(MergeFieldZahlungAuftrag.bisDatum, zahlungsposition.getVerfuegungZeitabschnitt().getGueltigkeit().getGueltigBis());
						excelRowGroup.addValue(MergeFieldZahlungAuftrag.bgPensum, BigDecimal.valueOf(zahlungsposition.getVerfuegungZeitabschnitt().getBgPensum())
							.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
						excelRowGroup.addValue(MergeFieldZahlungAuftrag.betragCHF, zahlungsposition.getBetrag());
						excelRowGroup.addValue(MergeFieldZahlungAuftrag.isKorrektur, !ZahlungspositionStatus.NORMAL.equals(zahlungsposition.getStatus()));
						excelRowGroup.addValue(MergeFieldZahlungAuftrag.isIgnoriert, zahlungsposition.isIgnoriert());
					});
			});

		return sheet;
	}
}

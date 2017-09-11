package ch.dvbern.ebegu.reporting;

import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GesuchStichtagDataRow;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GesuchZeitraumDataRow;
import ch.dvbern.ebegu.reporting.kanton.KantonDataRow;
import ch.dvbern.ebegu.reporting.kanton.mitarbeiterinnen.MitarbeiterinnenDataRow;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.oss.lib.excelmerger.ExcelMergeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 31/01/2017.
 */
public interface ReportService {

	// Gesuch Stichtag
	@Nonnull
	List<GesuchStichtagDataRow> getReportDataGesuchStichtag(@Nonnull LocalDate date, @Nullable String gesuchPeriodeID)
		throws IOException, URISyntaxException;

	@Nullable
	UploadFileInfo generateExcelReportGesuchStichtag(@Nonnull LocalDate date, @Nullable String gesuchPeriodeID)
		throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;

	// Gesuch Zeitraum

	@Nonnull
	List<GesuchZeitraumDataRow> getReportDataGesuchZeitraum(@Nonnull LocalDate dateVon, @Nonnull LocalDate dateBis, @Nullable String gesuchPeriodeID)
		throws IOException, URISyntaxException;

	@Nullable
	UploadFileInfo generateExcelReportGesuchZeitraum(@Nonnull LocalDate dateVon, @Nonnull LocalDate dateBis, @Nullable String gesuchPeriodeID)
		throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;

	// Kanton
	@Nonnull
	List<KantonDataRow> getReportDataKanton(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis)
		throws IOException, URISyntaxException;

	@Nullable
	UploadFileInfo generateExcelReportKanton(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis)
		throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;

	// MitarbeterInnen
	@Nonnull
	List<MitarbeiterinnenDataRow> getReportMitarbeiterinnen(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis)
		throws IOException, URISyntaxException;

	@Nullable
	UploadFileInfo generateExcelReportMitarbeiterinnen(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis)
		throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;

	// Zahlungen

	@Nullable
	UploadFileInfo generateExcelReportZahlungAuftrag(@Nonnull String auftragId) throws ExcelMergeException;

	@Nullable
	UploadFileInfo generateExcelReportZahlung(@Nonnull String zahlungId) throws ExcelMergeException;

	@Nullable
	UploadFileInfo generateExcelReportZahlungPeriode(@Nonnull String gesuchsperiodeId) throws ExcelMergeException;

	// Gesuchsteller / Kinder / Betreuung
	@Nullable
	UploadFileInfo generateExcelReportGesuchstellerKinderBetreuung(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis, @Nullable String gesuchPeriodeId) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;

	@Nullable
	UploadFileInfo generateExcelReportKinder(@Nonnull LocalDate datumVon, @Nonnull LocalDate datumBis, @Nullable String gesuchPeriodeId) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;

	@Nullable
	UploadFileInfo generateExcelReportGesuchsteller(@Nonnull LocalDate stichtag) throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;
}

package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.reporting.gesuchstichtag.GesuchStichtagDataRow;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GesuchZeitraumDataRow;
import ch.dvbern.ebegu.reporting.lib.ExcelMergeException;
import ch.dvbern.ebegu.util.UploadFileInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
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

	List<GesuchStichtagDataRow> getReportDataGesuchStichtag(@Nonnull LocalDateTime datetime, @Nullable String gesuchPeriodeID)
		throws IOException, URISyntaxException;

	UploadFileInfo generateExcelReportGesuchStichtag(@Nonnull LocalDateTime datetime, @Nullable String gesuchPeriodeID)
		throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;

	List<GesuchZeitraumDataRow> getReportDataGesuchZeitraum(@Nonnull LocalDateTime datetimeVon, @Nonnull LocalDateTime datetimeBis, @Nullable String gesuchPeriodeID)
		throws IOException, URISyntaxException;

	UploadFileInfo generateExcelReportGesuchZeitraum(@Nonnull LocalDateTime datetimeVon, @Nonnull LocalDateTime datetimeBis, @Nullable String gesuchPeriodeID)
		throws ExcelMergeException, IOException, MergeDocException, URISyntaxException;

	UploadFileInfo generateExcelReportZahlungAuftrag(String auftragId) throws ExcelMergeException;

	UploadFileInfo generateExcelReportZahlung(String zahlungId) throws ExcelMergeException;

	UploadFileInfo generateExcelReportZahlungPeriode(String gesuchsperiodeId) throws ExcelMergeException;

}
